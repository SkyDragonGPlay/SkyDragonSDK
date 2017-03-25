package com.skydragon.gplay.paysdk;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;

import com.skydragon.gplay.paysdk.ui.GplayActivity;
import com.skydragon.gplay.paysdk.global.Core;

public class GplayPaySDK {
    public static final int REQUEST_CODE = 0xECAB;
    public static final String RESULT_DATA_AUTH = "GplayThirdSDK_ResultDataAuth";
    public static final String RESULT_DATA_PAY = "GplayThirdSDK_ResultDataPay";

    /**
     * sdk init before call other function
     *
     * @param context   application context or activity context
     * @param appId     app id
     * @param appSecret app secret
     */
    public static void init(Context context, String appId, String appSecret) {
        Core.getInstance().init(context, appId, appSecret);
    }

    /**
     * Activity onResume
     *
     * @param activity activity
     */
    public static void onResume(Activity activity) {
        Core.getInstance().onResume(activity);
    }

    /**
     * Activity onPause
     *
     * @param activity activity
     */
    public static void onPause(Activity activity) {
        Core.getInstance().onPause(activity);
    }

    /**
     * Activity onActivityResult, trigger callback
     *
     * @param activity    activity
     * @param requestCode {@link GplayPaySDK#REQUEST_CODE}
     * @param resultCode  {@link Activity#RESULT_OK}
     * @param data        contain OAuthData
     */
    public static void onActivityResult(Activity activity, int requestCode, int resultCode, Intent data) {
        Core.getInstance().onActivityResult(activity, requestCode, resultCode, data);
    }

    /**
     * callback for login/register/bind
     */
    public interface OAuthResponse {
        void onResponse(OAuthData data);
    }

    /**
     * show login page first, register page second
     * login: show switch account 3s, then auto login or input account and password
     *
     * @param activity Activity or FragmentActivity
     * @param response callback
     */
    public static void login(Activity activity, final OAuthResponse response) {
        Core.getInstance().setAuthResponse(response);
        Intent intent = new Intent(activity, GplayActivity.class);
        intent.putExtra("gg", "login");
        activity.startActivityForResult(intent, REQUEST_CODE);
    }

    /**
     * show register page first, login page second
     *
     * @param activity Activity or FragmentActivity
     * @param response callback
     */
    public static void register(Activity activity, final OAuthResponse response) {
        Core.getInstance().setAuthResponse(response);
        Intent intent = new Intent(activity, GplayActivity.class);
        intent.putExtra("gg", "register");
        activity.startActivityForResult(intent, REQUEST_CODE);
    }

    /**
     * show bind page if a registered account has not bound mobile phone
     * show register page if account is trial or no account
     * return immediately if a registered account has a phone
     *
     * @param activity Activity or FragmentActivity
     * @param response callback
     */
    public static void bind(Activity activity, final OAuthResponse response) {
        Core.getInstance().setAuthResponse(response);
        Intent intent = new Intent(activity, GplayActivity.class);
        intent.putExtra("gg", "bind");
        activity.startActivityForResult(intent, REQUEST_CODE);
    }

    /**
     * @return latest login user id (include trial account)
     */
    public static String getUid() {
        return Core.getInstance().getUid();
    }

    /**
     * @return latest login user, true:trial account not registered
     */
    public static boolean isTrial() {
        return Core.getInstance().isTrial();
    }

    /**
     * @return latest login user access token or null
     */
    public static String getAccessToken() {
        return Core.getInstance().getAccessToken();
    }

    /**
     * @return latest login user refresh token or null
     */
    public static String getRefreshToken() {
        return Core.getInstance().getRefreshToken();
    }

    /**
     * callback for GplayUser
     */
    public interface UserResponse {
        void onResponse(GplayUser user);
    }

    /**
     * latest login user information {@link GplayUser}, sync information from server if necessary
     *
     * @param response async callback, response GplayUser is null if no account
     */
    public static void getUser(UserResponse response) {
        Core.getInstance().getUser(response);
    }

    /**
     * @return this sdk version
     */
    public static int getVersion() {
        return Core.getVersion();
    }

    /**
     * callback for pay
     */
    public interface PayResponse {
        void onResponse(PayData data);
    }

    /**
     * pay money
     *
     * @param activity activity
     * @param order    pay order id
     * @param amount   pay money amount
     * @param name     product name
     * @param desc     product description
     * @param extra    extra arguments, if not null, must be a json string
     * @param response callback
     */
    public static void pay(Activity activity, String order, String amount, String name, String desc, String extra, final PayResponse response) {
        Core.getInstance().setPayResponse(response);
        Intent intent = new Intent(activity, GplayActivity.class);
        intent.putExtra("gg", "pay");
        Bundle arg = new Bundle();
        arg.putString("order", order);
        arg.putString("amount", amount);
        arg.putString("name", name);
        arg.putString("desc", desc);
        arg.putString("extra", extra);
        intent.putExtra("arg", arg);
        activity.startActivityForResult(intent, REQUEST_CODE);
    }
}
