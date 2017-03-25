package com.skydragon.gplay.paysdk.h5.model;

import com.skydragon.gplay.paysdk.h5.utils.AesDecrypt;

import org.json.JSONException;
import org.json.JSONObject;

/**
 * package : com.skydragon.hybridsdk.model
 * <p/>
 * Description :
 *
 * @author Y.J.ZHOU
 * @date 2016/4/19 21:49.
 */
public class TokenInfo {
    private String  mAppId;
    private String  mAccessToken;
    private String  mRefreshToken;
    private long   mExpireIn;
    private long   mLoginTime;
    private static final String KEY_TOKEN = "9e61e9ed93609df8";
    private String  mTokenType;
    private final long TIME_SPACE = 1000 * 60 * 30;

    private final static String KEY_UID = "user_id";
    private final static String KEY_APP_ID = "app_id";
    private final static String KEY_REFRESH_TOKEN = "refresh_token";
    private final static String KEY_ACCESS_TOKEN = "access_token";
    private final static String KEY_TOKEN_TYPE = "token_type";
    private final static String KEY_EXPIRES_IN = "expires_in";
    private final static String KEY_LOGIN_TIME = "login_time";
    private final static String KEY_SCOPE = "scope";

    public static TokenInfo newFromTokenJsonObj(String appid, JSONObject jsonObject){
        if (jsonObject != null) {
            String user_id = jsonObject.optString(KEY_UID);
            String refresh_token = jsonObject.optString(KEY_REFRESH_TOKEN);
            String access_token = jsonObject.optString(KEY_ACCESS_TOKEN);
            String token_type = jsonObject.optString(KEY_TOKEN_TYPE);
            String scope = jsonObject.optString(KEY_SCOPE);
            long expires_in = Long.parseLong(jsonObject.optString(KEY_EXPIRES_IN));
            Long currentTime = System.currentTimeMillis();

            return new TokenInfo(appid, access_token, refresh_token, expires_in, currentTime);
        }
        return null;
    }

    public static TokenInfo newFromCacheJsonObj(JSONObject jsonObject){

        if(jsonObject == null)
            return null;

        String appid = jsonObject.optString(KEY_APP_ID, null);
        String refreshToken = jsonObject.optString(KEY_REFRESH_TOKEN, null);
        String accessToken = jsonObject.optString(KEY_ACCESS_TOKEN, null);

        if(accessToken != null){
            accessToken = AesDecrypt.aesDecode(accessToken, KEY_TOKEN);
        }

        if(refreshToken != null){
            refreshToken = AesDecrypt.aesDecode(refreshToken, KEY_TOKEN);
        }
        long expireTime = jsonObject.optLong(KEY_EXPIRES_IN, 0L);
        long loginTime = jsonObject.optLong(KEY_LOGIN_TIME, 0L);
        return new TokenInfo(appid, accessToken, refreshToken, expireTime, loginTime);
    }

    public TokenInfo(String appid, String accessToken, String refreshToken, long expireIn, long loginTime){
        this.mAppId = appid;
        this.mAccessToken = accessToken;
        this.mRefreshToken = refreshToken;
        this.mExpireIn = expireIn;
        this.mLoginTime = loginTime;
    }

    public JSONObject toAesJsonObject(){
        JSONObject jsonObject = new JSONObject();
        try {
            jsonObject.put("app_id", mAppId);
            jsonObject.put(KEY_ACCESS_TOKEN, AesDecrypt.aesEncode(mAccessToken, KEY_TOKEN));
            jsonObject.put(KEY_REFRESH_TOKEN, AesDecrypt.aesEncode(mRefreshToken, KEY_TOKEN));
            jsonObject.put(KEY_EXPIRES_IN, mExpireIn);
            jsonObject.put(KEY_LOGIN_TIME, mLoginTime);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return jsonObject;
    }

    public long getExpireAt() {
        return mLoginTime + mExpireIn  * 1000 - TIME_SPACE;
    }

    public String getAppId() {
        return mAppId;
    }

    public long getExpireIn() {
        return mExpireIn;
    }

    public long getLoginTime() {
        return mLoginTime;
    }

    public String getAccessToken() {
        return mAccessToken;
    }

    public String getRefreshToken() {
        return mRefreshToken;
    }

    public String getTokenType() {
        return mTokenType;
    }

    @Override
    public String toString() {
        return "TokenInfo{" +
                "appId='" + mAppId + '\'' +
                ", accessToken=" + mAccessToken +
                ", refreshToken='" + mRefreshToken +
                ", expireIn='" + mExpireIn +
                ", loginTime='" + mLoginTime +
                ", tokenType='" + mTokenType +
                '}';
    }
}
