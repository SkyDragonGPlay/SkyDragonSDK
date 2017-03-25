package com.skydragon.gplay.paysdk.data;

public class GplayUserDbTable {
    public static final String DataBaseName = "GplayThirdSdk_Cache"; //数据库名
    public static final int DataBaseVersion = 1;
    public static final String TableName = "gplay_user";

    public static final String createTable = "_id INTEGER PRIMARY KEY AUTOINCREMENT, "
            + "t01 TEXT NOT NULL DEFAULT '', "
            + "t02 TEXT NOT NULL DEFAULT '', "
            + "t03 TEXT, "
            + "t04 TEXT, "
            + "t05 TEXT, "
            + "t06 TEXT, "
            + "t07 TEXT, "
            + "t08 TEXT, "
            + "t09 TEXT, "
            + "t10 TEXT, "
            + "t11 TEXT, "
            + "t12 TEXT, "
            + "t13 TEXT, "
            + "t14 TEXT, "
            + "t15 TEXT, "
            + "t16 TEXT, "
            + "t17 TEXT, "
            + "t18 TEXT, "
            + "t19 TEXT, "
            + "t20 TEXT, "
            + "t21 TEXT, "
            + "t22 TEXT, "
            + "t23 TEXT, "
            + "t24 TEXT, "
            + "t25 TEXT, "
            + "t26 TEXT, "
            + "t27 TEXT, "
            + "t28 TEXT, "
            + "t29 TEXT, "
            + "t30 TEXT, "
            + "i01 INTEGER DEFAULT 0, "
            + "i02 INTEGER DEFAULT 0, "
            + "i03 INTEGER DEFAULT 0, "
            + "i04 INTEGER DEFAULT 0, "
            + "i05 INTEGER DEFAULT 0, "
            + "i06 INTEGER DEFAULT 0, "
            + "i07 INTEGER DEFAULT 0, "
            + "i08 INTEGER DEFAULT 0, "
            + "i09 INTEGER DEFAULT 0, "
            + "i10 INTEGER DEFAULT 0, "
            + "i11 INTEGER DEFAULT 0, "
            + "i12 INTEGER DEFAULT 0, "
            + "i13 INTEGER DEFAULT 0, "
            + "i14 INTEGER DEFAULT 0, "
            + "i15 INTEGER DEFAULT 0";

    public static final String[] tableColumns = {
            GplayUserInner.db_id,
            GplayUserInner.db_username,
            GplayUserInner.db_uid,
            GplayUserInner.db_phone,
            GplayUserInner.db_isTrial,
            GplayUserInner.db_accessToken,
            GplayUserInner.db_tokenType,
            GplayUserInner.db_refreshToken,
            GplayUserInner.db_scope,
            GplayUserInner.db_expiresIn,
            GplayUserInner.db_expiresAt,
            GplayUserInner.db_loginTime,
            GplayUserInner.db_isLoaded
    };
}
