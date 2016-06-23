package com.nthu.softwarestudio.app.vlooog.data;

/**
 * Created by Ywuan on 19/06/2016.
 */
public class AccountContract {
    public static final String DATABASE_NAME = "account.db";
    public static final Integer DATABASE_VER = 1;

    public static final String TABLE_NAME = "account_table";

    public static final String COL_1_ID = "ID";
    public static final String COL_2_USERNAME = "USERNAME";
    public static final String COL_3_ACCESS_TOKEN = "ACCESS_TOKEN";
    public static final String COL_4_USERID = "USERID";
    public static final String COL_5_NICKNAME = "NICKNAME";
    public static final String COL_6_DATAPATH = "DATAPATH";
    public static final String COL_7_EMAIL = "EMAIL";

    public static final String CREATE_TABLE = "create table " + TABLE_NAME +
            " (" + COL_1_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
            COL_2_USERNAME + " TEXT, " +
            COL_3_ACCESS_TOKEN + " TEXT, " +
            COL_4_USERID + " INTEGER, " +
            COL_5_NICKNAME + " TEXT, " +
            COL_6_DATAPATH + " TEXT, " +
            COL_7_EMAIL + " TEXT)";
    public static final String DROP_TABLE = "DROP TABLE IF EXISTS " + TABLE_NAME;
}
