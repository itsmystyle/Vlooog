package com.nthu.softwarestudio.app.vlooog.data;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

/**
 * Created by Ywuan on 22/06/2016.
 */
public class PostHelper extends SQLiteOpenHelper {
    private final String LOG_TAG = this.getClass().getSimpleName();

    public PostHelper(Context context) {
        super(context, PostContract.DATABASE_NAME, null, PostContract.DATABASE_VER);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(PostContract.CREATE_TABLE);
        Log.v(LOG_TAG, "onCreate Table");
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL(PostContract.DROP_TABLE);
        onCreate(db);
        Log.v(LOG_TAG, "onUpgrade database");
    }

    public boolean insertData(int postid, int rate, String rateValue, String comment){
        SQLiteDatabase sqLiteDatabase = this.getWritableDatabase();
        ContentValues contentValues = new ContentValues();
        contentValues.put(PostContract.COL_2_POSTID, postid);
        contentValues.put(PostContract.COL_3_RATE, rate);
        contentValues.put(PostContract.COL_4_RATEVALUE, rateValue);
        contentValues.put(PostContract.COL_5_COMMENT, comment);
        try{
            long result = sqLiteDatabase.insert(PostContract.TABLE_NAME, null, contentValues);

            return result==-1? false: true;
        }finally {
            sqLiteDatabase.close();
        }
    }

    public void deleteData(){
        SQLiteDatabase sqLiteDatabase = this.getWritableDatabase();

        try{
            sqLiteDatabase.delete(PostContract.TABLE_NAME, null, null);
        }finally {
            sqLiteDatabase.close();
        }
    }

    public boolean isEmpty(){
        SQLiteDatabase sqLiteDatabase = this.getWritableDatabase();
        String getData = "SELECT COUNT(*) FROM " + PostContract.TABLE_NAME;
        Cursor cursor = sqLiteDatabase.rawQuery(getData, null);

        try{
            if(cursor != null){
                cursor.moveToNext();
                int count = cursor.getInt(0);
                if(count > 0)
                    return false;
            }

            return true;
        }finally {
            sqLiteDatabase.close();
            cursor.close();
        }
    }

    public int getPostId(){
        SQLiteDatabase sqLiteDatabase = this.getWritableDatabase();
        String getAccessToken = "SELECT * FROM " + PostContract.TABLE_NAME;
        Cursor cursor = sqLiteDatabase.rawQuery(getAccessToken, null);

        try{
            if(cursor == null) return -1;

            cursor.moveToNext();
            return cursor.getInt(cursor.getColumnIndex(PostContract.COL_2_POSTID));
        }finally {
            if(sqLiteDatabase != null) sqLiteDatabase.close();
            if(cursor != null) cursor.close();
        }
    }

    public int getRate(){
        SQLiteDatabase sqLiteDatabase = this.getWritableDatabase();
        String getAccessToken = "SELECT * FROM " + PostContract.TABLE_NAME;
        Cursor cursor = sqLiteDatabase.rawQuery(getAccessToken, null);

        try{
            if(cursor == null) return -1;

            cursor.moveToNext();
            return cursor.getInt(cursor.getColumnIndex(PostContract.COL_3_RATE));
        }finally {
            if(sqLiteDatabase != null) sqLiteDatabase.close();
            if(cursor != null) cursor.close();
        }
    }

    public String getRateValue(){
        SQLiteDatabase sqLiteDatabase = this.getWritableDatabase();
        String getAccessToken = "SELECT * FROM " + PostContract.TABLE_NAME;
        Cursor cursor = sqLiteDatabase.rawQuery(getAccessToken, null);

        try{
            if(cursor == null) return null;

            cursor.moveToNext();
            return cursor.getString(cursor.getColumnIndex(PostContract.COL_4_RATEVALUE));
        }finally {
            if(sqLiteDatabase != null) sqLiteDatabase.close();
            if(cursor != null) cursor.close();
        }
    }

    public String getComment(){
        SQLiteDatabase sqLiteDatabase = this.getWritableDatabase();
        String getAccessToken = "SELECT * FROM " + PostContract.TABLE_NAME;
        Cursor cursor = sqLiteDatabase.rawQuery(getAccessToken, null);

        try{
            if(cursor == null) return null;

            cursor.moveToNext();
            return cursor.getString(cursor.getColumnIndex(PostContract.COL_5_COMMENT));
        }finally {
            if(sqLiteDatabase != null) sqLiteDatabase.close();
            if(cursor != null) cursor.close();
        }
    }
}
