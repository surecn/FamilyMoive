package com.surecn.familymoive.data;

import android.content.ContentProvider;
import android.content.ContentValues;
import android.content.UriMatcher;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteException;
import android.net.Uri;
import android.provider.BaseColumns;
import android.util.Log;

import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

/**
 * Created by surecn on 15/6/5.
 */
public class AppProvider extends ContentProvider {

    private SQLiteDatabase mDataBase;

    public final static String AUTHORITY = "com.surecn.familymoive";

    private final static UriMatcher sUriMatcher;

    private final static int TABLE = 1;

    private final static int TABLE_ID = 2;

    public static Uri getContentUri(String tableName) {
        return Uri.parse("content://" + AUTHORITY + "/" + tableName + "?notify=true");
    }

    public static Uri getContentUriNoNotification(String tableName) {
        return Uri.parse("content://" + AUTHORITY + "/" + tableName + "?notify=false");
    }

    static {
        sUriMatcher = new UriMatcher(UriMatcher.NO_MATCH);
        sUriMatcher.addURI(AUTHORITY, "*", TABLE);
        sUriMatcher.addURI(AUTHORITY, "*/#", TABLE_ID);
    }

    @Override
    public boolean onCreate() {
        Log.e("aa" , "===onCreate====");
        mDataBase = DBHelper.getHelper(getContext()).getWritableDatabase();
        return false;
    }

    @Override
    public Cursor query(Uri uri, String[] projection, String selection, String[] selectionArgs, String sortOrder) {
        String tableName = uri.getPathSegments().get(0);
        Cursor c = mDataBase.query(tableName, projection, selection, selectionArgs, null,
                null, sortOrder);
        //c.setNotificationUri(getContext().getContentResolver(), uri);
        return c;
    }

    @Override
    public String getType(Uri uri) {
        return null;
    }

    @Override
    public Uri insert(Uri uri, ContentValues values) {
        String tableName = uri.getPathSegments().get(0);
        long id = mDataBase.insert(tableName, "", values);
        Uri res =  Uri.withAppendedPath(uri, id + "");
        this.getContext().getContentResolver().notifyChange(res, null);
        return res;
    }

    @Override
    public int delete(Uri uri, String selection, String[] selectionArgs) {
        String tableName = uri.getPathSegments().get(0);
        int res = mDataBase.delete(tableName, selection, selectionArgs);
        this.getContext().getContentResolver().notifyChange(uri, null);
        return res;
    }

    @Override
    public int update(Uri uri, ContentValues values, String selection, String[] selectionArgs) {
        String tableName = uri.getPathSegments().get(0);
        int id = mDataBase.update(tableName, values, selection, selectionArgs);
        Uri res =  Uri.withAppendedPath(uri, id + "");
        this.getContext().getContentResolver().notifyChange(res, null);
        return id;
    }

}
