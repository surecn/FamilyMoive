package com.surecn.familymoive.data;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.net.Uri;
import android.util.Log;

import com.surecn.familymoive.domain.History;
import java.util.List;

/**
 * User: surecn(surecn@163.com)
 * Date: 2016-08-04
 * Time: 14:41
 */
public class HistoryModel extends BaseModel<History> {

    public HistoryModel(Context context) {
        super(context, "HISTORY");
    }

    public void add(History history) {
        ContentValues contentValues = new ContentValues();
        contentValues.put("type", history.getType());
        contentValues.put("url", history.getUrl());
        contentValues.put("time", history.getTime());
        contentValues.put("position", history.getPosition());
        contentValues.put("length", history.getLength());
        Uri uri = insert(contentValues);
        Log.e("aa", "===insert====" + uri);
    }

    public Uri addVideoHistory(String url, long position, long length) {
        ContentValues contentValues = new ContentValues();
        contentValues.put("type", 0);
        contentValues.put("url", url);
        contentValues.put("time", System.currentTimeMillis());
        contentValues.put("position", position);
        contentValues.put("length", length);
        return insert(contentValues);
    }

    public int update(String url, long position, long length) {
        ContentValues contentValues = new ContentValues();
        contentValues.put("position", position);
        contentValues.put("length", length);
        contentValues.put("time", System.currentTimeMillis());
        int i = update(contentValues, "url=?", new String[]{url});
        Log.e("aa", "===update====" + i);
        return i;
    }

    public void save(String url, long position, long length) {
        if (update(url, position, length) <= 0) {
            addVideoHistory(url, position, length);
        }
    }

    public void clear() {
        delete("1=1", null);
    }

    public List<History> all() {
        return query(null, null, null, "time desc");
    }

    @Override
    public History getRowByCursor(Cursor cursor) {
        History obj = new History();
        obj.setType(cursor.getInt(cursor.getColumnIndex("TYPE")));
        obj.setUrl(cursor.getString(cursor.getColumnIndex("URL")));
        obj.setTime(cursor.getLong(cursor.getColumnIndex("TIME")));
        obj.setPosition(cursor.getInt(cursor.getColumnIndex("POSITION")));
        obj.setLength(cursor.getInt(cursor.getColumnIndex("LENGTH")));
        return obj;
    }
}
