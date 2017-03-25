package com.skydragon.gplay.paysdk.global;

import android.content.Context;

import com.skydragon.gplay.paysdk.data.DaoControl;
import com.skydragon.gplay.paysdk.data.GplayUserInner;

/**
 * Created by lindaojiang on 2016/1/8.
 */
public class Test {
    public static void setUserExpires(Context context) {
        if(ThisApp.isDebug) {
            GplayUserInner user = DaoControl.getInstance().getlatestRegisteredUser(context);
            if(user != null && user.getExpiresAt() >= System.currentTimeMillis()) {
                user.setExpiresAt(System.currentTimeMillis()); //token expires, auto login use refresh token
                user.setIsLoaded(false); //sync user information(phone)
                DaoControl.getInstance().put(context, user);
            }
        }
    }

    public static void deleteAllUser(Context context) {
        if(ThisApp.isDebug) {
            DaoControl.getInstance().remove(context, null);
        }
    }
}
