package com.skydragon.gplay.paysdk.h5.utils;

import com.skydragon.gplay.paysdk.h5.model.SdkEnvirons;

/**
 * package : com.skydragon.hybridsdk.utils
 * <p/>
 * Description :
 *
 * @author Y.J.ZHOU
 * @date 2016/4/15 13:55.
 */
public class SDKRes {
    public static String getString(int resId) {
        return SdkEnvirons.getInstances().getContext().getString(resId);
    }
}
