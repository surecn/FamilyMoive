package com.surecn.moat.sqliteadmin;

import android.content.Context;

/**
 * Created by surecn on 17/1/3.
 */

public class SQLiteAdmin {

    private static SQLiteAdmin sSQLiteAdmin;

    private Context mContext;

    public static SQLiteAdmin with(Context context) {
        if (sSQLiteAdmin == null) {
            sSQLiteAdmin = new SQLiteAdmin(context);
        }
        return sSQLiteAdmin;
    }

    public SQLiteAdmin(Context context) {
        mContext = context;
    }

    public void init(int port) {
        new HttpServer(port).setOnHttpRequest(new SQLiteListHttpHandler(mContext));
    }
}
