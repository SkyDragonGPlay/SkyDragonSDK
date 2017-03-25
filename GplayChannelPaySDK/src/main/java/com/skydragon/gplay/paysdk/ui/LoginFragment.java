package com.skydragon.gplay.paysdk.ui;

import android.content.Context;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.app.Fragment;
import android.text.InputFilter;
import android.text.LoginFilter;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;

import com.skydragon.gplay.paysdk.data.DaoControl;
import com.skydragon.gplay.paysdk.global.SdkUtils;
import com.skydragon.gplay.paysdk.global.ThisApp;
import com.skydragon.gplay.paysdk.tool.CommonInfo;
import com.skydragon.gplay.paysdk.data.GplayUserInner;
import com.skydragon.gplay.paysdk.global.Core;

/**
 * Created by lindaojiang on 2015/12/15.
 */
public class LoginFragment extends Fragment {
    private static final String TAG = "LoginFragment";
    public static final String ARG_HIDE_SWITCH_ACCOUNT = "hideSwitchAccount";
    public static final String ARG_ERROR = "error";
    public static final String ARG_USERNAME= "un";
    public static final String ARG_PASSWORD = "pd";

    private Button buttonBack;
    private EditText editTextUsername;
    private EditText editTextUserPwd;
    private ImageButton doLogin;
    private ImageButton doSkip;
    private ImageButton goRegister;
    private Button doGetPassword;
    private TextView textViewError;

    private static Context appContext;
    private Handler handler;
    private boolean hideSwitch;

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        appContext = context.getApplicationContext();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(CommonInfo.getLayoutId(getContext(), "gplay_paysdk_fragment_login"), container, false);
        buttonBack = (Button)view.findViewById(CommonInfo.getId(getContext(), "back"));
        editTextUsername = (EditText)view.findViewById(CommonInfo.getId(getContext(), "username"));
        editTextUserPwd = (EditText)view.findViewById(CommonInfo.getId(getContext(), "userpwd"));
        doLogin = (ImageButton)view.findViewById(CommonInfo.getId(getContext(), "doLogin"));
        doSkip = (ImageButton)view.findViewById(CommonInfo.getId(getContext(), "doSkip"));
        goRegister = (ImageButton)view.findViewById(CommonInfo.getId(getContext(), "goRegister"));
        doGetPassword = (Button)view.findViewById(CommonInfo.getId(getContext(), "doGetPassword"));
        textViewError = (TextView)view.findViewById(CommonInfo.getId(getContext(), "textViewError"));
        return view;
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        handler = new Handler();

        CommonInfo.EditTextOnFocusChangeListener onFocusChangeListener = new CommonInfo.EditTextOnFocusChangeListener();
        editTextUsername.setOnFocusChangeListener(onFocusChangeListener);
        editTextUserPwd.setOnFocusChangeListener(onFocusChangeListener);
        editTextUsername.setFilters(new InputFilter[]{new LoginFilter.UsernameFilterGeneric(), new InputFilter.LengthFilter(20)});
        editTextUserPwd.setFilters(new InputFilter[]{new LoginFilter.PasswordFilterGMail()});

        doLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String username = CommonInfo.getEditTextValue(editTextUsername);
                if (TextUtils.isEmpty(username)) {
                    editTextUsername.setError(appContext.getString(CommonInfo.getStringId(appContext, "GplayThirdSdk_hintInputUsername")));
                    handler.postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            editTextUsername.setError(null);
                        }
                    }, 3000);
                    editTextUsername.requestFocus();
                    return;
                }
                String password = CommonInfo.getEditTextValue(editTextUserPwd);
                if (TextUtils.isEmpty(password)) {
                    editTextUserPwd.setError(appContext.getString(CommonInfo.getStringId(appContext, "GplayThirdSdk_hintInputPassword")));
                    handler.postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            editTextUserPwd.setError(null);
                        }
                    }, 3000);
                    editTextUserPwd.requestFocus();
                    return;
                }

                loginAction(username, password);
            }
        });

        doSkip.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                trialAccount();
            }
        });

        goRegister.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ((GplayActivity) getActivity()).loadRegisterFragment(null);
            }
        });

        doGetPassword.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {}
        });

        buttonBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ((GplayActivity)getActivity()).goBack(false);
            }
        });

        hideSwitch = false;
        Bundle arg = getArguments();
        if(arg != null) {
            String error = arg.getString(ARG_ERROR);
            String username = arg.getString(ARG_USERNAME);
            String password = arg.getString(ARG_PASSWORD);
            if(!TextUtils.isEmpty(username)) {
                editTextUsername.setText(username);
                arg.remove(ARG_USERNAME); //remove that use once
            }
            if(!TextUtils.isEmpty(password)) {
                editTextUserPwd.setText(password);
                arg.remove(ARG_PASSWORD);
            }
            if(!TextUtils.isEmpty(error)) {
                Core.getInstance().showError(GplayActivity.FRAGMENT_LOGIN, CommonInfo.getId(appContext, "textViewError"), error);
                arg.remove(ARG_ERROR);
            }

            hideSwitch = arg.getBoolean(ARG_HIDE_SWITCH_ACCOUNT, false);
        }

        if(savedInstanceState != null) {
            hideSwitch = savedInstanceState.getBoolean("hideSwitch", hideSwitch);
        }
    }

    @Override
    public void onResume() {
        super.onResume();

        if(!hideSwitch) {
            hideSwitch = true;

            //get latest one login user
            final GplayUserInner latestUser = DaoControl.getInstance().getlatestRegisteredUser(appContext);
            if (latestUser != null) {
                CustomDialogFragment.showDialog(getFragmentManager(), latestUser.getUsername(), appContext.getString(CommonInfo.getStringId(appContext, "GplayThirdSdk_actionAutoLogin")), appContext.getString(CommonInfo.getStringId(appContext, "GplayThirdSdk_switchAccount")), new CustomDialogFragment.Timing() {
                    @Override
                    public boolean onSecond(CustomDialogFragment dialog, int second) {
                        if (ThisApp.isDebug)
                            Log.d(TAG, "dialog timing " + second);

                        //auto login after 3s
                        if (second >= 3) {
                            //hide switch account button
                            dialog.setCancelButton(null);
                            dialog.setHint(appContext.getString(CommonInfo.getStringId(appContext, "GplayThirdSdk_actionLogin")));

                            //valid access token
                            if (latestUser.getExpiresAt() > System.currentTimeMillis() && !TextUtils.isEmpty(latestUser.getAccessToken())) {
                                SdkUtils.quickLoginValidUser(appContext, latestUser);
                                dialog.dismiss();
                                GplayActivity.exitActivity();
                            } else { //access token has expires, goto refresh token
                                SdkUtils.refreshToken(latestUser, appContext, new SdkUtils.GplayHttpsResult() {
                                    @Override
                                    public void onSuccess() {
                                        CustomDialogFragment.dismissInstance();
                                        GplayActivity.exitActivity();
                                    }

                                    @Override
                                    public void onFailure(String error) {
                                        CustomDialogFragment.dismissInstance();
                                        editTextUsername.setText(latestUser.getUsername());
                                        Core.getInstance().showError(GplayActivity.FRAGMENT_LOGIN, CommonInfo.getId(appContext, "textViewError"), error);
                                    }
                                });
                            }
                            return false;
                        }
                        return true;
                    }
                });
            }
        }
    }

    @Override
    public void onPause() {
        super.onPause();
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putBoolean("hideSwitch", hideSwitch);
    }

    private void loginAction(final String username, final String password){
        GplayUserInner user = DaoControl.getInstance().findUser(appContext, username);
        if(user == null){
            //not a trial account, has registered
            user = new GplayUserInner(null, username, null, null, false, null, null, null, null, 0L, 0L, 0L, false);
        }

        //show login dialog
        CustomDialogFragment.showDialog(getFragmentManager(), username,
                appContext.getString(CommonInfo.getStringId(appContext, "GplayThirdSdk_actionLogin")), null, null);

        SdkUtils.getAccessToken(user, password, appContext, new SdkUtils.GplayHttpsResult() {
            @Override
            public void onSuccess() {
                CustomDialogFragment.dismissInstance();
                GplayActivity.exitActivity();
            }

            @Override
            public void onFailure(String error) {
                CustomDialogFragment.dismissInstance();
                Core.getInstance().showError(GplayActivity.FRAGMENT_LOGIN, CommonInfo.getId(appContext, "textViewError"), error);
            }
        });
    }

    private void trialAccount(){
        GplayUserInner user = DaoControl.getInstance().getlatestTrialUser(appContext);
        if(user != null) { //has trial account
            SdkUtils.quickLoginValidUser(appContext, user);
            ((GplayActivity) getActivity()).goBack(true);
        }
        else { //get a trial account
            final GplayUserInner newUser = new GplayUserInner(null, null, null, null, true, null, null, null, null, 0L, 0L, 0L, false);

            //show wait dialog
            CustomDialogFragment.showDialog(getFragmentManager(), appContext.getString(CommonInfo.getStringId(appContext, "GplayThirdSdk_preparing")), null, null, null);

            //http get a trial account
            SdkUtils.getNewUser(newUser, appContext, new SdkUtils.GplayHttpsResult() {
                @Override
                public void onSuccess() {
                    CustomDialogFragment.dismissInstance();
                    GplayActivity.exitActivity();
                }

                @Override
                public void onFailure(String error) {
                    //try again
                    handler.postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            SdkUtils.getNewUser(newUser, appContext, new SdkUtils.GplayHttpsResult() {
                                @Override
                                public void onSuccess() {
                                    CustomDialogFragment.dismissInstance();
                                    GplayActivity.exitActivity();
                                }

                                @Override
                                public void onFailure(String error) {
                                    CustomDialogFragment.dismissInstance();
                                    Core.getInstance().showError(GplayActivity.FRAGMENT_LOGIN, CommonInfo.getId(appContext, "textViewError"), error);
                                }
                            });
                        }
                    }, 2000);
                }
            });
        }
    }
}