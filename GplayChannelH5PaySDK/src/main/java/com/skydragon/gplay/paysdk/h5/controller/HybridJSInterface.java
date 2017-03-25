package com.skydragon.gplay.paysdk.h5.controller;

import android.os.Build;
import android.util.Log;
import android.webkit.JavascriptInterface;

import com.skydragon.gplay.paysdk.h5.common.SDKConstant;
import com.skydragon.gplay.paysdk.h5.model.DeviceInfo;
import com.skydragon.gplay.paysdk.h5.model.OrderInfo;
import com.skydragon.gplay.paysdk.h5.model.SdkEnvirons;
import com.skydragon.gplay.paysdk.h5.model.TokenInfo;
import com.skydragon.gplay.paysdk.h5.model.UserInfo;

import org.json.JSONException;
import org.json.JSONObject;

/**
 * package : com.skydragon.hybridsdk.controller
 *
 * Description :
 *
 * @author Y.J.ZHOU
 * @date 2016/4/11 16:46.
 */
public class HybridJSInterface {

    private static final String TAG = "HybridJSInterface";

    private final String KEY_UUID = "uuid";
    private final String KEY_DEVILCE_ID = "dev_id";
    private final String KEY_VERSION = "sdk_version";
    private final String KEY_SESSION = "session";
    private final String KEY_USER_ID = "user_id";
    private final String KEY_IS_TRIAL = "is_trial";
    private final String KEY_EXPIRE_TIME = "expire_time";
    private final String KEY_EXPIRE_IN = "expire_in";
    private final String KEY_REFRESH_TOKEN = "refresh_token";
    private final String KEY_ACCESS_TOKEN = "access_token";
    private final String KEY_TOKEN_TYPE = "token_type";
    private final String KEY_CLIENT_ID = "client_id";
    private final String KEY_CLIENT_SECRET = "client_secret";

    public final static int STATUS_SUCCESS = 10000;
    public final static int STATUS_CANCLE = 80001;

    private OnHybridJSActionListener mOnHybridJSActionListener;
    public static String mJSBackAction;

    public HybridJSInterface(OnHybridJSActionListener listener) {
        mOnHybridJSActionListener = listener;
    }

    @JavascriptInterface
    public String getDeviceInfo(){
        DeviceInfo devinfo = SdkEnvirons.getInstances().getDeviceInfo();
        if(devinfo == null)
            return null;

        JSONObject jsonObject = new JSONObject();
        try {
            jsonObject.put(KEY_UUID, devinfo.getUUId());
            jsonObject.put(KEY_DEVILCE_ID, devinfo.getUniqueId());
            jsonObject.put(KEY_VERSION, SDKConstant.SDK_VERSION);
        } catch (JSONException e) {
            e.printStackTrace();
        }

        if(SDKConstant.isDebug)
            Log.d(TAG, "0--0930-1 OnHybridJSActionListener#getDeviceInfo" + jsonObject.toString());
        return jsonObject.toString();
    }

    @JavascriptInterface
    public void addBackEventListener(String jsStr){
        mJSBackAction = jsStr;
    }

    @JavascriptInterface
    public int getAndroidVersion(){
        return Build.VERSION.SDK_INT;
    }

    @JavascriptInterface
    public String getClientCredentials(){
        JSONObject ccJsonStr = new JSONObject();
        try {
            ccJsonStr.put(KEY_CLIENT_ID, SdkEnvirons.getInstances().getmClientIdId());
            ccJsonStr.put(KEY_CLIENT_SECRET, SdkEnvirons.getInstances().getmClientSecret());
        } catch (JSONException e) {
            e.printStackTrace();
        }


        if(SDKConstant.isDebug)
            Log.v(TAG, "0--0930-1 OnHybridJSActionListener#getClientCredentials " + ccJsonStr.toString());
        return ccJsonStr.toString();
    }

    @JavascriptInterface
    public String getSessionData(){
        JSONObject ccJsonStr = new JSONObject();
        DeviceInfo devinfo = SdkEnvirons.getInstances().getDeviceInfo();
        UserInfo userInfo = SdkEnvirons.getInstances().getUserInfo();

        if(devinfo != null
                && userInfo != null
                && devinfo.getSession() != null
                && devinfo.getSessionExpireTime() > 0) {
            try {
                ccJsonStr.put(KEY_SESSION, devinfo.getSession());
                ccJsonStr.put(KEY_USER_ID, devinfo.getSessionUid());
                ccJsonStr.put(KEY_IS_TRIAL, userInfo.getIsTrial());
                ccJsonStr.put(KEY_EXPIRE_TIME, devinfo.getSessionExpireTime());
            } catch (JSONException e) {
                e.printStackTrace();
            }
        } else {
            if(SDKConstant.isDebug)
                Log.v(TAG, "OnHybridJSActionListener#getSessionData null");
            return null;
        }

        if(SDKConstant.isDebug)
            Log.v(TAG, "0--0930-1 OnHybridJSActionListener#getSessionData " + ccJsonStr.toString());
        return ccJsonStr.toString();
    }

    @JavascriptInterface
    public String getToken(){
        UserInfo userInfo = SdkEnvirons.getInstances().getUserInfo();
        JSONObject json = new JSONObject();
        if(userInfo != null && userInfo.getTokenInfo() != null){
            TokenInfo tokenInfo = userInfo.getTokenInfo();
            try {
                json.put(KEY_USER_ID, userInfo.getUid());
                json.put(KEY_REFRESH_TOKEN, tokenInfo.getRefreshToken());
                json.put(KEY_ACCESS_TOKEN, tokenInfo.getAccessToken());
                json.put(KEY_TOKEN_TYPE, tokenInfo.getTokenType());
                json.put(KEY_EXPIRE_IN, tokenInfo.getExpireIn());
                json.put(KEY_EXPIRE_TIME, tokenInfo.getExpireIn() + tokenInfo.getLoginTime());
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
        if(SDKConstant.isDebug)
            Log.v(TAG, "0--0930-1 OnHybridJSActionListener#getToken " + json.toString());
        return json.toString();
    }

    @JavascriptInterface
    public String getLastUserId(){
        UserInfo userInfo = SdkEnvirons.getInstances().getUserInfo();
        if(SDKConstant.isDebug && userInfo != null)
            Log.v(TAG, "0--0930-1 OnHybridJSActionListener#getToken " + userInfo.toString());
        return userInfo.getUid();
    }

    @JavascriptInterface
    public void payCallback(int statusCode, String message){
        if(SDKConstant.isDebug)
            Log.v(TAG, "0--0930-1 OnHybridJSActionListener#payCallback statusCode " + statusCode + " , message " + message);
        if(mOnHybridJSActionListener != null){
            // on callback
            mOnHybridJSActionListener.onPayCallback(statusCode, message);
            // close webview
            mOnHybridJSActionListener.onActivityClose();
        }
    }

    @JavascriptInterface
    public String getOrderInfo(){
        OrderInfo orderInfo = SdkEnvirons.getInstances().getOrderInfo();
        if(orderInfo != null){
            if(SDKConstant.isDebug)
                Log.v(TAG, "0--0930-1 OnHybridJSActionListener#getOrderInfo "  + orderInfo.toJsonObj().toString());
            return orderInfo.toJsonObj().toString();
        } else{
            if(SDKConstant.isDebug)
                Log.v(TAG, "0--0930-1 OnHybridJSActionListener#getOrderInfo null ");
            return null;
        }
    }

    @JavascriptInterface
    public synchronized void setSessionData(String sessionData){
        if(SDKConstant.isDebug)
            Log.v(TAG, "0--0930-1 OnHybridJSActionListener#setSessionData " + sessionData);

        if(sessionData == null)
            return;
        try {
            JSONObject jsonObject = new JSONObject(sessionData);
            final String session = jsonObject.optString(KEY_SESSION, null);
            final Long expireTime = jsonObject.optLong(KEY_EXPIRE_TIME, 0L);
            final String userId = jsonObject.optString(KEY_USER_ID, null);
            final boolean isTrial = jsonObject.optBoolean(KEY_IS_TRIAL);

            SdkEnvirons.getInstances().refreshSessionDate(session, userId, isTrial, expireTime);

        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    @JavascriptInterface
    public void refreshTokenCallback(int statusCode, String message, String token){
        if(SDKConstant.isDebug)
            Log.v(TAG, "0--0930-1 OnHybridJSActionListener#refreshTokenCallback "  + statusCode + " , " + message + " , token" + token);

        UserInfo userInfo = SdkEnvirons.getInstances().getUserInfo();
        if(userInfo != null)
            userInfo.refreshTokenFromJSInterface(token);

        if(mOnHybridJSActionListener != null) {
            mOnHybridJSActionListener.onRefreshTokenCallback(statusCode, message, token);
        }
    }

    @JavascriptInterface
    public void loginCallback(int statusCode, String message, String token){
        if(SDKConstant.isDebug)
            Log.v(TAG, "0--0930-1 OnHybridJSActionListener#loginCallback statusCode : "+ statusCode + " , message : " + message + " , token : " + token);
        // refresh local token data
        UserInfo userInfo = SdkEnvirons.getInstances().getUserInfo();

        if(userInfo != null)
            userInfo.refreshTokenFromJSInterface(token);

        if(mOnHybridJSActionListener != null){
            // on callback
            mOnHybridJSActionListener.onOAuthCallback(statusCode, message);
            // close webview
            mOnHybridJSActionListener.onActivityClose();
        }
    }

    @JavascriptInterface
    public void bindCallback(int statusCode, String message){
        if(SDKConstant.isDebug)
            Log.v(TAG, "0--0930-1 OnHybridJSActionListener#bindCallback "+ statusCode + " , " + message + " , " + message);
        if(mOnHybridJSActionListener != null){
            // on callback
            mOnHybridJSActionListener.onOAuthCallback(statusCode, message);
            // close webview
            mOnHybridJSActionListener.onActivityClose();
        }
    }

    public interface OnHybridJSActionListener{
        public void onActivityClose();
        public void onRefreshTokenCallback(int statusCode, String message, String token);
        public void onOAuthCallback(int statusCode, String message);
        public void onPayCallback(int statusCode, String message);
    }
}
