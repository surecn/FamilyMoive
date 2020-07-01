package com.surecn.familymovie.data;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.net.Uri;
import android.util.Log;

import com.surecn.familymovie.domain.History;
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
        contentValues.put("root", history.getRoot());
        Uri uri = insert(contentValues);
    }

    public Uri addVideoHistory(String url, long position, long length, String root) {
        ContentValues contentValues = new ContentValues();
        contentValues.put("type", 0);
        contentValues.put("url", url);
        contentValues.put("time", System.currentTimeMillis());
        contentValues.put("position", position);
        contentValues.put("length", length);
        contentValues.put("root", root);
        return insert(contentValues);
    }

    public int update(String url, long position, long length, String root) {
        ContentValues contentValues = new ContentValues();
        contentValues.put("position", position);
        contentValues.put("length", length);
        contentValues.put("time", System.currentTimeMillis());
        contentValues.put("root", root);
        int i = update(contentValues, "url=?", new String[]{url});
        return i;
    }

    public void save(String url, long position, long length, String root) {
        if (update(url, position, length, root) <= 0) {
            addVideoHistory(url, position, length, root);
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
        obj.setRoot(cursor.getString(cursor.getColumnIndex("ROOT")));
        return obj;
    }
}
