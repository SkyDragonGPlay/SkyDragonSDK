package com.skydragon.gplay.paysdk.h5.common;

import android.content.Context;
import android.provider.Settings;
import android.telephony.TelephonyManager;
import android.text.TextUtils;

import com.skydragon.gplay.paysdk.h5.model.SdkEnvirons;

/**
 * package : com.skydragon.common
 * <p>
 * Description :
 *
 * @author Y.J.ZHOU
 * @date 2016/4/16 16:57.
 */
public class SdkUtils {
    // Get the unique Id of the device
    public static final String uniqId() {
        String unid = "(uniqId)";
        Context ctx = SdkEnvirons.getInstances().getContext();

        TelephonyManager tm = (TelephonyManager) ctx.getSystemService(Context.TELEPHONY_SERVICE);
        unid = tm.getDeviceId();
        if (!TextUtils.isEmpty(unid))
            return unid;

        unid = Settings.Secure.getString(ctx.getContentResolver(), Settings.Secure.ANDROID_ID);
        if (!TextUtils.isEmpty(unid))
            return unid;

        return unid;
    }
}
