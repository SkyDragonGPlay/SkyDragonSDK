package com.skydragon.gplay.paysdk.data;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteException;
import android.os.Environment;
import android.util.Log;

import com.skydragon.gplay.paysdk.global.ThisApp;

import java.io.File;

public class DaoControl {
    private static final String TAG = "DaoControl";
    private volatile static DaoControl instance = null;

    private DaoControl(){}
    public static DaoControl getInstance() {
        if(instance == null) {
            synchronized (DaoControl.class) {
                if(instance == null) {
                    instance = new DaoControl();
                }
            }
        }
        return instance;
    }

    /**
     * insert or replace data
     * @param context context
     * @param gplayUserInner user
     */
    public synchronized void put(Context context, GplayUserInner gplayUserInner) {
        ContentValues contentValues = new ContentValues();

        if(gplayUserInner.getId() != null)
            contentValues.put(GplayUserInner.db_id, gplayUserInner.getId());

        contentValues.put(GplayUserInner.db_username, gplayUserInner.getUsername());
        contentValues.put(GplayUserInner.db_uid, gplayUserInner.getUid());
        contentValues.put(GplayUserInner.db_phone, gplayUserInner.getPhone());
        contentValues.put(GplayUserInner.db_isTrial, gplayUserInner.getIsTrial());
        contentValues.put(GplayUserInner.db_accessToken, gplayUserInner.getAccessToken());
        contentValues.put(GplayUserInner.db_tokenType, gplayUserInner.getTokenType());
        contentValues.put(GplayUserInner.db_refreshToken, gplayUserInner.getRefreshToken());
        contentValues.put(GplayUserInner.db_scope, gplayUserInner.getScope());
        contentValues.put(GplayUserInner.db_expiresIn, gplayUserInner.getExpiresIn());
        contentValues.put(GplayUserInner.db_expiresAt, gplayUserInner.getExpiresAt());
        contentValues.put(GplayUserInner.db_loginTime, gplayUserInner.getLoginTime());
        contentValues.put(GplayUserInner.db_isLoaded, gplayUserInner.getIsLoaded());

        openDatabase(context);
        if(database != null) {
            if (gplayUserInner.getId() == null) {
                database.insert(GplayUserDbTable.TableName, null, contentValues);
            } else {
                database.update(GplayUserDbTable.TableName, contentValues, GplayUserInner.db_id + "=?", new String[]{String.valueOf(gplayUserInner.getId())});
            }
        }
        closeDatabase();
    }

    /**
     * delete data
     * @param context context
     * @param gplayUserInner delete all if this is null
     */
    public synchronized void remove(Context context, GplayUserInner gplayUserInner){
        openDatabase(context);
        if(database != null) {
            if (gplayUserInner == null)
                database.delete(GplayUserDbTable.TableName, null, null);
            else if (gplayUserInner.getId() != null)
                database.delete(GplayUserDbTable.TableName, GplayUserInner.db_id + "=?", new String[]{String.valueOf(gplayUserInner.getId())});
        }
        closeDatabase();
    }

    /**
     * get latest login user
     * @param context context
     * @param all true:all user, include trial, ignore next param isTrial; false:use next param isTrial
     * @param isTrial param all=false, isTrial=true only return trial user，isTrial=false only return registered user
     * @return GplayUserInner
     */
    private synchronized GplayUserInner getLatestLoginUser(Context context, boolean all, boolean isTrial) {
        String where = null;
        String[] args = null;
        if(!all) {
            where = GplayUserInner.db_isTrial + "=?";
            args = new String[]{isTrial?"1":"0"};
        }

        openDatabase(context);
        if(database == null)
            return null;

        Cursor cursor;
        cursor = database.query(GplayUserDbTable.TableName, GplayUserDbTable.tableColumns, where, args, null, null, GplayUserInner.db_loginTime + " DESC LIMIT 1 OFFSET 0");
        if(cursor == null) {
            return null;
        }

        GplayUserInner gplayUserInner = null;
        if(cursor.moveToNext()) {
            gplayUserInner =  new GplayUserInner();
            gplayUserInner.setId(cursor.getLong(cursor.getColumnIndex(GplayUserInner.db_id)));
            gplayUserInner.setUsername(cursor.getString(cursor.getColumnIndex(GplayUserInner.db_username)));
            gplayUserInner.setUid(cursor.getString(cursor.getColumnIndex(GplayUserInner.db_uid)));
            gplayUserInner.setPhone(cursor.getString(cursor.getColumnIndex(GplayUserInner.db_phone)));
            gplayUserInner.setIsTrial(cursor.getInt(cursor.getColumnIndex(GplayUserInner.db_isTrial)) != 0);
            gplayUserInner.setAccessToken(cursor.getString(cursor.getColumnIndex(GplayUserInner.db_accessToken)));
            gplayUserInner.setTokenType(cursor.getString(cursor.getColumnIndex(GplayUserInner.db_tokenType)));
            gplayUserInner.setRefreshToken(cursor.getString(cursor.getColumnIndex(GplayUserInner.db_refreshToken)));
            gplayUserInner.setScope(cursor.getString(cursor.getColumnIndex(GplayUserInner.db_scope)));
            gplayUserInner.setExpiresIn(cursor.getLong(cursor.getColumnIndex(GplayUserInner.db_expiresIn)));
            gplayUserInner.setExpiresAt(cursor.getLong(cursor.getColumnIndex(GplayUserInner.db_expiresAt)));
            gplayUserInner.setLoginTime(cursor.getLong(cursor.getColumnIndex(GplayUserInner.db_loginTime)));
            gplayUserInner.setIsLoaded(cursor.getInt(cursor.getColumnIndex(GplayUserInner.db_isLoaded)) != 0);
        }
        cursor.close();
        closeDatabase();
        return gplayUserInner;
    }

    /**
     * get latest login user, include trial
     * @param context context
     * @return GplayUserInner
     */
    public GplayUserInner getlatestUser(Context context) {
        return getLatestLoginUser(context, true, false);
    }

    /**
     * get latest login user, only trial
     * @param context context
     * @return GplayUserInner
     */
    public GplayUserInner getlatestTrialUser(Context context) {
        return getLatestLoginUser(context, false, true);
    }

    /**
     * get latest login user, only registered
     * @param context context
     * @return GplayUserInner
     */
    public GplayUserInner getlatestRegisteredUser(Context context) {
        return getLatestLoginUser(context, false, false);
    }

    /**
     * find user
     * @param context context
     * @param username user name
     * @return GplayUserInner
     */
    public synchronized GplayUserInner findUser(Context context, String username){
        openDatabase(context);
        if(database == null)
            return null;

        Cursor cursor = database.query(GplayUserDbTable.TableName, GplayUserDbTable.tableColumns, GplayUserInner.db_username + "=?", new String[]{username}, null, null, GplayUserInner.db_loginTime + " DESC LIMIT 1 OFFSET 0");
        if(cursor == null) {
            return null;
        }

        GplayUserInner gplayUserInner = null;
        if(cursor.moveToFirst()) {
            gplayUserInner =  new GplayUserInner();
            gplayUserInner.setId(cursor.getLong(cursor.getColumnIndex(GplayUserInner.db_id)));
            gplayUserInner.setUsername(cursor.getString(cursor.getColumnIndex(GplayUserInner.db_username)));
            gplayUserInner.setUid(cursor.getString(cursor.getColumnIndex(GplayUserInner.db_uid)));
            gplayUserInner.setPhone(cursor.getString(cursor.getColumnIndex(GplayUserInner.db_phone)));
            gplayUserInner.setIsTrial(cursor.getInt(cursor.getColumnIndex(GplayUserInner.db_isTrial)) != 0);
            gplayUserInner.setAccessToken(cursor.getString(cursor.getColumnIndex(GplayUserInner.db_accessToken)));
            gplayUserInner.setTokenType(cursor.getString(cursor.getColumnIndex(GplayUserInner.db_tokenType)));
            gplayUserInner.setRefreshToken(cursor.getString(cursor.getColumnIndex(GplayUserInner.db_refreshToken)));
            gplayUserInner.setScope(cursor.getString(cursor.getColumnIndex(GplayUserInner.db_scope)));
            gplayUserInner.setExpiresIn(cursor.getLong(cursor.getColumnIndex(GplayUserInner.db_expiresIn)));
            gplayUserInner.setExpiresAt(cursor.getLong(cursor.getColumnIndex(GplayUserInner.db_expiresAt)));
            gplayUserInner.setLoginTime(cursor.getLong(cursor.getColumnIndex(GplayUserInner.db_loginTime)));
            gplayUserInner.setIsLoaded(cursor.getInt(cursor.getColumnIndex(GplayUserInner.db_isLoaded)) != 0);
        }
        cursor.close();
        closeDatabase();
        return gplayUserInner;
    }

    private DatabaseHelper databaseHelper;
    private SQLiteDatabase database;

    private void openDatabase(Context context) {
        String dbPath = GplayUserDbTable.DataBaseName; //app inner storage
        if(Environment.getExternalStorageState().contentEquals(Environment.MEDIA_MOUNTED)) {
            dbPath = Environment.getExternalStorageDirectory() + File.separator + "database";
            File file = new File(dbPath);
            if(!file.exists()) {
                //noinspection ResultOfMethodCallIgnored
                file.mkdirs();
            }
            dbPath += File.separator + GplayUserDbTable.DataBaseName;
        }

        databaseHelper = new DatabaseHelper(context, dbPath);
        try {
            database = databaseHelper.getWritableDatabase(); //for upgrade
        }
        catch (SQLiteException e) {
            if(ThisApp.isDebug)
                Log.e(TAG, "openDatabase SQLiteException " + e.getMessage() + " dbPath=" + dbPath);
            //open inner storage databases
            databaseHelper.close();
            databaseHelper = new DatabaseHelper(context, GplayUserDbTable.DataBaseName);
            try {
                database = databaseHelper.getWritableDatabase();
            }
            catch (SQLiteException ex) {
                if(ThisApp.isDebug)
                    Log.e(TAG, "openDatabase 2 SQLiteException " + ex.getMessage() + " dbPath=" + GplayUserDbTable.DataBaseName);
                database = null;
                databaseHelper.close();
            }
        }
    }

    private void closeDatabase() {
        if(database != null) {
            databaseHelper.close();
            database = null;
        }
    }
}
