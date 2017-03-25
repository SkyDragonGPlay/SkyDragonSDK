package com.skydragon.gplay.paysdk.h5.utils;

import com.skydragon.gplay.paysdk.h5.common.SDKConstant;

/**
 * package : com.skydragon.hybridsdk.model
 *
 * Description :
 *
 * @author Y.J.ZHOU
 * @date 2016/4/11 20:06.
 */
public class GpalyPaySDKUrl {


    public static String SKYDRAGON_HOST_ONLINE = "http://account.skydragon-inc.cn/";
    public static String SKYDRAGON_HOST_DEVELOP = "http://dev.account.skydragon-inc.cn/";
    public static String SKYDRAGON_HOST_SANDBOX = "http://sandbox.account.skydragon-inc.cn/";

    private static String getSkyDragonHosts(){
        switch (SDKConstant.HOST_ENVIRONMENT){
            case SDKConstant.SANDBOX_ENVIRONMENT:
                return SKYDRAGON_HOST_SANDBOX;
            case SDKConstant.DEVELOP_ENVIRONMENT:
                return SKYDRAGON_HOST_DEVELOP;
        }
        return SKYDRAGON_HOST_ONLINE;
    }

    public static String HYBRID = "Hybrid/";

    public static String getLoginUrl(){ return getSkyDragonHosts() + HYBRID + "login";}

    public static String getPreLoginUrl(){ return getSkyDragonHosts() + HYBRID + "prelogin";}

    public static String getRegisterUrl(){ return getSkyDragonHosts() + HYBRID + "register"; }

    public static String getPayUrl(){return getSkyDragonHosts() + HYBRID + "pay"; }

    public static String getBindUrl(){ return getSkyDragonHosts() + HYBRID + "bind"; }

    public static String getRefreshTokenUrl(){ return getSkyDragonHosts() + HYBRID + "refreshToken"; }

    /*public static String getAssetsTestJS(){ return "file:///android_asset/jsfunc.html";}*/
}
