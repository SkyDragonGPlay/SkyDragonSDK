package com.skydragon.gplay.paysdk.h5.utils;

import android.text.TextUtils;
import android.util.Base64;
import android.util.Log;

import com.skydragon.gplay.paysdk.h5.common.SDKConstant;

import javax.crypto.Cipher;
import javax.crypto.spec.SecretKeySpec;

public class AesDecrypt {

    private static final String TAG = "AesDecrypt";

    public static String encode(String encryptString, String encryptKey) throws Exception {
        SecretKeySpec skeySpec = new SecretKeySpec(encryptKey.getBytes(), "AES");
        Cipher cipher = Cipher.getInstance("AES/ECB/PKCS5Padding");
        cipher.init(Cipher.ENCRYPT_MODE, skeySpec);
        byte[] bytes = cipher.doFinal(encryptString.getBytes("UTF-8"));
        return new String(Base64.encode(bytes, Base64.DEFAULT), "UTF-8");
    }

    public static String decode(byte[] decryptBytes, String decryptKey) throws Exception {
        byte[] data = Base64.decode(decryptBytes, Base64.DEFAULT);
        SecretKeySpec skeySpec = new SecretKeySpec(decryptKey.getBytes(), "AES");
        Cipher cipher = Cipher.getInstance("AES/ECB/PKCS5Padding");
        cipher.init(Cipher.DECRYPT_MODE, skeySpec);
        return new String(cipher.doFinal(data), "UTF-8");
    }

    public static String aesDecode(String data, String key) {
        if(!TextUtils.isEmpty(data)) {
            try{
                return AesDecrypt.decode(data.getBytes(), key);
            }
            catch (Exception e){
                if(SDKConstant.isDebug)
                    Log.v(TAG, "decode exception " + e.getMessage());
            }
        }
        return null;
    }

    public static String aesEncode(String data, String key){
        if(!TextUtils.isEmpty(data)){
            try{
                return AesDecrypt.encode(data, key);
            }
            catch (Exception e){
                if(SDKConstant.isDebug)
                    Log.v(TAG, "ecode exception " + e.getMessage());
            }
        }
        return null;
    }
}