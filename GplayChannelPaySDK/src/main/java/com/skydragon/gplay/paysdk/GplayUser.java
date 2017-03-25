package com.skydragon.gplay.paysdk;

import android.os.Parcel;
import android.os.Parcelable;

public class GplayUser implements Parcelable {
    private String uid;         //user id, unique
    private String username;    //this is different for trail account after registered
    private String phone;       //phone is null if not bound
    private Boolean isTrial;    //true:trial account
    private String accessToken; //access token
    private String refreshToken;//refresh token
    private Long expiresAt;     //access token expires time, 1970... millisecond
    private Long loginTime;     //latest login time, 1970... millisecond

    public GplayUser(String uid, String username, String phone, Boolean isTrial, String accessToken, String refreshToken, Long expiresAt, Long loginTime) {
        this.uid = uid;
        this.username = username;
        this.phone = phone;
        this.isTrial = isTrial;
        this.accessToken = accessToken;
        this.refreshToken = refreshToken;
        this.expiresAt = expiresAt;
        this.loginTime = loginTime;
    }

    public String getUid() {
        return uid;
    }

    public void setUid(String uid) {
        this.uid = uid;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
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

    public String getRefreshToken() {
        return refreshToken;
    }

    public void setRefreshToken(String refreshToken) {
        this.refreshToken = refreshToken;
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

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(uid);
        dest.writeString(username);
        dest.writeString(phone);
        dest.writeByte((byte)(isTrial?1:0));
        dest.writeString(accessToken);
        dest.writeString(refreshToken);
        dest.writeLong(expiresAt);
        dest.writeLong(loginTime);
    }

    public GplayUser(Parcel in) {
        uid = in.readString();
        username = in.readString();
        phone = in.readString();
        isTrial = in.readByte() == 1;
        accessToken = in.readString();
        refreshToken = in.readString();
        expiresAt = in.readLong();
        loginTime = in.readLong();
    }

    public static final Parcelable.Creator<GplayUser> CREATOR = new Parcelable.Creator<GplayUser>(){
        @Override
        public GplayUser createFromParcel(Parcel source) {
            return new GplayUser(source);
        }

        @Override
        public GplayUser[] newArray(int size) {
            return new GplayUser[size];
        }
    };


    @Override
    public String toString() {
        return "GplayUser{" +
                "uid='" + uid + '\'' +
                ", username='" + username + '\'' +
                ", phone='" + phone + '\'' +
                ", isTrial=" + isTrial +
                ", accessToken='" + accessToken + '\'' +
                ", refreshToken='" + refreshToken + '\'' +
                ", expiresAt=" + expiresAt +
                ", loginTime=" + loginTime +
                '}';
    }
}
