package com.skydragon.gplay.paysdk.h5.model;

import android.content.Context;
import android.util.Log;

import com.skydragon.gplay.paysdk.h5.common.SDKConstant;
import com.skydragon.gplay.paysdk.h5.persister.FilePersister;
import com.skydragon.gplay.paysdk.h5.utils.AesDecrypt;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.IOException;

public class UserInfo {
    private final String        TAG = "UserInfo";
    private String               uid;         //user id, unique
    private boolean             isTrial;    //true:trial account
    private String               session;
    private final String        mClientid;
    private TokenInfo            mTokenInfo;
    private FilePersister        cachePersister;
    private boolean             isLogin;

    private static final String AS_KEY = "2e0de09d2c3f861e";
    private final static String KEY_UID = "user_id";
    private final static String KEY_SESSION = "session";
    private final static String KEY_TOKEN_LIST = "token_list";
    private final static String KEY_IS_TRIAL = "isTrial";

    public UserInfo(String clientId, Context context){
        mClientid = clientId;
        isLogin = false;

        // init userInfo
        File hybridDir = context.getDir("hybrid", Context.MODE_PRIVATE);
        File cacheFile = null;
        if(hybridDir != null){
            String path = hybridDir.getAbsolutePath() + File.separator + "user_info.db";
            cacheFile = new File(path);
            if(!cacheFile.exists()){
                try {
                    cacheFile.createNewFile();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }

        if(cacheFile == null || !cacheFile.exists())
            return;

        cachePersister = new FilePersister(cacheFile);
        refreshUserFromCache();
    }

    public void refreshTokenFromJSInterface(String jsonObjectStr) {

        if(jsonObjectStr == null || jsonObjectStr.equals(""))
            return;

        JSONObject jsonToken;
        try {
            jsonToken = new JSONObject(jsonObjectStr);
        } catch (JSONException e) {
            return;
        }

        isLogin = true;

        TokenInfo tokenInfo = TokenInfo.newFromTokenJsonObj(mClientid, jsonToken);
        if(tokenInfo != null){
            mTokenInfo = tokenInfo;
            persisterCache();
        }
    }

    public void cleanToken(){
        mTokenInfo = null;
    }

    public void refreshUid(String session, String uid, boolean trial) {

        if(session.equals(this.session)
                && uid.equals(this.uid)
                && this.isTrial == trial){
            return;
        }

        if(!session.equals(this.session)){
            cleanCache();
        }

        this.session = session;
        this.uid = uid;
        this.isTrial = trial;

        // persister cache in local file
        persisterCache();
    }

    /*
    *  refresh UserInfo from local cache
    * */
    private void refreshUserFromCache(){

        if(cachePersister == null){
            return;
        }

        String cacheStr = (String)cachePersister.get(new String());

        if(cacheStr == null || cacheStr.equals(""))
            return;

        try {
            JSONObject jsonObject = new JSONObject(cacheStr);
            uid = jsonObject.optString(KEY_UID, null);
            isTrial = jsonObject.optBoolean(KEY_IS_TRIAL, false);
            session = jsonObject.optString(KEY_SESSION, null);
            if(session != null){
                session = AesDecrypt.aesDecode(session, AS_KEY);
            }

            JSONArray jsonArray = jsonObject.optJSONArray(KEY_TOKEN_LIST);

            for(int i = 0; i < jsonArray.length(); i++){
                JSONObject token = jsonArray.getJSONObject(i);
                TokenInfo tokenInfo = TokenInfo.newFromCacheJsonObj(token);
                if(tokenInfo != null
                        && tokenInfo.getAppId() != null
                        && tokenInfo.getAppId().equals(mClientid)){
                    mTokenInfo = tokenInfo;
                }
            }

        } catch (JSONException e) {
            if(SDKConstant.isDebug)
                Log.e(TAG, "refreshUserFromCache userinfo json create error : " + e.toString());
        }
    }

    public void persisterCache(){

        if(cachePersister == null){
            if(SDKConstant.isDebug){
                Log.v(TAG, "UserInfo#persisterCache Error cachePersister is null!!");
            }
            return;
        }

        JSONArray jsonArray = new JSONArray();
        try {
            String cacheStr = (String)cachePersister.get(new String());
            JSONObject jsonCache = new JSONObject(cacheStr);
            JSONArray jsonArrayTmp = jsonCache.optJSONArray(KEY_TOKEN_LIST);
            for(int i = 0; i < jsonArrayTmp.length(); i++){
                JSONObject jsonToken = jsonArrayTmp.getJSONObject(i);
                TokenInfo token = TokenInfo.newFromCacheJsonObj(jsonToken);
                if(!token.getAppId().equals(mClientid)){
                    jsonArray.put(jsonToken);
                }
            }
            if(mTokenInfo != null){
                jsonArray.put(mTokenInfo.toAesJsonObject());
            }
        } catch (JSONException e) {
            if(SDKConstant.isDebug)
                Log.e(TAG, "UserInfo#persisterCache userinfo json create error : " + e.toString());
        }

        JSONObject jsonObject = new JSONObject();
        try {
            jsonObject.put(KEY_UID, uid);
            jsonObject.put(KEY_IS_TRIAL, isTrial);
            jsonObject.put(KEY_SESSION, AesDecrypt.aesEncode(session, AS_KEY));
            jsonObject.put(KEY_TOKEN_LIST, jsonArray);
        } catch (JSONException e) {
            if(SDKConstant.isDebug)
                Log.e(TAG, "UserInfo#persisterCache userinfo json create error : " + e.toString());
        }
        cachePersister.set(jsonObject.toString());
    }

    public void cleanCache(){
        if(cachePersister != null)
            cachePersister.set("");
    }

    public String getUid() {
        return uid;
    }

    public boolean getIsTrial() {
        return isTrial;
    }

    public String getAccessToken() {
        if(mTokenInfo != null)
            return mTokenInfo.getAccessToken();
        else
            return null;
    }

    public String getRefreshToken() {
        if(mTokenInfo != null)
            return mTokenInfo.getRefreshToken();
        else
            return null;
    }

    public boolean isLogin(){
        return isLogin;
    }

    public TokenInfo getTokenInfo() {
        return mTokenInfo;
    }

    @Override
    public String toString() {
        String userinfo = "UserInfo{" +
                "uid='" + uid + '\'' +
                ", isTrial=" + isTrial +
                ", session='" + session +
                ", clientid='" + mClientid +
                '}';

        if(mTokenInfo != null)
            userinfo += mTokenInfo.toString();

        return userinfo;
    }

}
