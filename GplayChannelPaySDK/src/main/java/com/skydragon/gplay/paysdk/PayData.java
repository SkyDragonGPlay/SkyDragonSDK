package com.skydragon.gplay.paysdk;

import android.os.Parcel;
import android.os.Parcelable;

public class PayData implements Parcelable {

    public static final int RESULT_CODE_SUCCESS = 1;
    public static final int RESULT_CODE_FAIL = 2;
    public static final int RESULT_CODE_CANCEL = 3;
    public static final int RESULT_CODE_INVALID = 4;

    private int resultCode;     //RESULT_CODE_XXX
    private String errorDescription;    //pay error string

    private String order;   //pay order id
    private String amount;  //pay money amount
    private String name;    //product name
    private String desc;    //product description
    private String extra;   //extra arguments
    private String channel; //pay channel

    public PayData() {
        resultCode = RESULT_CODE_CANCEL;
        errorDescription = null;
        order = null;
        amount = null;
        name = null;
        desc = null;
        extra = null;
        channel = null;
    }

    public int getResultCode() {
        return resultCode;
    }

    public void setResultCode(int resultCode) {
        this.resultCode = resultCode;
    }

    public String getErrorDescription() {
        return errorDescription;
    }

    public void setErrorDescription(String errorDescription) {
        this.errorDescription = errorDescription;
    }

    public String getOrder() {
        return order;
    }

    public void setOrder(String order) {
        this.order = order;
    }

    public String getAmount() {
        return amount;
    }

    public void setAmount(String amount) {
        this.amount = amount;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDesc() {
        return desc;
    }

    public void setDesc(String desc) {
        this.desc = desc;
    }

    public String getExtra() {
        return extra;
    }

    public void setExtra(String extra) {
        this.extra = extra;
    }

    public String getChannel() {
        return channel;
    }

    public void setChannel(String channel) {
        this.channel = channel;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeInt(resultCode);
        dest.writeString(errorDescription);
        dest.writeString(order);
        dest.writeString(amount);
        dest.writeString(name);
        dest.writeString(desc);
        dest.writeString(extra);
        dest.writeString(channel);
    }
    
    public PayData(Parcel in){
        resultCode = in.readInt();
        errorDescription = in.readString();
        order = in.readString();
        amount = in.readString();
        name = in.readString();
        desc = in.readString();
        extra = in.readString();
        channel = in.readString();
    }

    public static final Parcelable.Creator<PayData> CREATOR = new Parcelable.Creator<PayData>(){
        @Override
        public PayData createFromParcel(Parcel source) {
            return new PayData(source);
        }

        @Override
        public PayData[] newArray(int size) {
            return new PayData[size];
        }
    };

    @Override
    public String toString() {
        return "PayData{" +
                "resultCode=" + resultCode +
                ", errorDescription='" + errorDescription + '\'' +
                ", order='" + order + '\'' +
                ", amount='" + amount + '\'' +
                ", name='" + name + '\'' +
                ", desc='" + desc + '\'' +
                ", extra='" + extra + '\'' +
                ", channel='" + channel + '\'' +
                '}';
    }
}
