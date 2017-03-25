package com.skydragon.gplay.paysdk.tool;

import android.util.Base64;

import javax.crypto.Cipher;
import javax.crypto.spec.SecretKeySpec;

public class AesDecrypt {

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
}