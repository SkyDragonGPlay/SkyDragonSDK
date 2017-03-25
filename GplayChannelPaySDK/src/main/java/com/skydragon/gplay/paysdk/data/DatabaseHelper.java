package com.skydragon.gplay.paysdk.data;

import android.content.Context;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

/**
 * Created by lindaojiang on 2016/1/20.
 */
public class DatabaseHelper extends SQLiteOpenHelper{
    private static final String TAG = "DatabaseHelper";

    public DatabaseHelper(Context context, String dataBasePath) {
        super(context, dataBasePath, null, GplayUserDbTable.DataBaseVersion);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        try {
            db.execSQL("CREATE TABLE IF NOT EXISTS " + GplayUserDbTable.TableName + "(" + GplayUserDbTable.createTable + ");");
        }
        catch (SQLException e){
            Log.e(TAG, "DataBaseOpenHelper create tables error!!!--!!! " + e.getMessage());
        }
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        Log.w(TAG, "onUpgrade oldVersion=" + oldVersion + " newVersion=" + newVersion);
    }
}
