package com.nthu.softwarestudio.app.vlooog;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import com.nthu.softwarestudio.app.vlooog.data.AccountContract;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by doda on 2016/6/23.
 */
public class LogHelper extends SQLiteOpenHelper {

    public LogHelper(Context context) {
        super(context, LogContract.DATABASE_NAME,null,LogContract.DATABASE_VER);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(LogContract.CREATE_TABLE);
        Log.v("database debug","create new tabe");
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL(LogContract.DROP_TABLE);
        onCreate(db);
        Log.v("database debug","update tabe");
    }

    public boolean insertLog(Integer fromtmp, Integer totmp, String tmpmsg){
        SQLiteDatabase sqLiteDatabase = this.getWritableDatabase();
        ContentValues contentValues = new ContentValues();
        contentValues.put(LogContract.COL_2_MFROM, fromtmp);
        contentValues.put(LogContract.COL_3_MTO, totmp);
        contentValues.put(LogContract.COL_4_MSG, tmpmsg);
        try{
            long result = sqLiteDatabase.insert(LogContract.TABLE_NAME, null, contentValues);
            return result==-1? false: true;
        }finally {
            sqLiteDatabase.close();
        }
    }

    public List<MsgData> getmsg(Integer fromtmp, Integer totmp){
        SQLiteDatabase sqLiteDatabase = this.getWritableDatabase();
        String getUserId = "SELECT * FROM " + LogContract.TABLE_NAME + " WHERE MFROM = " + fromtmp.toString()+" AND MTO = " + totmp.toString() + " OR "+" MFROM = "+ totmp.toString() +" AND MTO = "+ fromtmp.toString();
        Cursor cursor = sqLiteDatabase.rawQuery(getUserId, null);
        List<MsgData> outputtmp = new ArrayList<MsgData>();
        MsgData fintmp = new MsgData("t",1);
        Log.v("test database",getUserId);
        try{
            if(cursor.getCount()==0) return outputtmp;
            while(cursor.moveToNext()){
                fintmp = new MsgData(cursor.getString(cursor.getColumnIndex(LogContract.COL_4_MSG)),cursor.getInt(cursor.getColumnIndex(LogContract.COL_2_MFROM)));
                outputtmp.add(fintmp);
            }
            return outputtmp;
        }finally {
            if(sqLiteDatabase != null) sqLiteDatabase.close();
            if(cursor != null) cursor.close();
        }
    }

}
