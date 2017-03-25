package com.skydragon.gplay.paysdk.h5.model;

import android.util.Log;

import com.skydragon.gplay.paysdk.h5.common.SDKConstant;
import com.skydragon.gplay.paysdk.h5.common.SdkUtils;
import com.skydragon.gplay.paysdk.h5.persister.FilePersister;
import com.skydragon.gplay.paysdk.h5.utils.AesDecrypt;

import org.json.JSONException;
import org.json.JSONObject;
import java.io.File;
import java.util.UUID;

/**
 * package : com.skydragon.hybridsdk.model
 * <p>
 * Description :
 *
 * @author Y.J.ZHOU
 * @date 2016/4/19 14:10.
 */
public class DeviceInfo {

    private String mUUID;
    private String mUniqueId;
    private String mSession;
    private String mSessionUid;
    private long mSessionExpireTime;
    private FilePersister filePersister;

    private static final String DEV_KEY = "2c3f82e0de09d61e";
    private static final String DEV_INFO = "devinfo.db";
    private static final String KEY_UUID = "dev_info_UUID";
    private static final String KEY_SESSION = "dev_info_Session";
    private static final String KEY_SESSION_UID = "dev_info_Session_uid";
    private static final String KEY_SESSION_ET = "dev_info_Session_et";

    public DeviceInfo(String hybridDir){

        File mFolder = new File(hybridDir);
        if (!mFolder.exists()) {
            mFolder.mkdirs();
        }

        filePersister = new FilePersister(mFolder.getAbsolutePath() + File.separator + DEV_INFO);

        // refresh deviceinfo from persister file
        refreshFromPersister();

        if(mUUID == null || mUUID.equals("")){
            UUID uuid = UUID.randomUUID();
            mUUID = uuid.toString();
        }

        mUniqueId = SdkUtils.uniqId();

        persistDeviveInfo();
    }

    public String getUUId(){
        return mUUID;
    }

    public String getUniqueId(){
        return mUniqueId;
    }

    public void refreshSession(String session, String uid, Long expireTime){
        mSession = session;
        mSessionUid = uid;
        mSessionExpireTime = expireTime;
        persistDeviveInfo();
    }


    private void persistDeviveInfo(){
        JSONObject jsonObject = new JSONObject();
        try {
            jsonObject.put(KEY_UUID, mUUID);
            jsonObject.put(KEY_SESSION, mSession);
            jsonObject.put(KEY_SESSION_UID, mSessionUid);
            jsonObject.put(KEY_SESSION_ET, mSessionExpireTime);
        } catch (JSONException e) {
            e.printStackTrace();
        }

        if(filePersister != null){
            filePersister.set(AesDecrypt.aesEncode(jsonObject.toString(), DEV_KEY));
        }
    }

    private DeviceInfo refreshFromPersister(){
        if(filePersister != null){
            String jsonStr = AesDecrypt.aesDecode((String)filePersister.get(new String()), DEV_KEY);

            if(jsonStr != null){
                try {
                    JSONObject jsonObject = new JSONObject(jsonStr);
                    mUUID = jsonObject.optString(KEY_UUID, null);
                    mSession = jsonObject.optString(KEY_SESSION, null);
                    mSessionUid = jsonObject.optString(KEY_SESSION_UID, null);
                    mSessionExpireTime = jsonObject.optLong(KEY_SESSION_ET);

                } catch (JSONException e) {
                    if(SDKConstant.isDebug){
                        Log.v("", "error " + e.toString());
                    }
                }
            }
        }
        return this;
    }

    public String getSessionUid() {
        return mSessionUid;
    }

    public String getSession(){
        return  mSession;
    }

    public long getSessionExpireTime(){
        return  mSessionExpireTime;
    }
}
