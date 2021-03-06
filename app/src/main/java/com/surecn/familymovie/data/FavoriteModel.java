package com.surecn.familymovie.data;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import com.surecn.familymovie.domain.Favorite;
import java.util.List;

/**
 * User: surecn(surecn@163.com)
 * Date: 2016-08-04
 * Time: 14:41
 */
public class FavoriteModel extends BaseModel<Favorite> {

    public final static int TYPE_LIVE = 0;
    public final static int TYPE_FOLDER_LAN = 1;
    public final static int TYPE_FOLDER_LOCAL = 2;

    public FavoriteModel(Context context) {
        super(context, "FAVORITE");
    }

    public boolean addLive(int id) {
        ContentValues contentValues = new ContentValues();
        contentValues.put("type", TYPE_LIVE);
        contentValues.put("value", String.valueOf(id));
        contentValues.put("time", System.currentTimeMillis());
        if(insert(contentValues) != null) {
            return true;
        }
        return false;
    }

    public boolean deleteLive(int id) {
        return delete(TYPE_LIVE, String.valueOf(id)) > 0;
    }

    public boolean isExist(String url) {
        List list = query(null, "value=?", new String[]{String.valueOf(url)}, "time desc");
        if (list == null || list.size() <=0) {
            return false;
        }
        return true;
    }

    public boolean addLanFolder(String url, String extra) {
        ContentValues contentValues = new ContentValues();
        contentValues.put("type", TYPE_FOLDER_LAN);
        contentValues.put("value", url);
        contentValues.put("time", System.currentTimeMillis());
        contentValues.put("extra", extra);
        if(insert(contentValues) != null) {
            return true;
        }
        return false;
    }

    public boolean addLocalFolder(String url, String extra) {
        ContentValues contentValues = new ContentValues();
        contentValues.put("type", TYPE_FOLDER_LOCAL);
        contentValues.put("value", url);
        contentValues.put("time", System.currentTimeMillis());
        contentValues.put("extra", extra);
        if(insert(contentValues) != null) {
            return true;
        }
        return false;
    }

    public boolean deleteLanFolder(String value) {
        return delete(TYPE_FOLDER_LAN, value) > 0;
    }

    public boolean deleteLocalFolder(String value) {
        return delete(TYPE_FOLDER_LOCAL, value) > 0;
    }


//    public boolean addFile(String url) {
//        ContentValues contentValues = new ContentValues();
//        contentValues.put("type", TYPE_FILE);
//        contentValues.put("value", url);
//        contentValues.put("time", System.currentTimeMillis());
//        if(insert(contentValues) != null) {
//            return true;
//        }
//        return false;
//    }

    public List<Favorite> getFavoriteLives() {
        return query(null, "TYPE=?", new String[]{String.valueOf(TYPE_LIVE)}, "time desc");
    }

    public List<Favorite> getFavoriteFolders() {
        return query(null, "TYPE=? or TYPE=?", new String[]{String.valueOf(TYPE_FOLDER_LAN), String.valueOf(TYPE_FOLDER_LOCAL)}, "time desc");
    }

    public int delete(int type, String value) {
        return delete("type=? and value=?", new String[]{String.valueOf(type), value});
    }

    public void clear() {
        delete("1=1", null);
    }

    public List<Favorite> all() {
        return query(null, null, null, "time desc");
    }

    @Override
    public Favorite getRowByCursor(Cursor cursor) {
        Favorite obj = new Favorite();
        obj.setType(cursor.getInt(cursor.getColumnIndex("TYPE")));
        obj.setValue(cursor.getString(cursor.getColumnIndex("VALUE")));
        obj.setTime(cursor.getLong(cursor.getColumnIndex("TIME")));
        obj.setExtra(cursor.getString(cursor.getColumnIndex("EXTRA")));
        return obj;
    }
}
