package com.nthu.softwarestudio.app.vlooog;

/**
 * Created by doda on 2016/6/23.
 */
public class LogContract {
    public static final String DATABASE_NAME = "log.db";
    public static final Integer DATABASE_VER = 1;

    public static final String TABLE_NAME = "log_table";

    public static final String COL_1_ID = "ID";
    public static final String COL_2_MFROM = "MFROM";
    public static final String COL_3_MTO = "MTO";
    public static final String COL_4_MSG = "MSG";

    public static final String CREATE_TABLE = "create table " + TABLE_NAME +
            " (" + COL_1_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
            COL_2_MFROM + " INTEGER NOT NULL, " +
            COL_3_MTO + " INTEGER NOT NULL, " +
            COL_4_MSG + " TEXT)";
    public static final String DROP_TABLE = "DROP TABLE IF EXISTS " + TABLE_NAME;
}
