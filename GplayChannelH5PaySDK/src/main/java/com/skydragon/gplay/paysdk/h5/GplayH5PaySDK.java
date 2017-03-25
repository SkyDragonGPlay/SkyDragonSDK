package com.skydragon.gplay.paysdk.h5;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

import com.skydragon.gplay.paysdk.h5.common.SDKConstant;
import com.skydragon.gplay.paysdk.h5.model.OrderInfo;
import com.skydragon.gplay.paysdk.h5.model.PayData;
import com.skydragon.gplay.paysdk.h5.model.UserInfo;
import com.skydragon.gplay.paysdk.h5.model.OAuthData;
import com.skydragon.gplay.paysdk.h5.model.SdkEnvirons;
import com.skydragon.gplay.paysdk.h5.controller.HybridJSInterface;

/**
 * package : com.skydragon.hybridsdk
 *
 * Description :
 *
 * @author Y.J.ZHOU
 * @date 2016/4/11 20:37.
 */
public class GplayH5PaySDK {
    public static final String METHOD_EXEC = "METHOD_EXEC";

    public static final int METHOD_LOGIN = 1;
    public static final int METHOD_REGISTER = 2;
    public static final int METHOD_PAY = 3;
    public static final int METHOD_BIND = 4;
    public static final int METHOD_REFRESH_TOKEN = 5;

    private static final long EXPIRED_TIME = 30 * 60 * 1000;
    /**
     * sdk init before call other function
     *
     * @param context   application context or activity context
     * @param appId     app id
     * @param appSecret app secret
     */
    public void init(Context context, String appId, String appSecret) {
        SdkEnvirons.getInstances().init(context, appId, appSecret);
    }

    /**
     * show login page first, register page second
     * login: show switch account 3s, then auto login or input account and password
     *
     * @param activity Activity or FragmentActivity
     * @param response callback
     */
    public void login(final Activity activity, final OAuthResponse response) {
        UserInfo userinfo = SdkEnvirons.getInstances().getUserInfo();
        long nowTime = System.currentTimeMillis();

        if(userinfo != null
                && userinfo.isLogin()
                && response != null){

            long loginTime = userinfo.getTokenInfo().getLoginTime();

            if (nowTime - loginTime < EXPIRED_TIME){
                OAuthData oAuthData = new OAuthData();
                oAuthData.setResultCode(OAuthData.RESULT_CODE_SUCCESS);
                oAuthData.setIsTrial(userinfo.getIsTrial());
                oAuthData.setUid(userinfo.getUid());
                oAuthData.setErrorDescription("账号已经登陆！");

                response.onResponse(oAuthData);
                return;
            }
        }

        SdkEnvirons.getInstances().registerAuthResponse(response);

        if(userinfo != null
                && userinfo.getTokenInfo() != null
                && nowTime < userinfo.getTokenInfo().getExpireAt()){
            refreshToken(activity, new OnRefreshBackListener() {
                @Override
                public void onRefreshCallbackFailed() {
                    exeCmd2Activity(activity, METHOD_LOGIN);
                }
            });
        } else {
            exeCmd2Activity(activity, METHOD_LOGIN);
        }
    }

    private void exeCmd2Activity(Activity act, int cmd){
        Intent intent = new Intent(act, GplayH5Activity.class);
        intent.putExtra(METHOD_EXEC, cmd);
        act.startActivity(intent);
    }

    private void refreshToken(final Activity activity, final OnRefreshBackListener listener){
        SdkEnvirons.getInstances().registerLoginCallbackListener(new GplayH5Activity.OnLoginCallbackListener() {
            @Override
            public void onLoginCallback(int statusCode, String message, String token) {

                GplayH5PaySDK.OAuthResponse response = SdkEnvirons.getInstances().getoAuthResponse();
                switch (statusCode){
                    case HybridJSInterface.STATUS_SUCCESS:
                        // refresh token success !
                        UserInfo userInfo = SdkEnvirons.getInstances().getUserInfo();

                        if(response != null && userInfo != null){
                            OAuthData oAuthData = new OAuthData();
                            oAuthData.setIsTrial(userInfo.getIsTrial());
                            oAuthData.setUid(userInfo.getUid());
                            oAuthData.setErrorDescription(message);
                            oAuthData.setResultCode(OAuthData.RESULT_CODE_SUCCESS);
                            response.onResponse(oAuthData);
                        }

                        SdkEnvirons.getInstances().registerAuthResponse(null);
                        break;
                    case HybridJSInterface.STATUS_CANCLE:
                        if(response != null){
                            OAuthData oAuthData = new OAuthData();
                            response.onResponse(oAuthData);
                        }
                        break;
                    default:
                        listener.onRefreshCallbackFailed();

                }
            }
        });

        exeCmd2Activity(activity, METHOD_REFRESH_TOKEN);
    }

    /**
     * show register page first, login page second
     *
     * @param activity Activity or FragmentActivity
     * @param response callback
     */
    public void register(Activity activity, final OAuthResponse response) {
        SdkEnvirons.getInstances().registerAuthResponse(response);

        exeCmd2Activity(activity, METHOD_REGISTER);
    }

    /**
     * Activity onActivityResult, trigger callback
     *
     * @param activity    activity
     * @param requestCode {@link GplayH5PaySDK}
     * @param resultCode  {@link Activity#RESULT_OK}
     * @param data        contain OAuthData
     */
    public void onActivityResult(Activity activity, int requestCode, int resultCode, Intent data) {
        // do nothing
    }

    /**
     * pay money
     * @param response callback
     */
    public void bind(Activity activity, final OAuthResponse response) {
        SdkEnvirons.getInstances().registerAuthResponse(response);
        exeCmd2Activity(activity, METHOD_BIND);
    }

    /**
     * show bind page if a registered account has not bound mobile phone
     * show register page if account is trial or no account
     * return immediately if a registered account has a phone
     *
     * @param activity Activity or FragmentActivity
     * @param response callback
     */
    public void pay(Activity activity, String order, String amount, String name, String desc
            , String extra, final PayResponse response) {
        SdkEnvirons.getInstances().registerPayResponse(response);
        SdkEnvirons.getInstances().setOrderInfo(new OrderInfo(order, amount, name, desc, extra));

        exeCmd2Activity(activity, METHOD_PAY);
    }

    /**
     * latest login user information {@link UserInfo}, sync information from server if necessary
     *
     * @param response async callback, response GplayUser is null if no account
     */
    public void getUser(UserResponse response) {
        UserInfo userInfo = SdkEnvirons.getInstances().getUserInfo();

        if(userInfo != null){
            response.onResponse(userInfo);
        }
    }

    /**
     * @return this sdk version
     */
    public int getVersion() { return SDKConstant.SDK_VERSION_CODE;}

    /**
     * @return latest login user access token or null
     */
    public String getAccessToken() {

        UserInfo userInfo = SdkEnvirons.getInstances().getUserInfo();

        if(userInfo != null){
            return userInfo.getAccessToken();
        }
        return "";
    }

    /**
     * @return latest login user refresh token or null
     */
    public String getRefreshToken() {

        UserInfo userInfo = SdkEnvirons.getInstances().getUserInfo();

        if(userInfo != null){
            return userInfo.getRefreshToken();
        }
        return "";
    }

    /**
     * @return latest login user id (include trial account)
     */
    public String getUid() {
        UserInfo userInfo = SdkEnvirons.getInstances().getUserInfo();
        if(userInfo != null){
            return userInfo.getUid();
        }
        return "";
    }


    public boolean isTrial() {
        UserInfo userInfo = SdkEnvirons.getInstances().getUserInfo();

        if(userInfo != null){
            return userInfo.getIsTrial();
        }
        return false;
    }

    /**
     * Activity onResume
     * @param activity activity
     */
    public void onResume(Activity activity) {}

    /**
     * Activity onPause
     * @param activity activity
     */
    public void onPause(Activity activity) {}


    /**
     * callback for GplayUser
     */
    public interface UserResponse {
        void onResponse(UserInfo user);
    }

    /**
     * callback for pay
     */
    public interface PayResponse {
        void onResponse(PayData data);
    }

    /**
     * callback for login/register/bind
     */
    public interface OAuthResponse {
        void onResponse(OAuthData data);
    }

    private interface OnRefreshBackListener{
        public void onRefreshCallbackFailed();
    }
}
