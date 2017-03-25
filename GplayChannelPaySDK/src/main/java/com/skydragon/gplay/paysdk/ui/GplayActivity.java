package com.skydragon.gplay.paysdk.ui;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.text.TextUtils;
import android.util.Log;

import com.skydragon.gplay.paysdk.OAuthData;
import com.skydragon.gplay.paysdk.data.DaoControl;
import com.skydragon.gplay.paysdk.GplayPaySDK;
import com.skydragon.gplay.paysdk.PayData;
import com.skydragon.gplay.paysdk.data.GplayUserInner;
import com.skydragon.gplay.paysdk.global.Core;
import com.skydragon.gplay.paysdk.global.ThisApp;
import com.skydragon.gplay.paysdk.tool.CommonInfo;

/**
 * Created by lindaojiang on 2015/12/11.
 */
public class GplayActivity extends FragmentActivity {
    private static final String TAG = "GplayActivity";
    public static final String FRAGMENT_LOGIN = "login";
    public static final String FRAGMENT_REGISTER = "register";
    public static final String FRAGMENT_BIND = "bind";
    public static final String FRAGMENT_PAY = "pay";
    public static final String FRAGMENT_PAY_FAIL = "pay_fail";
    public static final String FRAGMENT_PAY_SUCCESS = "pay_success";
    public static final String FRAGMENT_DIALOG ="dialog";
    private static boolean needFinish = false;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(CommonInfo.getLayoutId(this, "gplay_paysdk_activity_gplay"));

        if(savedInstanceState == null) {
            needFinish = false;
            String action = null;
            if(getIntent() != null)
                action = getIntent().getStringExtra("gg");
            if(!TextUtils.isEmpty(action) && action.contentEquals("bind")){
                //get latest one login user
                GplayUserInner user = DaoControl.getInstance().getlatestUser(this);
                if(user != null && !user.getIsTrial()) {
                    if(TextUtils.isEmpty(user.getPhone())) {
                        //show bind page if a registered account has not bound mobile phone;
                        loadBindFragment(null);
                    }
                    else {
                        //return immediately if a registered account has a phone
                        goBack(false); //set data and finish
                    }
                }
                else { //show register page if account is trial or no account
                    Bundle bundle = new Bundle();
                    bundle.putBoolean("showLogin", true);
                    loadRegisterFragment(bundle);
                }
            }
            else if(!TextUtils.isEmpty(action) && action.contentEquals("register")){
                Bundle bundle = new Bundle();
                bundle.putBoolean("showLogin", true);
                loadRegisterFragment(bundle);
            }
            else if(!TextUtils.isEmpty(action) && action.contentEquals("login")) {
                loadLoginFragment(null);
            }
            else if(!TextUtils.isEmpty(action) && action.contentEquals("pay")) {
                Bundle bundle = null;
                if(getIntent() != null) {
                    bundle = getIntent().getBundleExtra("arg");
                }
                loadPayFragment(bundle);
            }
            else {
                goBack(false);
            }
        }

        if(needFinish) {
            goBack(true);
        }
    }

    @Override
    public void onBackPressed() {
        goBack(false);
    }

    @Override
    protected void onResume() {
        super.onResume();
        Core.setGplayActivity(this);
        if(needFinish) {
            goBack(true);
        }
    }

    @Override
    protected void onPause() {
        Core.setGplayActivity(null); //set before onSaveInstanceState
        super.onPause();
    }

    public void loadLoginFragment(Bundle arg){
        LoginFragment fragment = new LoginFragment();
        if(arg != null)
            fragment.setArguments(arg);
        getSupportFragmentManager().beginTransaction().add(CommonInfo.getId(GplayActivity.this, "container"), fragment, FRAGMENT_LOGIN).addToBackStack(null).commit();
    }

    public void loadRegisterFragment(Bundle arg){
        RegisterFragment fragment = new RegisterFragment();
        if(arg != null)
            fragment.setArguments(arg);
        getSupportFragmentManager().beginTransaction().add(CommonInfo.getId(GplayActivity.this, "container"), fragment, FRAGMENT_REGISTER).addToBackStack(null).commit();
    }

    public void loadBindFragment(Bundle arg){
        BindFragment fragment = new BindFragment();
        if(arg != null)
            fragment.setArguments(arg);
        getSupportFragmentManager().beginTransaction().add(CommonInfo.getId(GplayActivity.this, "container"), fragment, FRAGMENT_BIND).addToBackStack(null).commit();
    }

    public void loadPayFragment(Bundle arg){
        PayFragment fragment = new PayFragment();
        if(arg != null)
            fragment.setArguments(arg);
        getSupportFragmentManager().beginTransaction().add(CommonInfo.getId(GplayActivity.this, "container"), fragment, FRAGMENT_PAY).addToBackStack(null).commit();
    }

    public void loadPayFailFragment(Bundle arg){
        PayFailFragment fragment = new PayFailFragment();
        if(arg != null)
            fragment.setArguments(arg);
        getSupportFragmentManager().beginTransaction().add(CommonInfo.getId(GplayActivity.this, "container"), fragment, FRAGMENT_PAY_FAIL).addToBackStack(null).commit();
    }

    public void loadPaySucessFragment(Bundle arg){
        PaySuccessFragment fragment = new PaySuccessFragment();
        if(arg != null)
            fragment.setArguments(arg);
        getSupportFragmentManager().beginTransaction().add(CommonInfo.getId(GplayActivity.this, "container"), fragment, FRAGMENT_PAY_SUCCESS).addToBackStack(null).commit();
    }

    public void unloadLastFragment(){
        getSupportFragmentManager().popBackStackImmediate();
    }

    public void goBack(boolean activityFinish){
        //pop fragment
        if (!activityFinish && getSupportFragmentManager().getBackStackEntryCount() > 1) {
            getSupportFragmentManager().popBackStack();
            return;
        }

        String action = null;
        if(getIntent() != null)
            action = getIntent().getStringExtra("gg");
        if(!TextUtils.isEmpty(action) && action.contentEquals("pay")) {
            if(Core.getInstance().getPayData() == null) {
                PayData payData = new PayData();
                payData.setResultCode(PayData.RESULT_CODE_CANCEL);
                if (getIntent() != null) {
                    Bundle arg = getIntent().getBundleExtra("arg");
                    payData.setOrder(arg.getString("order"));
                    payData.setAmount(arg.getString("amount"));
                    payData.setName(arg.getString("name"));
                    payData.setDesc(arg.getString("desc"));
                    payData.setExtra(arg.getString("extra"));
                }
                Core.getInstance().setPayData(payData);
            }
            setResult(Activity.RESULT_OK, new Intent().putExtra(GplayPaySDK.RESULT_DATA_PAY, Core.getInstance().getPayData()));
        }
        else { //oauth
            if (Core.getInstance().getAuthData() == null) {
                OAuthData authData = new OAuthData();
                authData.setResultCode(OAuthData.RESULT_CODE_CANCEL);
                Core.getInstance().setAuthData(authData);
            }
            setResult(Activity.RESULT_OK, new Intent().putExtra(GplayPaySDK.RESULT_DATA_AUTH, Core.getInstance().getAuthData()));
        }
        needFinish = false;
        finish();
    }

    public static void exitActivity() {
        if(Core.getGplayActivity() != null) {
            Core.getGplayActivity().goBack(true);
        }
        else {
            Core.getInstance().getHandler().postDelayed(new Runnable() {
                @Override
                public void run() {
                    if(Core.getGplayActivity() != null) {
                        Core.getGplayActivity().goBack(true);
                    }
                    else {
                        needFinish = true;
                        if(ThisApp.isDebug)
                            Log.d(TAG, "setNeedFinish");
                    }
                }
            }, 800); //run once after 800ms
        }
    }
}