package com.surecn.familymoive.data;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteStatement;
import android.net.Uri;
import android.provider.BaseColumns;

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

    protected BaseModel(Context context, String tableName) {
        mTable = tableName;
        mContext = context;
        mUri = AppProvider.getContentUri(tableName);
    }

//    protected void save(ContentValues values, String whereClause, String[] whereArgs) {
//        SQLiteDatabase database = null;
//        try {
//            database = DBHelper.getHelper(mContext).getWritableDatabase();
//            if (whereClause == null) {
//                whereClause = BaseColumns._ID + "=" + values.get(BaseColumns._ID);
//            }
//            int i = database.update(mTable, values, whereClause, whereArgs);
//            if (i <= 0) {
//                database.insert(mTable, null, values);
//            }
//        } catch (Exception e) {
//            e.printStackTrace();
//        } finally {
//            if (database != null) {
//                database.close();
//            }
//        }
//    }

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

    protected int delete(String whereClause, String[] whereArgs) {
        return mContext.getContentResolver().delete(mUri, whereClause, whereArgs);
    }

    protected Uri insert(ContentValues values) {
        return mContext.getContentResolver().insert(mUri, values);
    }

    protected int update(ContentValues values, String where, String[] whereArgs) {
        return mContext.getContentResolver().update(mUri, values, where, whereArgs);
    }

    protected abstract T getRowByCursor(Cursor cursor);
}
