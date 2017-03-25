package com.skydragon.gplay.paysdk.data;

import android.text.TextUtils;
import android.util.Log;

import com.skydragon.gplay.paysdk.global.ThisApp;
import com.skydragon.gplay.paysdk.tool.AesDecrypt;

public class GplayUserInner {
    private static final String TAG = "GplayUserInner";

    //database column name
    public static final String db_id = "_id";
    public static final String db_username = "t01";
    public static final String db_uid = "t02";
    public static final String db_phone = "t03";
    public static final String db_isTrial = "i01";
    public static final String db_accessToken = "t04";
    public static final String db_tokenType = "t05";
    public static final String db_refreshToken = "t06";
    public static final String db_scope = "t07";
    public static final String db_expiresIn= "i02";
    public static final String db_expiresAt= "i03";
    public static final String db_loginTime= "i04";
    public static final String db_isLoaded = "i05";

    private Long id;                //primary key
    private String username;        //this is different for trail account after registered
    private String uid;             //user id, unique
    private String phone;           //phone is null if not bound
    private Boolean isTrial;        //true:trial account
    private String accessToken;     //access token, stored encrypted
    private String tokenType;       //token type
    private String refreshToken;    //refresh token, stored encrypted
    private String scope;           //auth scope
    private Long expiresIn;         //access token valid duration time, second(not millisecond)
    private Long expiresAt;         //access token expires time, 1970... millisecond
    private Long loginTime;         //latest login time, 1970... millisecond
    private Boolean isLoaded;       //user information synchronous, true:has checked from server once(ignore multiple devices login case)

    public GplayUserInner() {
    }

    public GplayUserInner(Long id) {
        this.id = id;
    }

    public GplayUserInner(Long id, String username, String uid, String phone, Boolean isTrial, String accessToken, String tokenType, String refreshToken, String scope, Long expiresIn, Long expiresAt, Long loginTime, Boolean isLoaded) {
        this.id = id;
        this.username = username;
        this.uid = uid;
        this.phone = phone;
        this.isTrial = isTrial;
        this.accessToken = accessToken;
        this.tokenType = tokenType;
        this.refreshToken = refreshToken;
        this.scope = scope;
        this.expiresIn = expiresIn;
        this.expiresAt = expiresAt;
        this.loginTime = loginTime;
        this.isLoaded = isLoaded;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getUid() {
        return uid;
    }

    public void setUid(String uid) {
        this.uid = uid;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public Boolean getIsTrial() {
        return isTrial;
    }

    public void setIsTrial(Boolean isTrial) {
        this.isTrial = isTrial;
    }

    public String getAccessToken() {
        return accessToken;
    }

    public void setAccessToken(String accessToken) {
        this.accessToken = accessToken;
    }

    public String getTokenType() {
        return tokenType;
    }

    public void setTokenType(String tokenType) {
        this.tokenType = tokenType;
    }

    public String getRefreshToken() {
        return refreshToken;
    }

    public void setRefreshToken(String refreshToken) {
        this.refreshToken = refreshToken;
    }

    public String getScope() {
        return scope;
    }

    public void setScope(String scope) {
        this.scope = scope;
    }

    public Long getExpiresIn() {
        return expiresIn;
    }

    public void setExpiresIn(Long expiresIn) {
        this.expiresIn = expiresIn;
    }

    public Long getExpiresAt() {
        return expiresAt;
    }

    public void setExpiresAt(Long expiresAt) {
        this.expiresAt = expiresAt;
    }

    public Long getLoginTime() {
        return loginTime;
    }

    public void setLoginTime(Long loginTime) {
        this.loginTime = loginTime;
    }

    public Boolean getIsLoaded() {
        return isLoaded;
    }

    public void setIsLoaded(Boolean isLoaded) {
        this.isLoaded = isLoaded;
    }

    @Override
    public String toString() {
        return "GplayUserInner{" +
                "id=" + id +
                ", username='" + username + '\'' +
                ", uid='" + uid + '\'' +
                ", phone='" + phone + '\'' +
                ", isTrial=" + isTrial +
                ", accessToken='" + accessToken + '\'' +
                ", tokenType='" + tokenType + '\'' +
                ", refreshToken='" + refreshToken + '\'' +
                ", scope='" + scope + '\'' +
                ", expiresIn=" + expiresIn +
                ", expiresAt=" + expiresAt +
                ", loginTime=" + loginTime +
                ", isLoaded=" + isLoaded +
                '}';
    }

    public static String encode(String data){
        if(!TextUtils.isEmpty(data)){
            try{
                return AesDecrypt.encode(data, "3f012cde0e829d6e");
            }
            catch (Exception e){
                if(ThisApp.isDebug)
                    Log.e(TAG, "encode exception " + e.getMessage());
            }
        }
        return null;
    }

    public static String decode(String data) {
        if(!TextUtils.isEmpty(data)) {
            try{
                return AesDecrypt.decode(data.getBytes(), "3f012cde0e829d6e");
            }
            catch (Exception e){
                if(ThisApp.isDebug)
                    Log.e(TAG, "decode exception " + e.getMessage());
            }
        }
        return null;
    }
}
