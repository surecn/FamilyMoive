package com.surecn.moat.sqliteadmin;

import android.content.Context;

import java.io.IOException;

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
        HttpServer httpServer = new HttpServer(port);
        httpServer.setOnHttpRequest(new SQLiteListHttpHandler(mContext));
        try {
            httpServer.start();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
