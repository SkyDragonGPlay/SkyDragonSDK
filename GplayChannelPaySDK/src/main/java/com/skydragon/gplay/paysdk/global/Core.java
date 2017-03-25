package com.skydragon.gplay.paysdk.global;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Handler;
import android.os.Looper;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.View;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
import android.widget.TextView;

import com.skydragon.gplay.paysdk.GplayPaySDK;
import com.skydragon.gplay.paysdk.OAuthData;
import com.skydragon.gplay.paysdk.PayData;
import com.skydragon.gplay.paysdk.data.DaoControl;
import com.skydragon.gplay.paysdk.ui.GplayActivity;
import com.skydragon.gplay.paysdk.GplayUser;
import com.skydragon.gplay.paysdk.data.GplayUserInner;

/**
 * Created by lindaojiang on 2015/12/9.
 */
public class Core {
    private static final String TAG = "Core";
    private static final int VERSION = 1;

    private static volatile Core instance = null;
    public static Core getInstance(){
        if(instance == null) {
            synchronized (Core.class){
                if(instance == null)
                    instance = new Core();
            }
        }
        return instance;
    }

    private Context appContext;
    private String appId;
    private String appSecret;
    private String locale;
    private GplayPaySDK.OAuthResponse authResponse;
    private OAuthData authData;
    private GplayPaySDK.PayResponse payResponse;
    private PayData payData;
    private Handler handler;
    private Runnable latestErrorRunnable;
    private static volatile GplayActivity gplayActivity = null;

    private Core(){
        appId = null;
        appSecret = null;
        locale = "";
        authResponse = null;
        authData = null;
        payResponse = null;
        payData = null;
        handler = new Handler(Looper.getMainLooper());
        latestErrorRunnable = null;
    }

    public void init(Context context, String appId, String appSecret){
        appContext = context.getApplicationContext();
        this.locale = context.getResources().getConfiguration().locale.toString();
        this.appId = appId;
        this.appSecret = appSecret;
    }

    public String getAppId() {
        return appId;
    }

    public String getAppSecret() {
        return appSecret;
    }

    public String getLocale() {
        return locale;
    }

    public void setAuthResponse(GplayPaySDK.OAuthResponse authResponse) {
        this.authResponse = authResponse;
        authData = null;
    }

    public void setAuthData(OAuthData authData) {
        this.authData = authData;
    }

    public OAuthData getAuthData() {
        return authData;
    }

    public PayData getPayData() {
        return payData;
    }

    public void setPayData(PayData payData) {
        this.payData = payData;
    }

    public void setPayResponse(GplayPaySDK.PayResponse payResponse) {
        this.payResponse = payResponse;
    }

    public static int getVersion(){
        return VERSION;
    }

    public String getUid(){
        GplayUserInner user = DaoControl.getInstance().getlatestUser(appContext);
        if(user != null)
            return user.getUid();
        return null;
    }

    public boolean isTrial() {
        GplayUserInner user = DaoControl.getInstance().getlatestUser(appContext);
        if(user != null)
            return user.getIsTrial();
        return true;
    }

    public String getAccessToken() {
        GplayUserInner user = DaoControl.getInstance().getlatestUser(appContext);
        if(user != null)
            return GplayUserInner.decode(user.getAccessToken());
        return null;
    }

    public String getRefreshToken() {
        GplayUserInner user = DaoControl.getInstance().getlatestUser(appContext);
        if(user != null)
            return GplayUserInner.decode(user.getRefreshToken());
        return null;
    }

    public void getUser(final GplayPaySDK.UserResponse response) {
        if(response == null)
            return;

        final GplayUserInner user = DaoControl.getInstance().getlatestUser(appContext);
        if(user != null) {
            if(user.getIsLoaded()){
                GplayUser gplayUser = new GplayUser(user.getUid(), user.getUsername(), user.getPhone(), user.getIsTrial(), GplayUserInner.decode(user.getAccessToken()), GplayUserInner.decode(user.getRefreshToken()), user.getExpiresAt(), user.getLoginTime());
                response.onResponse(gplayUser);
            }
            else { //sync information from server
                SdkUtils.syncUser(user, appContext, new SdkUtils.GplayHttpsResult() {
                    @Override
                    public void onSuccess() {
                        GplayUser gplayUser = new GplayUser(user.getUid(), user.getUsername(), user.getPhone(), user.getIsTrial(), GplayUserInner.decode(user.getAccessToken()), GplayUserInner.decode(user.getRefreshToken()), user.getExpiresAt(), user.getLoginTime());
                        response.onResponse(gplayUser);
                    }

                    @Override
                    public void onFailure(String error) {
                        GplayUser gplayUser = new GplayUser(user.getUid(), user.getUsername(), user.getPhone(), user.getIsTrial(), GplayUserInner.decode(user.getAccessToken()), GplayUserInner.decode(user.getRefreshToken()), user.getExpiresAt(), user.getLoginTime());
                        response.onResponse(gplayUser);
                    }
                });
            }
        }
        else {
            response.onResponse(null);
        }
    }

    public void onResume(Activity activity){

    }

    public void onPause(Activity activity){

    }

    public void onActivityResult(Activity activity, int requestCode, int resultCode, Intent data){
        if(requestCode == GplayPaySDK.REQUEST_CODE) {
            if (authResponse != null) {
                authResponse.onResponse(authData);
                authResponse = null;
            }
            if(payResponse != null) {
                payResponse.onResponse(payData);
                payResponse = null;
            }
        }
    }

    /**
     * show error and alpha animation disappear
     * @param fragmentTag fragment tag name
     * @param errorViewId view id
     * @param content error content
     */
    public synchronized void showError(final String fragmentTag, final int errorViewId, final String content) {
        if(getGplayActivity() == null) {
            if(ThisApp.isDebug)
                Log.d(TAG, "showError getGplayActivity=null");
            return;
        }
        Fragment fragment = getGplayActivity().getSupportFragmentManager().findFragmentByTag(fragmentTag);
        if(fragment == null || fragment.getView() == null) {
            if(ThisApp.isDebug)
                Log.d(TAG, "showError fragment=null");
            return;
        }
        TextView view = (TextView)fragment.getView().findViewById(errorViewId);
        if(view == null) {
            if(ThisApp.isDebug)
                Log.d(TAG, "showError view=null");
            return;
        }

        view.setText(content);
        view.setVisibility(View.VISIBLE);

        if(latestErrorRunnable != null)
            handler.removeCallbacks(latestErrorRunnable);
        latestErrorRunnable = new Runnable() {
            @Override
            public void run() {
                AlphaAnimation alphaAnimation = new AlphaAnimation(1.0f, 0.0f);
                alphaAnimation.setDuration(1000);
                alphaAnimation.setAnimationListener(new Animation.AnimationListener() {
                    @Override
                    public void onAnimationStart(Animation animation) {
                    }

                    @Override
                    public void onAnimationEnd(Animation animation) {
                        //get view again
                        if(getGplayActivity() == null) {
                            if(ThisApp.isDebug)
                                Log.d(TAG, "showError getGplayActivity=null");
                            return;
                        }
                        Fragment fragment = getGplayActivity().getSupportFragmentManager().findFragmentByTag(fragmentTag);
                        if(fragment == null || fragment.getView() == null) {
                            if(ThisApp.isDebug)
                                Log.d(TAG, "showError fragment=null");
                            return;
                        }
                        TextView view = (TextView)fragment.getView().findViewById(errorViewId);
                        if(view == null) {
                            if(ThisApp.isDebug)
                                Log.d(TAG, "showError view=null");
                            return;
                        }
                        view.setVisibility(View.GONE);
                        view.setText(null);
                    }

                    @Override
                    public void onAnimationRepeat(Animation animation) {
                    }
                });

                //get view again
                if(getGplayActivity() == null) {
                    if(ThisApp.isDebug)
                        Log.d(TAG, "showError getGplayActivity=null");
                    return;
                }
                Fragment fragment = getGplayActivity().getSupportFragmentManager().findFragmentByTag(fragmentTag);
                if(fragment == null || fragment.getView() == null) {
                    if(ThisApp.isDebug)
                        Log.d(TAG, "showError fragment=null");
                    return;
                }
                TextView view = (TextView)fragment.getView().findViewById(errorViewId);
                if(view == null) {
                    if(ThisApp.isDebug)
                        Log.d(TAG, "showError view=null");
                    return;
                }
                view.startAnimation(alphaAnimation);
            }
        };

        handler.postDelayed(latestErrorRunnable, 3300);
    }

    public Handler getHandler() {
        return handler;
    }

    public static synchronized GplayActivity getGplayActivity() {
        return gplayActivity;
    }

    public static synchronized void setGplayActivity(GplayActivity gplayActivity) {
        Core.gplayActivity = gplayActivity;
    }
}
