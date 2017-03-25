package com.skydragon.gplay.paysdk.h5.model;

import android.content.Context;
import android.os.Environment;

import com.skydragon.gplay.paysdk.h5.GplayH5PaySDK;
import com.skydragon.gplay.paysdk.h5.GplayH5Activity;

import java.io.File;

/**
 * package : com.skydragon.hybridsdk.model
 * <p/>
 * Description :
 *
 * @author Y.J.ZHOU
 * @date 2016/4/13 15:35.
 */
public class SdkEnvirons {

    private static SdkEnvirons mInstance;
    private GplayH5PaySDK.OAuthResponse oAuthResponse;
    private GplayH5PaySDK.PayResponse oPayResponse;
    private Context mContext;
    private String mClientId;
    private String mClientSecret;
    private GplayH5Activity.OnLoginCallbackListener loginCallbackListener;
    private DeviceInfo mDeviceInfo;
    private UserInfo mUserInfo;
    private OrderInfo mOrderInfo;
    private String mHybridDir;

    private SdkEnvirons() {}

    public static SdkEnvirons getInstances(){
        synchronized (SdkEnvirons.class) {
            if (mInstance == null){
                mInstance = new SdkEnvirons();
            }
        }
        return mInstance;
    }

    public void init(Context context, String clientId, String clientSecret){
        mContext = context;
        mClientId = clientId;
        mClientSecret = clientSecret;

        // init deviceInfo
        mDeviceInfo = new DeviceInfo(getHybridPath() + File.separator + "hybrid");

        mUserInfo = getUserInfo();
    }

    public UserInfo getUserInfo() {
        if(mContext == null)
            return null;

        if(mUserInfo == null) {
            mUserInfo = new UserInfo(mClientId, mContext);
        }

        return mUserInfo;
    }

    public void clearAllResopnseListener(){
        if(oAuthResponse != null){
            oAuthResponse.onResponse(new OAuthData());
            oAuthResponse = null;
        }

        if(oPayResponse != null){
            oPayResponse.onResponse(new PayData());
            oPayResponse = null;
        }
    }

    public void refreshSessionDate(String session, String uid, boolean isTrial, Long expireTime){
        if(session == null || session.equals(""))
            return;

        if(mDeviceInfo != null){
            mDeviceInfo.refreshSession(session, uid, expireTime);
        }

        if(mUserInfo != null){
            mUserInfo.refreshUid(session, uid, isTrial);
        }
    }

    public void registerLoginCallbackListener(GplayH5Activity.OnLoginCallbackListener listener){
        loginCallbackListener = listener;
    }

    public void registerAuthResponse(GplayH5PaySDK.OAuthResponse response) {
        oAuthResponse = response;
    }

    public void registerPayResponse(GplayH5PaySDK.PayResponse response) {
        oPayResponse = response;
    }

    public GplayH5PaySDK.OAuthResponse getoAuthResponse() {
        return oAuthResponse;
    }

    public GplayH5PaySDK.PayResponse getoPayResponse() {
        return oPayResponse;
    }

    public GplayH5Activity.OnLoginCallbackListener getLoginCallbackListener() {
        return loginCallbackListener;
    }

    public Context getContext() {
        return mContext;
    }

    public DeviceInfo getDeviceInfo() {
        return mDeviceInfo;
    }

    public String  getmClientIdId() {
        return mClientId;
    }

    public String getmClientSecret() {
        return mClientSecret;
    }

    public String getHybridPath(){
        if(mHybridDir == null){
            mHybridDir = Environment.getExternalStorageDirectory().getAbsolutePath();
            File hybridDir = new File(mHybridDir);
            if(!hybridDir.exists()){
                hybridDir.mkdirs();
            }
        }
        return mHybridDir;
    }

    public void setOrderInfo(OrderInfo mOrderInfo) {
        this.mOrderInfo = mOrderInfo;
    }

    public OrderInfo getOrderInfo() {
        return mOrderInfo;
    }
}
