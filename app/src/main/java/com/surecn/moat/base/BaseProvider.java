package com.surecn.moat.base;

/**
 * User: surecn(surecn@163.com)
 * Date: 2016-07-04
 * Time: 14:54
 */

import android.content.ContentProvider;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.UriMatcher;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.database.sqlite.SQLiteQueryBuilder;
import android.net.Uri;
import android.text.TextUtils;

/**
 * Created by surecn on 15/6/5.
 */
public abstract class BaseProvider extends ContentProvider {

    public static final String PARAMETER_NOTIFY = "notify";

    private SQLiteOpenHelper mSQLiteOpenHelper;

    private UriMatcher mUriMatcher;

    private int TABLE = 1;

    private int TABLE_ID = 2;

    public BaseProvider(String authority) {
        mUriMatcher = new UriMatcher(UriMatcher.NO_MATCH);
        mUriMatcher.addURI(authority, "*", TABLE);
        mUriMatcher.addURI(authority, "*/#", TABLE_ID);
    }

    @Override
    public boolean onCreate() {
        mSQLiteOpenHelper = getSQLiteOpenHelper();
        return true;
    }

    public abstract SQLiteOpenHelper getSQLiteOpenHelper();

    @Override
    public Cursor query(Uri uri, String[] projection, String selection, String[] selectionArgs, String sortOrder) {
        SqlArguments args = new SqlArguments(uri, selection, selectionArgs);
        if (!accessCheck(args.table, "query")) {
            return null;
        }

        SQLiteQueryBuilder qb = new SQLiteQueryBuilder();
        qb.setTables(args.table);

        SQLiteDatabase db = mSQLiteOpenHelper.getWritableDatabase();
        Cursor result = qb.query(db, projection, args.where, args.args, null, null, sortOrder);
        result.setNotificationUri(getContext().getContentResolver(), uri);

        return result;
    }

    @Override
    public String getType(Uri uri) {
        return null;
    }

    @Override
    public Uri insert(Uri uri, ContentValues values) {
        SqlArguments args = new SqlArguments(uri);
        if (!accessCheck(args.table, "insert")) {
            return null;
        }

        SQLiteDatabase db = mSQLiteOpenHelper.getWritableDatabase();

        final long rowId = db.insert(args.table, null, values);
        if (rowId <= 0) return null;
        uri = ContentUris.withAppendedId(uri, rowId);
        sendNotify(uri);
        return uri;
    }

    @Override
    public int delete(Uri uri, String selection, String[] selectionArgs) {
        SqlArguments args = new SqlArguments(uri, selection, selectionArgs);
        if (!accessCheck(args.table, "delete")) {
            return -1;
        }

        SQLiteDatabase db = mSQLiteOpenHelper.getWritableDatabase();
        int count = db.delete(args.table, args.where, args.args);
        if (count > 0) sendNotify(uri);

        return count;
    }

    @Override
    public int update(Uri uri, ContentValues values, String selection, String[] selectionArgs) {
        SqlArguments args = new SqlArguments(uri, selection, selectionArgs);
        if (!accessCheck(args.table, "update")) {
            return -1;
        }

        SQLiteDatabase db = mSQLiteOpenHelper.getWritableDatabase();
        int count = db.update(args.table, values, args.where, args.args);
        if (count > 0) sendNotify(uri);
        return count;
    }

    private void sendNotify(Uri uri) {
        String notify = uri.getQueryParameter(PARAMETER_NOTIFY);
        if (notify == null || "true".equals(notify)) {
            getContext().getContentResolver().notifyChange(uri, null);
        }
    }

    public boolean accessCheck(String tableName, String operator) {
        return true;
    }

    static class SqlArguments {
        public final String table;
        public final String where;
        public final String[] args;

        SqlArguments(Uri url, String where, String[] args) {
            if (url.getPathSegments().size() == 1) {
                this.table = url.getPathSegments().get(0);
                this.where = where;
                this.args = args;
            } else if (url.getPathSegments().size() != 2) {
                throw new IllegalArgumentException("Invalid URI: " + url);
            } else if (!TextUtils.isEmpty(where)) {
                throw new UnsupportedOperationException("WHERE clause not supported: " + url);
            } else {
                this.table = url.getPathSegments().get(0);
                this.where = "_id=" + ContentUris.parseId(url);
                this.args = null;
            }
        }

        SqlArguments(Uri url) {
            if (url.getPathSegments().size() == 1) {
                table = url.getPathSegments().get(0);
                where = null;
                args = null;
            } else {
                throw new IllegalArgumentException("Invalid URI: " + url);
            }
        }
    }

}
