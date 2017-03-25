package com.skydragon.gplay.paysdk;

import android.os.Parcel;
import android.os.Parcelable;

public class OAuthData implements Parcelable{

    public static final int RESULT_CODE_SUCCESS = 1;
    public static final int RESULT_CODE_FAIL = 2;
    public static final int RESULT_CODE_CANCEL = 3;

    private int resultCode;     //RESULT_CODE_XXX
    private String uid;         //user id
    private boolean isTrial;    //true:trial account not registered
    private String errorDescription;    //login error string

    public OAuthData() {
        resultCode = RESULT_CODE_CANCEL;
        uid = null;
        isTrial = true;
        errorDescription = null;
    }

    public int getResultCode() {
        return resultCode;
    }

    public void setResultCode(int resultCode) {
        this.resultCode = resultCode;
    }

    public String getUid() {
        return uid;
    }

    public void setUid(String uid) {
        this.uid = uid;
    }

    public boolean isTrial() {
        return isTrial;
    }

    public void setIsTrial(boolean isTrial) {
        this.isTrial = isTrial;
    }

    public String getErrorDescription() {
        return errorDescription;
    }

    public void setErrorDescription(String errorDescription) {
        this.errorDescription = errorDescription;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeInt(resultCode);
        dest.writeString(uid);
        dest.writeByte((byte) (isTrial ? 1 : 0));
        dest.writeString(errorDescription);
    }

    public OAuthData(Parcel in){
        resultCode = in.readInt();
        uid = in.readString();
        isTrial = in.readByte() == 1;
        errorDescription = in.readString();
    }

    public static final Parcelable.Creator<OAuthData> CREATOR = new Parcelable.Creator<OAuthData>(){
        @Override
        public OAuthData createFromParcel(Parcel source) {
            return new OAuthData(source);
        }

        @Override
        public OAuthData[] newArray(int size) {
            return new OAuthData[size];
        }
    };

    @Override
    public String toString() {
        return "OAuthData{" +
                "resultCode=" + resultCode +
                ", uid='" + uid + '\'' +
                ", isTrial=" + isTrial +
                ", errorDescription='" + errorDescription + '\'' +
                '}';
    }
}
