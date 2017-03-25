package com.skydragon.gplay.paysdk.h5.model;

import android.util.Log;

import com.skydragon.gplay.paysdk.h5.common.SDKConstant;

import org.json.JSONException;
import org.json.JSONObject;

/**
 * package : com.skydragon.hybridsdk.model
 * <p/>
 * Description :
 *
 * @author Y.J.ZHOU
 * @date 2016/4/21 10:59.
 */
public class OrderInfo {
    private final String TAG = "OrderInfo";

    private String mOrderSN;
    private String mAmount;
    private String mGoodsName;
    private String mDesc;
    private String mExtra;

    public OrderInfo(String order, String amount, String name, String desc, String extra){
        mOrderSN = order;
        mAmount = amount;
        mGoodsName  = name;
        mDesc = desc;
        mExtra = extra;
    }

    public JSONObject toJsonObj(){
        JSONObject jsonObject = new JSONObject();
        try {
            jsonObject.put("order_sn", mOrderSN);
            jsonObject.put("amount", mAmount);
            jsonObject.put("name", mGoodsName);
            jsonObject.put("desc", mDesc);
            jsonObject.put("extra", mExtra);
        } catch (JSONException e) {
            if(SDKConstant.isDebug)
                Log.e(TAG, "persisterCache userinfo json create error : " + e.toString());
        }
        return jsonObject;
    }

    public String getAmount() {
        return mAmount;
    }

    public String getDesc() {
        return mDesc;
    }

    public String getExtra() {
        return mExtra;
    }

    public String getOrderSN() {
        return mOrderSN;
    }

    public String getGoodsName() {
        return mGoodsName;
    }
}
