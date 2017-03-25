package com.skydragon.gplay.paysdk.h5;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;

import com.skydragon.gplay.paysdk.h5.controller.HybridJSInterface;
import com.skydragon.gplay.paysdk.h5.model.OAuthData;
import com.skydragon.gplay.paysdk.h5.model.OrderInfo;
import com.skydragon.gplay.paysdk.h5.model.PayData;
import com.skydragon.gplay.paysdk.h5.model.SdkEnvirons;
import com.skydragon.gplay.paysdk.h5.model.UserInfo;
import com.skydragon.gplay.paysdk.h5.utils.GpalyPaySDKUrl;
import com.skydragon.gplay.paysdk.h5.view.BaseWebView;

/**
 * package : com.skydragon.hybridsdk.controller
 * <p/>
 * Description :
 *
 * @author Y.J.ZHOU
 * @date 2016/4/11 20:36.
 */

public class GplayH5Activity extends Activity implements HybridJSInterface.OnHybridJSActionListener {

    private BaseWebView mHybridWebView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        mHybridWebView = new BaseWebView(this);

        setContentView(mHybridWebView);

        mHybridWebView.addJavascriptInterface(new HybridJSInterface(this), "HybridJSInterface");

        Intent intent = getIntent();
        int method = intent.getIntExtra(GplayH5PaySDK.METHOD_EXEC, -1);
        invorkMethod(method);
    }

    @Override
    protected void onStart() {
        super.onStart();
    }

    private void invorkMethod(int method) {

        switch (method){
            case GplayH5PaySDK.METHOD_LOGIN:
                mHybridWebView.loadUrl(GpalyPaySDKUrl.getPreLoginUrl());
                break;
            case GplayH5PaySDK.METHOD_REGISTER:
                mHybridWebView.loadUrl(GpalyPaySDKUrl.getRegisterUrl());
                break;
            case GplayH5PaySDK.METHOD_PAY:
                mHybridWebView.loadUrl(GpalyPaySDKUrl.getPayUrl());
                break;
            case GplayH5PaySDK.METHOD_BIND:
                mHybridWebView.loadUrl(GpalyPaySDKUrl.getBindUrl());
                break;
            case GplayH5PaySDK.METHOD_REFRESH_TOKEN:
                mHybridWebView.loadUrl(GpalyPaySDKUrl.getRefreshTokenUrl());
                break;
            default:
                break;
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        SdkEnvirons.getInstances().clearAllResopnseListener();
    }

    @Override
    protected void onResume() {
        super.onResume();
    }

    @Override
    public void onActivityClose() {
        handler.sendEmptyMessage(0);
        this.finish();
    }

    Handler handler = new Handler(){
        @Override
        public void dispatchMessage(Message msg) {
            super.dispatchMessage(msg);
            mHybridWebView.destory();
            mHybridWebView = null;
        }
    };

    @Override
    public void onRefreshTokenCallback(int statusCode, String message, String token) {
        OnLoginCallbackListener listener = SdkEnvirons.getInstances().getLoginCallbackListener();

        if(listener != null)
            listener.onLoginCallback(statusCode, message, token);

        SdkEnvirons.getInstances().registerAuthResponse(null);

        onActivityClose();
    }

    @Override
    public void onOAuthCallback(int statusCode, String message) {
        GplayH5PaySDK.OAuthResponse response = SdkEnvirons.getInstances().getoAuthResponse();
        UserInfo userInfo = SdkEnvirons.getInstances().getUserInfo();

        if(response != null){
            OAuthData oAuthData = new OAuthData();
            if(statusCode == HybridJSInterface.STATUS_CANCLE){
                oAuthData.setResultCode(OAuthData.RESULT_CODE_CANCEL);

                SdkEnvirons.getInstances().getUserInfo().cleanToken();

            } else if(userInfo != null){
                oAuthData.setIsTrial(userInfo.getIsTrial());
                oAuthData.setUid(userInfo.getUid());
                oAuthData.setErrorDescription(message);
                if(statusCode == HybridJSInterface.STATUS_SUCCESS){
                    oAuthData.setResultCode(OAuthData.RESULT_CODE_SUCCESS);
                } else {
                    oAuthData.setResultCode(OAuthData.RESULT_CODE_FAIL);
                }
            }
            response.onResponse(oAuthData);
        }

        // release AuthResponse Interface
        SdkEnvirons.getInstances().registerAuthResponse(null);
    }

    @Override
    public void onPayCallback(int statusCode, String message) {
        GplayH5PaySDK.PayResponse response = SdkEnvirons.getInstances().getoPayResponse();

        if(response == null)
            return;

        OrderInfo orderInfo = SdkEnvirons.getInstances().getOrderInfo();
        final PayData payData = new PayData();

        switch (statusCode){
            case HybridJSInterface.STATUS_SUCCESS:
                if(orderInfo != null){
                    payData.setResultCode(PayData.RESULT_CODE_SUCCESS);
                    payData.setAmount(orderInfo.getAmount());
                    payData.setName(orderInfo.getGoodsName());
                    payData.setErrorDescription(message);
                }
                break;

            default:
                break;
        }

        response.onResponse(payData);

        // release PayReponse Interface
        SdkEnvirons.getInstances().registerPayResponse(null);
    }

    public interface OnLoginCallbackListener {
        public void onLoginCallback(int statusCode, String message, String token);
    }
}
