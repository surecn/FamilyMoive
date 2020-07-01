package com.surecn.familymovie.data;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import com.surecn.familymovie.domain.FileItem;
import java.util.List;

/**
 * User: surecn(surecn@163.com)
 * Date: 2016-08-04
 * Time: 14:41
 */
public class ServerModel extends BaseModel<FileItem> {

    public ServerModel(Context context) {
        super(context, "SERVER");
    }

    public void add(FileItem fileItem) {
        ContentValues contentValues = new ContentValues();
        contentValues.put("TYPE", fileItem.getType());
        contentValues.put("NAME", fileItem.getName());
        contentValues.put("PATH", fileItem.getPath());
        contentValues.put("TIME", System.currentTimeMillis());
        contentValues.put("ACCESS", String.valueOf(fileItem.canAccess));
        contentValues.put("SER", fileItem.server);
        contentValues.put("NEEDPASS", String.valueOf(fileItem.needPass));
        save(contentValues, "PATH=?", new String[]{fileItem.path});
    }

    public void setAuth(FileItem fileItem, String user, String pass) {
        ContentValues contentValues = new ContentValues();
        contentValues.put("USER", user);
        contentValues.put("PASSWORD", pass);
        contentValues.put("ACCESS", String.valueOf(fileItem.canAccess));
        if (update(contentValues, "PATH=?", new String[] {fileItem.path}, false) <= 0) {
            contentValues.put("TYPE", fileItem.getType());
            contentValues.put("NAME", fileItem.getName());
            contentValues.put("PATH", fileItem.getPath());
            contentValues.put("TIME", System.currentTimeMillis());
            contentValues.put("SER", fileItem.server);
            contentValues.put("NEEDPASS", String.valueOf(fileItem.needPass));
            save(contentValues, "PATH=?", new String[]{fileItem.path});
        }
    }

    public FileItem getByPath(String path) {
        return querySingle(null, "PATH=?", new String[]{path}, null);
    }

    public void add(List<FileItem> fileItems) {
        for (FileItem fileItem : fileItems) {
            add(fileItem);
        }
    }

    public void clear() {
        delete("1=1", null);
    }

    public List<FileItem> all() {
        return query(null, null, null, "");
    }

    @Override
    public FileItem getRowByCursor(Cursor cursor) {
        FileItem obj = new FileItem();
        obj.setType(cursor.getInt(cursor.getColumnIndex("TYPE")));
        obj.setName(cursor.getString(cursor.getColumnIndex("NAME")));
        obj.setPath(cursor.getString(cursor.getColumnIndex("PATH")));
        obj.setUser(cursor.getString(cursor.getColumnIndex("USER")));
        obj.setPass(cursor.getString(cursor.getColumnIndex("PASSWORD")));
        obj.setCanAccess(Boolean.parseBoolean(cursor.getString(cursor.getColumnIndex("ACCESS"))));
        obj.setServer(cursor.getString(cursor.getColumnIndex("SER")));
        obj.setNeedPass(Boolean.parseBoolean(cursor.getString(cursor.getColumnIndex("NEEDPASS"))));
        return obj;
    }
}
