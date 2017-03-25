package com.skydragon.gplay.paysdk.global;

import com.skydragon.gplay.paysdk.tool.AesDecrypt;

/**
 * Created by lindaojiang on 2015/12/9.
 */
public class ThisApp {
    public static final boolean isDebug = true;
//    public static final boolean isDebug = false;

    public static final String UserAgent = "GplayThirdSDK";

    //地址解密秘钥字符串
    private static String getLocalKey() {
//        String key = "e94b301612d623a8";
//        String v1 = key.substring(4, 10);
//        int v2 = Integer.valueOf(v1);
//        int v3 = v2 + 386975;
//        key = key.substring(0,4) + String.valueOf(v3) + key.substring(10, 16);
//        key = "c269d743" + key + "863d1b35";
//        key = key.substring(19, 26) + key.substring(0,11) +  key.substring(26,32) + key.substring(11,19) ;
//        Log.d("ThisApp", "key=" + key);

        String key = "623a886c269d743e943d1b35b688587d";
        key = key.substring(7,18) + key.substring(24,32) + key.substring(0,7) +key.substring(18,24);
        key = key.substring(8, 24);
        int v4 = Integer.valueOf(key.substring(4, 10));
        int v5 = v4-386975;
        key = key.substring(0,4) + String.valueOf(v5) + key.substring(10, 16);
        return key;
    }

    private static String getHost() {
        // SkyDragon 服务器域名地址
        String hostSrc = "i0wOacrSK3s7Ghfxi7qq/OlKjFcgbslSuP2rBEhjlas=";
        String url = "";
        try{
            url = AesDecrypt.decode(hostSrc.getBytes(), getLocalKey());
        }
        catch (Exception ignored) {
        }
        //return url;
        return "https://account.skydragon-inc.cn";
    }

    private static String getGplayHost() {
        // Gplay 服务器域名地址
        return "http://api.skydragon-inc.cn";
    }

    public static String getUrlRegister(){
        return getHost() + "/OAuth/register";
    }

    public static String getUrlGetAutoUser(){
        return getHost() + "/OAuth/createAutoUser";
    }

    public static String getUrlVerifyCode(){
        return getHost() + "/OAuth/getVerifyCode";
    }

    public static String getUrlToken(){
        return getHost() + "/OAuth/token";
    }

    public static String getUrlCharge(){
        return getGplayHost() + "/pay/createCharge";
    }

    public static String getUrlGetUser(){
        return getHost() + "/OAuth/getUserInfo";
    }

    public static String getUrlBind(){
        return getHost() + "/OAuth/bind";
    }
}
