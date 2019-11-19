package com.surecn.familymovie.data;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

/**
 * User: surecn(surecn@163.com)
 * Date: 2016-06-03
 * Time: 14:24
 */
public class DBHelper extends SQLiteOpenHelper {

    public static DBHelper getHelper(Context context) {
        if (instance == null) {
            instance = new DBHelper(context);
        }
        return instance;
    }

    /**
     * 数据库名字
     */
    private static final String DB_NAME = "data.db";
    /**
     * 数据库版本
     */
    private static final int DB_VERSION = 2;


    private static DBHelper instance;

    private Context mContext;

    public DBHelper(Context context) {
        super(context, DB_NAME, null, DB_VERSION);
        mContext = context;
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        try {
            db.execSQL("CREATE TABLE HISTORY ( TYPE INTEGER, URL TEXT PRIMARY KEY, TIME INTEGER, POSITION INTEGER, LENGTH INTEGER);");
            db.execSQL("CREATE TABLE FAVORITE ( TYPE INTEGER, VALUE TEXT PRIMARY KEY, TIME INTEGER);");
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        if (newVersion >= 2) {
            db.execSQL("CREATE TABLE FAVORITE ( TYPE INTEGER, VALUE TEXT PRIMARY KEY, TIME INTEGER);");
        }
    }

}
