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
        contentValues.put("ACCESS", fileItem.canAccess);
        contentValues.put("SER", fileItem.server);
        contentValues.put("NEEDPASS", fileItem.needPass);
        contentValues.put("CUSTOM", fileItem.custom);
        save(contentValues, "PATH=?", new String[]{fileItem.path});
    }

    public void setAuth(FileItem fileItem, String user, String pass) {
        ContentValues contentValues = new ContentValues();
        contentValues.put("USER", user);
        contentValues.put("PASSWORD", pass);
        contentValues.put("ACCESS", fileItem.canAccess);
        contentValues.put("CUSTOM", fileItem.custom);
        if (update(contentValues, "PATH=?", new String[] {fileItem.path}, false) <= 0) {
            contentValues.put("TYPE", fileItem.getType());
            contentValues.put("NAME", fileItem.getName());
            contentValues.put("PATH", fileItem.getPath());
            contentValues.put("TIME", System.currentTimeMillis());
            contentValues.put("SER", fileItem.server);
            contentValues.put("NEEDPASS", fileItem.needPass);
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
        return query(null, "ACCESS=1", null, "");
    }

    @Override
    public FileItem getRowByCursor(Cursor cursor) {
        FileItem obj = new FileItem();
        obj.setType(cursor.getInt(cursor.getColumnIndex("TYPE")));
        obj.setName(cursor.getString(cursor.getColumnIndex("NAME")));
        obj.setPath(cursor.getString(cursor.getColumnIndex("PATH")));
        obj.setUser(cursor.getString(cursor.getColumnIndex("USER")));
        obj.setPass(cursor.getString(cursor.getColumnIndex("PASSWORD")));
        obj.setCanAccess(cursor.getInt(cursor.getColumnIndex("ACCESS")));
        obj.setServer(cursor.getString(cursor.getColumnIndex("SER")));
        obj.setNeedPass(cursor.getInt(cursor.getColumnIndex("NEEDPASS")));
        int customIndex = cursor.getColumnIndex("CUSTOM");
        if (!cursor.isNull(customIndex)) {
            obj.setCustom(cursor.getInt(customIndex));
        }
        return obj;
    }
}
