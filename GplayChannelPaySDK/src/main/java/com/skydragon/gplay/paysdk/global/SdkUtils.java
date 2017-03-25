package com.skydragon.gplay.paysdk.global;


import android.content.Context;
import android.text.TextUtils;
import android.util.Log;

import com.skydragon.gplay.loopj.android.http.Base64;
import com.skydragon.gplay.paysdk.OAuthData;
import com.skydragon.gplay.paysdk.PayData;
import com.skydragon.gplay.paysdk.data.DaoControl;
import com.skydragon.gplay.paysdk.data.GplayUserInner;
import com.skydragon.gplay.paysdk.data.ResponseBind;
import com.skydragon.gplay.paysdk.data.ResponseCreateUser;
import com.skydragon.gplay.paysdk.data.ResponseGetCode;
import com.skydragon.gplay.paysdk.data.ResponseGetUser;
import com.skydragon.gplay.paysdk.data.ResponseRegister;
import com.skydragon.gplay.paysdk.data.ResponseToken;
import com.skydragon.gplay.paysdk.tool.CommonInfo;
import com.skydragon.gplay.loopj.android.http.AsyncHttpClient;
import com.skydragon.gplay.loopj.android.http.AsyncHttpResponseHandler;
import com.skydragon.gplay.loopj.android.http.RequestParams;

import org.apache.http.Header;
import org.apache.http.protocol.HTTP;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.UUID;

public class SdkUtils {
    private static final String TAG = "SdkUtils";
    private static final long TOKEN_EXPIRES_DIFF = 10 * 60 * 1000L; //10min

    public static void quickLoginValidUser(final Context appContext, final GplayUserInner user) {
        if(ThisApp.isDebug)
            Log.d(TAG, "quickLoginValidUser " + user.getUsername());
        user.setLoginTime(System.currentTimeMillis());
        DaoControl.getInstance().put(appContext, user);

        //callback data
        OAuthData authData = new OAuthData();
        authData.setUid(user.getUid());
        authData.setIsTrial(user.getIsTrial());
        authData.setErrorDescription(null);
        authData.setResultCode(OAuthData.RESULT_CODE_SUCCESS);
        Core.getInstance().setAuthData(authData);
    }

    public static AsyncHttpClient newHttpClient() {
        AsyncHttpClient client = new AsyncHttpClient();
        client.setLoggingEnabled(false);
        client.setUserAgent(ThisApp.UserAgent);
        client.addHeader(HTTP.CONTENT_TYPE, Constants.URL_ENCODED_CONTENT);
        client.setTimeout(6000);
        client.setConnectTimeout(6000);
        client.setMaxRetriesAndTimeout(0, 6000);
        return client;
    }

    public static RequestParams newRequestParams() {
        RequestParams params = new RequestParams();
        params.put(Constants.CLIENT_ID, Core.getInstance().getAppId());
        params.put(Constants.CLIENT_SECRET, Core.getInstance().getAppSecret());
        params.put(Constants.LOCALE, Core.getInstance().getLocale());
        return params;
    }

    public static void refreshToken(final GplayUserInner user, final Context appContext, final GplayHttpsResult gplayHttpsResult) {
        AsyncHttpClient httpClient = newHttpClient();
        RequestParams params = newRequestParams();
        params.put(Constants.GRANT_TYPE, "refresh_token");
        params.put(Constants.REFRESH_TOKEN, GplayUserInner.decode(user.getRefreshToken()));

        httpClient.post(ThisApp.getUrlToken(), params, new AsyncHttpResponseHandler() {
            @Override
            public void onSuccess(int i, Header[] headers, byte[] bytes) {
                if (ThisApp.isDebug)
                    Log.d(TAG, "refresh token onSuccess i=" + i);
                oAuthToken(bytes, user, appContext, gplayHttpsResult);
            }

            @Override
            public void onFailure(int i, Header[] headers, byte[] bytes, Throwable throwable) {
                if (ThisApp.isDebug)
                    Log.e(TAG, "refresh token failed i=" + i + " " + ((throwable != null) ? throwable.getMessage() : ""));

                oAuthToken(bytes, user, appContext, gplayHttpsResult);
            }
        });
    }

    public static void getAccessToken(final GplayUserInner user, final String password, final Context appContext, final GplayHttpsResult gplayHttpsResult) {
        AsyncHttpClient httpClient = newHttpClient();
        RequestParams params = newRequestParams();
        params.put(Constants.GRANT_TYPE, "password");
        params.put(Constants.USERNAME, user.getUsername());
        params.put(Constants.PASSWORD, password);

        //httpClient.addHeader(Constants.AUTHORIZATION, SdkUtils.getBasicAuthorizationHeader(Core.getInstance().getAppId(), Core.getInstance().getAppSecret()));

        httpClient.post(ThisApp.getUrlToken(), params, new AsyncHttpResponseHandler() {
            @Override
            public void onSuccess(int i, Header[] headers, byte[] bytes) {
                if (ThisApp.isDebug)
                    Log.d(TAG, "get access token onSuccess i=" + i);
                oAuthToken(bytes, user, appContext, gplayHttpsResult);
            }

            @Override
            public void onFailure(int i, Header[] headers, byte[] bytes, Throwable throwable) {
                if (ThisApp.isDebug)
                    Log.e(TAG, "get access token failed i=" + i + " " + ((throwable != null) ? throwable.getMessage() : ""));

                oAuthToken(bytes, user, appContext, gplayHttpsResult);
            }
        });
    }

    private static void oAuthToken(final byte[] bytes, final GplayUserInner user, final Context appContext, final GplayHttpsResult gplayHttpsResult) {
        //callback data
        OAuthData authData = new OAuthData();
        authData.setResultCode(OAuthData.RESULT_CODE_FAIL);
        authData.setUid(user.getUid()); //user id is null if a registered account login
        authData.setIsTrial(user.getIsTrial());
        authData.setErrorDescription(appContext.getString(CommonInfo.getStringId(appContext, "GplayThirdSdk_errorNetwork")));

        if (bytes != null && bytes.length > 0) {
            ResponseToken token;
            try{
                JSONObject json = new JSONObject(new String(bytes));
                token = new ResponseToken(json.optString(Constants.ACCESS_TOKEN), json.optString(Constants.TOKEN_TYPE), json.optLong(Constants.EXPIRES_IN), json.optString(Constants.SCOPE), json.optString(Constants.REFRESH_TOKEN), json.optString(Constants.ERROR), json.optString(Constants.ERROR_DESCRIPTION), json.optString(Constants.USER_ID));
                if (ThisApp.isDebug) {
                    Log.d(TAG, "get token :\n" + json);
                }
            }
            catch (JSONException e) {
                if (ThisApp.isDebug)
                    Log.e(TAG, "get token failed JSONException " + e.getMessage() + " bytes=" + new String(bytes));
                token = null;
            }

            if(token != null) {
                //receive new token
                if (!TextUtils.isEmpty(token.getAccess_token())) {
                    String accessToken = GplayUserInner.encode(token.getAccess_token());
                    String refreshToken = GplayUserInner.encode(token.getRefresh_token());
                    user.setAccessToken(accessToken);
                    user.setRefreshToken(refreshToken);
                    user.setTokenType(token.getToken_type());
                    user.setScope(token.getScope());
                    user.setExpiresIn(token.getExpires_in());
                    user.setExpiresAt(System.currentTimeMillis() + (token.getExpires_in() * 1000) - TOKEN_EXPIRES_DIFF);
                    user.setUid(token.getUser_id());
                    user.setLoginTime(System.currentTimeMillis());
                    DaoControl.getInstance().put(appContext, user);

                    authData.setResultCode(OAuthData.RESULT_CODE_SUCCESS);
                    authData.setUid(token.getUser_id());
                    authData.setErrorDescription(null);
                    Core.getInstance().setAuthData(authData);

                    //sync user
                    if (!user.getIsLoaded()) {
                        //row id is null if a new record insert when a registered account login, must refresh row id from database, avoid insert tow record
                        GplayUserInner storedUser = DaoControl.getInstance().findUser(appContext, user.getUsername());
                        if(storedUser != null) {
                            SdkUtils.syncUser(storedUser, appContext, new GplayHttpsResult() {
                                @Override
                                public void onSuccess() {
                                }

                                @Override
                                public void onFailure(String error) {
                                }
                            });
                        }
                        else {
                            Log.e(TAG, "syncUser reload user empty");
                        }
                    }

                    if (gplayHttpsResult != null) {
                        gplayHttpsResult.onSuccess();
                    }
                    return;
                } else { //raise oauth error, destroy old token
                    if (user.getId() != null) {
                        user.setExpiresAt(0L);
                        DaoControl.getInstance().put(appContext, user);
                    }

                    String errorDescription = token.getError_description();
                    if (TextUtils.isEmpty(errorDescription))
                        errorDescription = appContext.getString(CommonInfo.getStringId(appContext, "GplayThirdSdk_errorSystem"));
                    authData.setErrorDescription(errorDescription);
                }
            }
        }
        else {
            if (ThisApp.isDebug) {
                Log.e(TAG, "get token bytes=null");
            }
        }

        Core.getInstance().setAuthData(authData);
        if (gplayHttpsResult != null) {
            gplayHttpsResult.onFailure(authData.getErrorDescription());
        }
    }

    public static void getNewUser(final GplayUserInner user, final Context appContext, final GplayHttpsResult gplayHttpsResult) {
        AsyncHttpClient httpClient = newHttpClient();
        RequestParams params = newRequestParams();
        String randString = UUID.randomUUID().toString();
        String sign = CommonInfo.md5(CommonInfo.md5(randString + Core.getInstance().getAppId()) + Core.getInstance().getAppSecret());
        params.put("state", randString);
        params.put("sign", sign);

        httpClient.post(ThisApp.getUrlGetAutoUser(), params, new AsyncHttpResponseHandler() {
            @Override
            public void onSuccess(int i, Header[] headers, byte[] bytes) {
                if (bytes != null && bytes.length > 0) {
                    ResponseCreateUser responseCreateUser;
                    JSONObject json;
                    try{
                        json = new JSONObject(new String(bytes));
                        responseCreateUser = new ResponseCreateUser(json.optString(Constants.USERNAME), json.optString(Constants.USER_ID), json.optString(Constants.ERROR), json.optString(Constants.ERROR_DESCRIPTION));
                        if (ThisApp.isDebug) {
                            Log.d(TAG, "create user onSuccess :\n" + json);
                        }
                    }
                    catch (JSONException e) {
                        if (ThisApp.isDebug)
                            Log.e(TAG, "create user failed JSONException " + e.getMessage());
                        onFailure(i, headers, bytes, null);
                        return;
                    }

                    //callback data
                    OAuthData authData = new OAuthData();
                    authData.setIsTrial(true);

                    //receive new user
                    if (!TextUtils.isEmpty(responseCreateUser.getUser_id()) && !TextUtils.isEmpty(responseCreateUser.getUsername())) {
                        user.setUid(responseCreateUser.getUser_id());
                        user.setUsername(responseCreateUser.getUsername());
                        user.setLoginTime(System.currentTimeMillis());

                        if(json != null){
                            user.setAccessToken(json.optString(Constants.ACCESS_TOKEN));
                            user.setRefreshToken(json.optString(Constants.REFRESH_TOKEN));
                            user.setTokenType(json.optString(Constants.TOKEN_TYPE));
                            user.setScope(json.optString(Constants.SCOPE));
                            user.setExpiresIn(json.optLong(Constants.EXPIRES_IN));
                            user.setExpiresAt(System.currentTimeMillis() + (user.getExpiresIn() * 1000) - TOKEN_EXPIRES_DIFF);
                            user.setLoginTime(System.currentTimeMillis());
                        }
                        DaoControl.getInstance().put(appContext, user);

                        authData.setResultCode(OAuthData.RESULT_CODE_SUCCESS);
                        authData.setUid(responseCreateUser.getUser_id());
                        Core.getInstance().setAuthData(authData);

                        if (gplayHttpsResult != null) {
                            gplayHttpsResult.onSuccess();
                        }
                    } else { //raise error
                        String errorDescription = responseCreateUser.getError_description();
                        if (TextUtils.isEmpty(errorDescription))
                            errorDescription = appContext.getString(CommonInfo.getStringId(appContext, "GplayThirdSdk_errorSystem"));
                        authData.setResultCode(OAuthData.RESULT_CODE_FAIL);
                        authData.setErrorDescription(errorDescription);
                        Core.getInstance().setAuthData(authData);

                        if (gplayHttpsResult != null) {
                            gplayHttpsResult.onFailure(errorDescription);
                        }
                    }

                } else {
                    onFailure(i, headers, bytes, null);
                }
            }

            @Override
            public void onFailure(int i, Header[] headers, byte[] bytes, Throwable throwable) {
                if (ThisApp.isDebug)
                    Log.e(TAG, "create user failed " + ((throwable != null) ? throwable.getMessage() : "") + " bytes=" + (bytes != null ? new String(bytes) : ""));

                OAuthData authData = new OAuthData();
                authData.setResultCode(OAuthData.RESULT_CODE_FAIL);
                authData.setErrorDescription(appContext.getString(CommonInfo.getStringId(appContext, "GplayThirdSdk_errorNetwork")));
                Core.getInstance().setAuthData(authData);

                if (gplayHttpsResult != null) {
                    gplayHttpsResult.onFailure(authData.getErrorDescription());
                }
            }
        });
    }

    public static void getCode(final String phone, final Context appContext, final GplayHttpsResult1 httpsResult) {
        AsyncHttpClient httpClient = newHttpClient();
        RequestParams params = newRequestParams();
        params.put(Constants.PHONE, phone);

        httpClient.post(ThisApp.getUrlVerifyCode(), params, new AsyncHttpResponseHandler() {
            @Override
            public void onSuccess(int i, Header[] headers, byte[] bytes) {
                if (bytes != null && bytes.length > 0) {
                    ResponseGetCode responseGetCode;
                    try {
                        JSONObject json = new JSONObject(new String(bytes));
                        responseGetCode = new ResponseGetCode(json.optString(Constants.SERIAL), json.optString(Constants.ERROR), json.optString(Constants.ERROR_DESCRIPTION));

                        if (ThisApp.isDebug) {
                            Log.d(TAG, "get code onSuccess :\n" + json);
                        }
                    } catch (JSONException e) {
                        if (ThisApp.isDebug)
                            Log.e(TAG, "get code failed JSONException " + e.getMessage());
                        onFailure(i, headers, bytes, null);
                        return;
                    }

                    if (!TextUtils.isEmpty(responseGetCode.getSerial())) {
                        if (httpsResult != null)
                            httpsResult.onSuccess(responseGetCode.getSerial());
                    } else {
                        onFailure(i, headers, bytes, null);
                    }

                } else {
                    onFailure(i, headers, bytes, null);
                }
            }

            @Override
            public void onFailure(int i, Header[] headers, byte[] bytes, Throwable throwable) {
                if (ThisApp.isDebug)
                    Log.e(TAG, "get code failed " + ((throwable != null) ? throwable.getMessage() : "") + " bytes=" + (bytes != null ? new String(bytes) : ""));

                if (httpsResult != null)
                    httpsResult.onFailure(appContext.getString(CommonInfo.getStringId(appContext, "GplayThirdSdk_errorGetCode")));
            }
        });
    }

    public static void register(final String username, final String uid, final String password, final String phone, final String code, final String serial, final Context appContext, final GplayHttpsResult httpsResult) {
        AsyncHttpClient httpClient = newHttpClient();
        RequestParams params = newRequestParams();
        params.put(Constants.USERNAME, username);
        params.put(Constants.PASSWORD, password);
        params.put(Constants.USER_ID, uid);
        params.put(Constants.PHONE, phone);
        params.put(Constants.CODE, code);
        params.put(Constants.SERIAL, serial);

        httpClient.post(ThisApp.getUrlRegister(), params, new AsyncHttpResponseHandler() {
            @Override
            public void onSuccess(int i, Header[] headers, byte[] bytes) {
                if (bytes != null && bytes.length > 0) {
                    ResponseRegister responseRegister;
                    try {
                        JSONObject json = new JSONObject(new String(bytes));
                        responseRegister = new ResponseRegister(json.optString(Constants.SUCCESS), json.optString(Constants.ERROR), json.optString(Constants.ERROR_DESCRIPTION));

                        if (ThisApp.isDebug) {
                            Log.d(TAG, "register onSuccess :\n" + json);
                        }

                    } catch (JSONException e) {
                        if (ThisApp.isDebug)
                            Log.e(TAG, "register failed JSONException " + e.getMessage());
                        onFailure(i, headers, bytes, null);
                        return;
                    }

                    if (TextUtils.isEmpty(responseRegister.getError())) {
                        //callback data OAuthData create when login
                        if (httpsResult != null)
                            httpsResult.onSuccess();
                    } else {//raise error
                        String errorDescription = responseRegister.getError_description();
                        if (TextUtils.isEmpty(errorDescription))
                            errorDescription = appContext.getString(CommonInfo.getStringId(appContext, "GplayThirdSdk_errorSystem"));

                        //callback data
                        OAuthData authData = new OAuthData();
                        authData.setResultCode(OAuthData.RESULT_CODE_FAIL);
                        authData.setErrorDescription(errorDescription);
                        Core.getInstance().setAuthData(authData);

                        if (httpsResult != null) {
                            httpsResult.onFailure(errorDescription);
                        }
                    }
                } else {
                    onFailure(i, headers, bytes, null);
                }
            }

            @Override
            public void onFailure(int i, Header[] headers, byte[] bytes, Throwable throwable) {
                if (ThisApp.isDebug)
                    Log.e(TAG, "register failed " + ((throwable != null) ? throwable.getMessage() : "") + " bytes=" + (bytes != null ? new String(bytes) : ""));

                OAuthData authData = new OAuthData();
                authData.setResultCode(OAuthData.RESULT_CODE_FAIL);
                authData.setErrorDescription(appContext.getString(CommonInfo.getStringId(appContext, "GplayThirdSdk_errorNetwork")));
                Core.getInstance().setAuthData(authData);

                if (httpsResult != null)
                    httpsResult.onFailure(authData.getErrorDescription());
            }
        });
    }

    public static void bind(final GplayUserInner user, final String phone, final String code, final String serial, final Context appContext, final GplayHttpsResult httpsResult) {
        AsyncHttpClient httpClient = newHttpClient();
        RequestParams params = newRequestParams();
        params.put(Constants.USERNAME, user.getUsername());
        params.put(Constants.ACCESS_TOKEN, GplayUserInner.decode(user.getAccessToken()));
        params.put(Constants.PHONE, phone);
        params.put(Constants.CODE, code);
        params.put(Constants.SERIAL, serial);

        httpClient.post(ThisApp.getUrlBind(), params, new AsyncHttpResponseHandler() {
            @Override
            public void onSuccess(int i, Header[] headers, byte[] bytes) {
                if (bytes != null && bytes.length > 0) {
                    ResponseBind responseBind;
                    try {
                        JSONObject json = new JSONObject(new String(bytes));
                        responseBind = new ResponseBind(json.optString(Constants.SUCCESS), json.optString(Constants.ERROR), json.optString(Constants.ERROR_DESCRIPTION));

                        if (ThisApp.isDebug) {
                            Log.d(TAG, "bind onSuccess :\n" + json);
                        }

                    } catch (JSONException e) {
                        if (ThisApp.isDebug)
                            Log.e(TAG, "bind failed JSONException " + e.getMessage());
                        onFailure(i, headers, bytes, null);
                        return;
                    }

                    //callback data
                    OAuthData authData = new OAuthData();
                    authData.setUid(user.getUid());
                    authData.setIsTrial(user.getIsTrial());

                    if (TextUtils.isEmpty(responseBind.getError())) {
                        user.setPhone(phone);
                        user.setIsLoaded(true);
                        user.setLoginTime(System.currentTimeMillis());
                        DaoControl.getInstance().put(appContext, user);

                        authData.setResultCode(OAuthData.RESULT_CODE_SUCCESS);
                        Core.getInstance().setAuthData(authData);

                        if (httpsResult != null)
                            httpsResult.onSuccess();
                    } else {
                        String errorDescription = responseBind.getError_description();
                        if (TextUtils.isEmpty(errorDescription))
                            errorDescription = appContext.getString(CommonInfo.getStringId(appContext, "GplayThirdSdk_errorSystem"));
                        authData.setResultCode(OAuthData.RESULT_CODE_FAIL);
                        authData.setErrorDescription(errorDescription);
                        Core.getInstance().setAuthData(authData);

                        if (httpsResult != null) {
                            httpsResult.onFailure(errorDescription);
                        }
                    }

                } else {
                    onFailure(i, headers, bytes, null);
                }
            }

            @Override
            public void onFailure(int i, Header[] headers, byte[] bytes, Throwable throwable) {
                if (ThisApp.isDebug)
                    Log.e(TAG, "bind failed " + ((throwable != null) ? throwable.getMessage() : "") + " bytes=" + (bytes != null ? new String(bytes) : ""));

                OAuthData authData = new OAuthData();
                authData.setResultCode(OAuthData.RESULT_CODE_FAIL);
                authData.setErrorDescription(appContext.getString(CommonInfo.getStringId(appContext, "GplayThirdSdk_errorNetwork")));
                Core.getInstance().setAuthData(authData);

                if (httpsResult != null)
                    httpsResult.onFailure(authData.getErrorDescription());
            }
        });
    }

    public static void syncUser(final GplayUserInner user, final Context appContext, final GplayHttpsResult httpsResult) {
        AsyncHttpClient httpClient = newHttpClient();
        RequestParams params = newRequestParams();
        params.put(Constants.ACCESS_TOKEN, GplayUserInner.decode(user.getAccessToken()));

        httpClient.post(ThisApp.getUrlGetUser(), params, new AsyncHttpResponseHandler() {
            @Override
            public void onSuccess(int i, Header[] headers, byte[] bytes) {
                if (bytes != null && bytes.length > 0) {
                    ResponseGetUser responseGetUser;
                    try {
                        JSONObject json = new JSONObject(new String(bytes));
                        responseGetUser = new ResponseGetUser(json.optString(Constants.USERNAME), json.optString(Constants.USER_ID), json.optString(Constants.PHONE), json.optString(Constants.ERROR), json.optString(Constants.ERROR_DESCRIPTION));

                        if (ThisApp.isDebug) {
                            Log.d(TAG, "syncUser onSuccess :\n" + json);
                        }

                    } catch (JSONException e) {
                        if (ThisApp.isDebug)
                            Log.e(TAG, "syncUser failed JSONException " + e.getMessage());
                        onFailure(i, headers, bytes, null);
                        return;
                    }

                    if (TextUtils.isEmpty(responseGetUser.getError()) && !TextUtils.isEmpty(responseGetUser.getUser_id())) {
                        user.setPhone(responseGetUser.getPhone());
                        user.setIsLoaded(true);
                        DaoControl.getInstance().put(appContext, user);

                        if(httpsResult != null)
                            httpsResult.onSuccess();
                    } else {
                        onFailure(i, headers, bytes, null);
                    }
                } else {
                    onFailure(i, headers, bytes, null);
                }
            }

            @Override
            public void onFailure(int i, Header[] headers, byte[] bytes, Throwable throwable) {
                if (ThisApp.isDebug)
                    Log.e(TAG, "syncUser failed " + ((throwable != null) ? throwable.getMessage() : "") + " bytes=" + (bytes != null ? new String(bytes) : ""));
                if(httpsResult != null)
                    httpsResult.onFailure("");
            }
        });
    }

    public interface GplayHttpsResult {
        void onSuccess();

        void onFailure(String error);
    }

    public interface GplayHttpsResult1 {
        void onSuccess(String data);

        void onFailure(String error);
    }

    public static void getCharge(final PayData payData, final Context appContext, final GplayHttpsResult1 httpsResult) {
        AsyncHttpClient httpClient = newHttpClient();
        RequestParams params = newRequestParams();
        params.put("order_sn", payData.getOrder());
        params.put("subject", payData.getName());
        params.put("body", payData.getDesc());
        params.put("extra", payData.getExtra());
        params.put("pay_channel", payData.getChannel());

        httpClient.post(ThisApp.getUrlCharge(), params, new AsyncHttpResponseHandler() {
            @Override
            public void onSuccess(int i, Header[] headers, byte[] bytes) {
                if (bytes != null && bytes.length > 0) {
                    JSONObject json;
                    try {
                        String response = new String(bytes);
                        Log.d(TAG, "onSuccess: " + response);
                        json = new JSONObject(response);

                        if (ThisApp.isDebug) {
                            Log.d(TAG, "getCharge onSuccess :\n" + json.toString());
                        }
                    } catch (JSONException e) {
                        if (ThisApp.isDebug)
                            Log.e(TAG, "getCharge JSONException " + e.getMessage());
                        json = null;
                    }

                    // success
                    if (json != null) {
                        JSONObject jsonResult = json.optJSONObject("result");
                        if(jsonResult != null) {
                            String status = jsonResult.optString("status");
                            if (!TextUtils.isEmpty(status) && status.contentEquals("ok")) {
                                JSONObject jsonData = json.optJSONObject("data");
                                if(jsonData != null) {
                                    String strBase64Charge = jsonData.optString("channel_charge");
                                    byte[] strCharge = Base64.decode(strBase64Charge, Base64.DEFAULT);

                                    try{
                                        JSONObject jsonCharge = new JSONObject(new String(strCharge));
                                        if (httpsResult != null)
                                            httpsResult.onSuccess(jsonCharge.toString());
                                    } catch (JSONException e) {
                                        if (ThisApp.isDebug)
                                            Log.e(TAG, "getCharge JSONException " + e.getMessage());
                                    }
                                    return;
                                }
                            }
                        }
                    }

                    // fail
                    payData.setResultCode(PayData.RESULT_CODE_FAIL);
                    payData.setErrorDescription(appContext.getString(CommonInfo.getStringId(appContext, "GplayThirdSdk_errorSystem")));
                    Core.getInstance().setPayData(payData);

                    if (httpsResult != null)
                        httpsResult.onFailure(payData.getErrorDescription());
                } else {
                    onFailure(i, headers, bytes, null);
                }
            }

            @Override
            public void onFailure(int i, Header[] headers, byte[] bytes, Throwable throwable) {
                if (ThisApp.isDebug)
                    Log.e(TAG, "getCharge failed " + ((throwable != null) ? throwable.getMessage() : "") + " bytes=" + (bytes != null ? new String(bytes) : ""));

                payData.setResultCode(PayData.RESULT_CODE_FAIL);
                payData.setErrorDescription(appContext.getString(CommonInfo.getStringId(appContext, "GplayThirdSdk_errorNetwork")));
                Core.getInstance().setPayData(payData);

                if (httpsResult != null)
                    httpsResult.onFailure(payData.getErrorDescription());
            }
        });
    }
}
