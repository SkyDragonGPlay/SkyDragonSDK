package com.skydragon.gplay.paysdk.ui;

import android.content.Context;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v4.app.Fragment;
import android.text.InputFilter;
import android.text.LoginFilter;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.skydragon.gplay.paysdk.data.DaoControl;
import com.skydragon.gplay.paysdk.global.SdkUtils;
import com.skydragon.gplay.paysdk.data.GplayUserInner;
import com.skydragon.gplay.paysdk.global.Core;
import com.skydragon.gplay.paysdk.tool.CommonInfo;

/**
 * Created by lindaojiang on 2015/12/15.
 */
public class RegisterFragment extends Fragment {
    private static final String TAG = "RegisterFragment";

    private Button buttonBack;
    private EditText editTextUsername;
    private EditText editTextUserPwd;
    private EditText editTextPhone;
    private EditText editTextCode;
    private ImageButton imageButtonGetCode;
    private TextView textViewSendCodeAgain;
    private ImageButton imageButtonCommit;
    private ImageButton imageButtondoRegister;
    private ImageButton iamgeButtonGoLogin;
    private LinearLayout layoutDo;
    private TextView textViewError;

    private static Context appContext;
    private Handler handler;
    private String verifyCodeSerial;
    private int seconds;
    private static boolean showLogin = false;

    private static final int SEND_MSG_NONE = 0;
    private static final int SEND_MSG_GO_LOGIN = 1;
    private static int sendMsg = SEND_MSG_NONE;
    private static Bundle sendBundle = null;

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        appContext = context.getApplicationContext();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(CommonInfo.getLayoutId(getContext(), "gplay_paysdk_fragment_register"), container, false);
        buttonBack = (Button)view.findViewById(CommonInfo.getId(getContext(), "back"));
        editTextUsername = (EditText)view.findViewById(CommonInfo.getId(getContext(), "username"));
        editTextUserPwd = (EditText)view.findViewById(CommonInfo.getId(getContext(), "userpwd"));
        editTextPhone = (EditText)view.findViewById(CommonInfo.getId(getContext(), "phone"));
        editTextCode = (EditText)view.findViewById(CommonInfo.getId(getContext(), "code"));
        imageButtonGetCode = (ImageButton)view.findViewById(CommonInfo.getId(getContext(), "getCode"));
        textViewSendCodeAgain = (TextView)view.findViewById(CommonInfo.getId(getContext(), "sendCodeAgain"));
        imageButtonCommit = (ImageButton)view.findViewById(CommonInfo.getId(getContext(), "commit"));
        imageButtondoRegister = (ImageButton)view.findViewById(CommonInfo.getId(getContext(), "doRegister"));
        iamgeButtonGoLogin = (ImageButton)view.findViewById(CommonInfo.getId(getContext(), "goLogin"));
        layoutDo = (LinearLayout)view.findViewById(CommonInfo.getId(getContext(), "layoutDo"));
        textViewError = (TextView)view.findViewById(CommonInfo.getId(getContext(), "textViewError"));
        return view;
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        handler = new RegisterHandler();

        CommonInfo.EditTextOnFocusChangeListener onFocusChangeListener = new CommonInfo.EditTextOnFocusChangeListener();
        editTextUsername.setOnFocusChangeListener(onFocusChangeListener);
        editTextUserPwd.setOnFocusChangeListener(onFocusChangeListener);
        editTextPhone.setOnFocusChangeListener(onFocusChangeListener);
        editTextCode.setOnFocusChangeListener(onFocusChangeListener);
        editTextUsername.setFilters(new InputFilter[]{new LoginFilter.UsernameFilterGeneric(), new InputFilter.LengthFilter(20)});
        editTextUserPwd.setFilters(new InputFilter[]{new LoginFilter.PasswordFilterGMail()});

        imageButtonGetCode.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String phone = CommonInfo.getEditTextValue(editTextPhone);
                if (TextUtils.isEmpty(phone)) {
                    editTextPhone.setError(appContext.getString(CommonInfo.getStringId(appContext, "GplayThirdSdk_hintInputPhone")));
                    handler.postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            editTextPhone.setError(null);
                        }
                    }, 3000);
                    editTextPhone.requestFocus();
                    return;
                }

                SdkUtils.getCode(phone, appContext, new SdkUtils.GplayHttpsResult1() {
                    @Override
                    public void onSuccess(String data) {
                        verifyCodeSerial = data;
                        textViewSendCodeAgain.setVisibility(View.VISIBLE);
                        imageButtonGetCode.setVisibility(View.GONE);
                        seconds = 60; //60s send again
                        Runnable runnable = new Runnable() {
                            @Override
                            public void run() {
                                seconds--;
                                if (seconds < 1) {
                                    textViewSendCodeAgain.setVisibility(View.GONE);
                                    imageButtonGetCode.setVisibility(View.VISIBLE);
                                } else {
                                    textViewSendCodeAgain.setText(String.format("%ds", seconds));
                                    handler.postDelayed(this, 1000);
                                }
                            }
                        };
                        handler.post(runnable);
                    }

                    @Override
                    public void onFailure(String error) {
                        Core.getInstance().showError(GplayActivity.FRAGMENT_REGISTER, CommonInfo.getId(appContext, "textViewError"), error);
                    }
                });
            }
        });

        imageButtonCommit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                registerAction();
            }
        });

        imageButtondoRegister.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                registerAction();
            }
        });

        iamgeButtonGoLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                GplayActivity activity = (GplayActivity)getActivity();
                activity.unloadLastFragment();
                activity.loadLoginFragment(null);
            }
        });

        buttonBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ((GplayActivity)getActivity()).goBack(false);
            }
        });

        Bundle bundle = getArguments();
        if(bundle != null) {
            showLogin = bundle.getBoolean("showLogin", false);
            if(showLogin){
                imageButtonCommit.setVisibility(View.GONE);
                layoutDo.setVisibility(View.VISIBLE);
            }
        }

        verifyCodeSerial = null;
        seconds = 0;
        if(savedInstanceState != null){
            verifyCodeSerial = savedInstanceState.getString("verifyCodeSerial");
            seconds = savedInstanceState.getInt("seconds");
        }
    }

    @Override
    public void onResume() {
        super.onResume();

        if(sendMsg != SEND_MSG_NONE) {
            handler.sendEmptyMessage(sendMsg);
        }
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        if(!TextUtils.isEmpty(verifyCodeSerial))
            outState.putString("verifyCodeSerial", verifyCodeSerial);
        outState.putInt("seconds", seconds);
    }

    @Override
    public void onPause() {
        super.onPause();
    }

    private void registerAction(){
        final String username = CommonInfo.getEditTextValue(editTextUsername);
        if(TextUtils.isEmpty(username) || username.length() < 4){
            editTextUsername.setError(appContext.getString(CommonInfo.getStringId(appContext, "GplayThirdSdk_hintNewUsername")));
            handler.postDelayed(new Runnable() {
                @Override
                public void run() {
                    editTextUsername.setError(null);
                }
            }, 3000);
            editTextUsername.requestFocus();
            return;
        }

        final String password = CommonInfo.getEditTextValue(editTextUserPwd);
        if(TextUtils.isEmpty(password) || password.length() < 4){
            editTextUserPwd.setError(appContext.getString(CommonInfo.getStringId(appContext, "GplayThirdSdk_hintNewPassword")));
            handler.postDelayed(new Runnable() {
                @Override
                public void run() {
                    editTextUserPwd.setError(null);
                }
            }, 3000);
            editTextUserPwd.requestFocus();
            return;
        }

        final String phone = CommonInfo.getEditTextValue(editTextPhone);
        String code = null;
        if(!TextUtils.isEmpty(phone)) {
            code = CommonInfo.getEditTextValue(editTextCode);
            if(TextUtils.isEmpty(code) || code.length() < 4){
                editTextCode.setError(appContext.getString(CommonInfo.getStringId(appContext, "GplayThirdSdk_hintInputCode")));
                handler.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        editTextCode.setError(null);
                    }
                }, 3000);
                editTextCode.requestFocus();
                return;
            }
        }

        //show register and login dialog
        CustomDialogFragment.showDialog(getFragmentManager(), username, appContext.getString(CommonInfo.getStringId(appContext, "GplayThirdSdk_actionRegister")), null, null);

        //get trial user id set for this username
        GplayUserInner user = DaoControl.getInstance().getlatestTrialUser(appContext);
        String uid = null;
        if(user != null) {
            uid = user.getUid();
        }
        else {
            user = new GplayUserInner(null, username, null, phone, false, null, null, null, null, 0L, 0L, 0L, true);
        }
        final GplayUserInner newUser = user;

        SdkUtils.register(username, uid, password, phone, code, verifyCodeSerial, appContext, new SdkUtils.GplayHttpsResult() {
            @Override
            public void onSuccess() {
                //save new user
                if (newUser.getId() != null) { //local stored trial account
                    newUser.setUsername(username); //change trial username
                    newUser.setPhone(phone);
                    newUser.setIsTrial(false); //change trial
                    newUser.setIsLoaded(true);
                    DaoControl.getInstance().put(appContext, newUser);
                } //else insert new user after login

                //login
                CustomDialogFragment dialog = CustomDialogFragment.getInstance();
                if(dialog != null)
                    dialog.setHint(appContext.getString(CommonInfo.getStringId(appContext, "GplayThirdSdk_actionLogin")));
                SdkUtils.getAccessToken(newUser, password, appContext, new SdkUtils.GplayHttpsResult() {
                    @Override
                    public void onSuccess() {
                        CustomDialogFragment.dismissInstance();
                        GplayActivity.exitActivity();
                    }

                    @Override
                    public void onFailure(String error) {
                        CustomDialogFragment.dismissInstance();

                        sendBundle = new Bundle();
                        sendBundle.putString(LoginFragment.ARG_ERROR, appContext.getString(CommonInfo.getStringId(appContext, "GplayThirdSdk_errorRegisterLogin"))); //login page show error
                        sendBundle.putString(LoginFragment.ARG_USERNAME, username);
                        sendBundle.putString(LoginFragment.ARG_PASSWORD, password);
                        sendBundle.putBoolean(LoginFragment.ARG_HIDE_SWITCH_ACCOUNT, true);
                        sendMsg = SEND_MSG_GO_LOGIN;
                        handler.sendEmptyMessage(sendMsg);
                    }
                });
            }

            @Override
            public void onFailure(String error) {
                CustomDialogFragment.dismissInstance();
                Core.getInstance().showError(GplayActivity.FRAGMENT_REGISTER, CommonInfo.getId(appContext, "textViewError"), error);
            }
        });
    }

    private static class RegisterHandler extends Handler {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            switch (msg.what) {
                case SEND_MSG_GO_LOGIN:
                {
                    synchronized (RegisterFragment.class) { //lock for receive twice
                        if (Core.getGplayActivity() != null && sendMsg == SEND_MSG_GO_LOGIN) {
                            GplayActivity activity = Core.getGplayActivity();
                            if (!showLogin) { //from login page, pop resister fragment and last login fragment
                                activity.unloadLastFragment();
                            }
                            activity.unloadLastFragment();
                            activity.loadLoginFragment(sendBundle);
                            sendMsg = SEND_MSG_NONE;
                        }
                    }
                    break;
                }
                default:
                    break;
            }
        }
    }
}