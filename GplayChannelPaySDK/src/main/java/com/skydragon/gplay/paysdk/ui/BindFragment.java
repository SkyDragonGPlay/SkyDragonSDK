package com.skydragon.gplay.paysdk.ui;

import android.content.Context;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.app.Fragment;
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
import com.skydragon.gplay.paysdk.tool.CommonInfo;
import com.skydragon.gplay.paysdk.data.GplayUserInner;
import com.skydragon.gplay.paysdk.global.Core;

/**
 * Created by lindaojiang on 2015/12/15.
 */
public class BindFragment extends Fragment {
    private static final String TAG = "BindFragment";

    private Button buttonBack;
    private TextView textViewUsername;
    private LinearLayout layoutPwd;
    private EditText editTextUserPwd;
    private EditText editTextPhone;
    private EditText editTextCode;
    private ImageButton imageButtonGetCode;
    private TextView textViewSendCodeAgain;
    private ImageButton imageButtonCommit;
    private TextView textViewError;

    private static Context appContext;
    private Handler handler;
    private GplayUserInner user;
    private String verifyCodeSerial;
    private int seconds;

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        appContext = context.getApplicationContext();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(CommonInfo.getLayoutId(getContext(), "gplay_paysdk_fragment_bind"), container, false);
        buttonBack = (Button)view.findViewById(CommonInfo.getId(getContext(), "back"));
        textViewUsername = (TextView)view.findViewById(CommonInfo.getId(getContext(), "username"));
        layoutPwd = (LinearLayout)view.findViewById(CommonInfo.getId(getContext(), "layoutPwd"));
        editTextUserPwd = (EditText)view.findViewById(CommonInfo.getId(getContext(), "userpwd"));
        editTextPhone = (EditText)view.findViewById(CommonInfo.getId(getContext(), "phone"));
        editTextCode = (EditText)view.findViewById(CommonInfo.getId(getContext(), "code"));
        imageButtonGetCode = (ImageButton)view.findViewById(CommonInfo.getId(getContext(), "getCode"));
        textViewSendCodeAgain = (TextView)view.findViewById(CommonInfo.getId(getContext(), "sendCodeAgain"));
        imageButtonCommit = (ImageButton)view.findViewById(CommonInfo.getId(getContext(), "commit"));
        textViewError = (TextView)view.findViewById(CommonInfo.getId(getContext(), "textViewError"));
        return view;
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        handler = new Handler();

        user = DaoControl.getInstance().getlatestUser(appContext);
        if(user == null || user.getIsTrial() || !TextUtils.isEmpty(user.getPhone())){
            //set data and finish
            ((GplayActivity)getActivity()).goBack(false);
            return;
        }

        CommonInfo.EditTextOnFocusChangeListener onFocusChangeListener = new CommonInfo.EditTextOnFocusChangeListener();
        editTextUserPwd.setOnFocusChangeListener(onFocusChangeListener);
        editTextPhone.setOnFocusChangeListener(onFocusChangeListener);
        editTextCode.setOnFocusChangeListener(onFocusChangeListener);

        buttonBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ((GplayActivity) getActivity()).goBack(false);
            }
        });

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
                        Core.getInstance().showError(GplayActivity.FRAGMENT_BIND, CommonInfo.getId(appContext, "textViewError"), error);
                    }
                });
            }
        });

        imageButtonCommit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                bindAction();
            }
        });

        textViewUsername.setText(user.getUsername());
        verifyCodeSerial = null;
        if(savedInstanceState != null){
            verifyCodeSerial = savedInstanceState.getString("verifyCodeSerial");
            seconds = savedInstanceState.getInt("seconds");
        }
    }

    @Override
    public void onResume() {
        super.onResume();

        //access token expires need password login, ignore refresh token
        if(!TextUtils.isEmpty(user.getAccessToken()) && user.getExpiresAt() > System.currentTimeMillis()){
            layoutPwd.setVisibility(View.GONE);
        }
        else {
            layoutPwd.setVisibility(View.VISIBLE);
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

    private void bindAction(){
        String password = null;
        if(layoutPwd.getVisibility() == View.VISIBLE) {
            password = CommonInfo.getEditTextValue(editTextUserPwd);
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
        }

        final String phone = CommonInfo.getEditTextValue(editTextPhone);
        if(TextUtils.isEmpty(phone)){
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

        final String code = CommonInfo.getEditTextValue(editTextCode);
        if(TextUtils.isEmpty(code)){
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

        //access token expires need password login, ignore refresh token
        if(!TextUtils.isEmpty(password)){
            //show login dialog
            CustomDialogFragment.showDialog(getFragmentManager(), user.getUsername(), appContext.getString(CommonInfo.getStringId(appContext, "GplayThirdSdk_actionLogin")), null, null);


            SdkUtils.getAccessToken(user, password, appContext, new SdkUtils.GplayHttpsResult() {
                @Override
                public void onSuccess() {
                    CustomDialogFragment dialog = CustomDialogFragment.getInstance();
                    if(dialog != null)
                        dialog.setHint(appContext.getString(CommonInfo.getStringId(appContext, "GplayThirdSdk_actionBind")));
                    doBind(phone, code);
                }

                @Override
                public void onFailure(String error) {
                    CustomDialogFragment.dismissInstance();
                    Core.getInstance().showError(GplayActivity.FRAGMENT_BIND, CommonInfo.getId(appContext, "textViewError"), error);
                }
            });
        }
        else {
            //show bind dialog
            CustomDialogFragment.showDialog(getFragmentManager(), user.getUsername(), appContext.getString(CommonInfo.getStringId(appContext, "GplayThirdSdk_actionBind")), null, null);
            doBind(phone, code);
        }
    }

    private void doBind(final String phone, final String code){
        SdkUtils.bind(user, phone, code, verifyCodeSerial, appContext, new SdkUtils.GplayHttpsResult() {
            @Override
            public void onSuccess() {
                CustomDialogFragment.dismissInstance();
                GplayActivity.exitActivity();
            }

            @Override
            public void onFailure(String error) {
                CustomDialogFragment.dismissInstance();
                Core.getInstance().showError(GplayActivity.FRAGMENT_BIND, CommonInfo.getId(appContext, "textViewError"), error);
            }
        });
    }
}