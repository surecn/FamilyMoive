package com.surecn.familymovie.data;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.net.Uri;

import java.util.ArrayList;
import java.util.List;

/**
 * User: surecn(surecn@163.com)
 * Date: 2016-08-03
 * Time: 14:18
 */
public abstract class BaseModel<T> {

    private String mTable;

    private Context mContext;

    private Uri mUri;
    private Uri mUriNoNotify;

    protected BaseModel(Context context, String tableName) {
        mTable = tableName;
        mContext = context;
        mUri = AppProvider.getContentUri(tableName);
        mUriNoNotify = AppProvider.getContentUriNoNotification(tableName);
    }



//    protected long count(String where) {
//        SQLiteDatabase database = null;
//        Cursor cursor = null;
//        try {
//            database = DBHelper.getHelper(mContext).getReadableDatabase();
//            String sql = "SELECT COUNT(*) FROM " + mTable + " WHERE " + where;
//            SQLiteStatement statement = database.compileStatement(sql);
//            return statement.simpleQueryForLong();
//        } catch (Exception e) {
//            e.printStackTrace();
//        } finally {
//            if (cursor != null) {
//                cursor.close();
//            }
//            if (database != null) {
//                database.close();
//            }
//        }
//        return -1;
//    }

    protected List<T> query(String[] columns, String selection,
                        String[] selectionArgs, String orderBy) {
        Cursor cursor = null;
        try {
            cursor = mContext.getContentResolver().query(mUri, columns, selection, selectionArgs, orderBy);
            List<T> list = new ArrayList();
            while (cursor.moveToNext()) {
                T t = getRowByCursor(cursor);
                list.add(t);
            }
            return list;
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (cursor != null) {
                cursor.close();
            }
        }
        return null;
    }

    protected T querySingle(String[] columns, String selection,
                         String[] selectionArgs, String orderBy) {
        Cursor cursor = null;
        try {
            cursor = mContext.getContentResolver().query(mUri, columns, selection, selectionArgs, orderBy);
            if (cursor.moveToNext()) {
                return getRowByCursor(cursor);
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (cursor != null) {
                cursor.close();
            }
        }
        return null;
    }

    protected void save(ContentValues values, String where, String[] whereArgs, boolean notify) {
        if (update(values, where, whereArgs, notify) <= 0) {
            insert(values, notify);
        }
    }

    protected int delete(String whereClause, String[] whereArgs, boolean notify) {
        return mContext.getContentResolver().delete(notify ? mUri : mUriNoNotify, whereClause, whereArgs);
    }

    protected Uri insert(ContentValues values, boolean notify) {
        return mContext.getContentResolver().insert(notify ? mUri : mUriNoNotify, values);
    }

    protected int update(ContentValues values, String where, String[] whereArgs, boolean notify) {
        return mContext.getContentResolver().update(notify ? mUri : mUriNoNotify, values, where, whereArgs);
    }

    protected int delete(String whereClause, String[] whereArgs) {
        return delete(whereClause, whereArgs, true);
    }

    protected Uri insert(ContentValues values) {
        return insert(values, true);
    }

    protected int update(ContentValues values, String where, String[] whereArgs) {
        return update(values, where, whereArgs, true);
    }

    protected void save(ContentValues values, String where, String[] whereArgs) {
        save(values, where, whereArgs, true);
    }


    protected abstract T getRowByCursor(Cursor cursor);
}
