package com.nthu.softwarestudio.app.vlooog.data;

import android.graphics.Bitmap;

/**
 * Created by Ywuan on 22/06/2016.
 */
public class PostContract {
    public final static String POST_DETAIL = "post-detail";
    public final static String POST_PROFILE_PICTURE = "profile-picture";
    public final static String POST_POFILE_NAME = "profile-name";
    public final static String POST_CONTENT_IMAGE = "content-image";
    public final static String POST_RATING = "rating-bar";
    public final static String POST_RATING_VALUE = "rating-value";
    public final static String POST_CONTENT = "content";
    public final static String POST_COMMENT = "comment";
    public final static String POST_DATE = "date";
    public final static String POST_ID = "post-id";
    public final static String POST_USER_ID = "user-id";

    public final static String DATABASE_NAME = "post.db";
    public final static int DATABASE_VER = 1;
    public final static String TABLE_NAME = "post_table";
    public final static String COL_1_ID = "ID";
    public final static String COL_2_POSTID = "POST_ID";
    public final static String COL_3_RATE = "RATE";
    public final static String COL_4_RATEVALUE = "RATEVALUE";
    public final static String COL_5_COMMENT = "COMMENT";

    public final static String CREATE_TABLE = "create table " + TABLE_NAME +
            " (" + COL_1_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
            COL_2_POSTID + " INTEGER, " +
            COL_3_RATE + " INTEGER, " +
            COL_4_RATEVALUE + " INTEGER, " +
            COL_5_COMMENT + " INTEGER)";
    public final static  String DROP_TABLE = "DROP TABLE IF EXISTS " + TABLE_NAME;

}
