package com.surecn.moat.sqliteadmin;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.text.TextUtils;
import com.surecn.familymoive.common.http.NanoHTTPD;
import java.util.Map;

/**
 * Created by surecn on 17/1/4.
 */

public class SQLiteListHttpHandler implements HttpServer.OnHttpRequest {

    Context mContext;

    private String mTableName = "favorites";

    private String mDatabase = "launcher.db";

    private int mPageSize = 50;

    private int mCurrentPage = 0;

    public SQLiteListHttpHandler(Context context) {
        mContext = context;
    }

    @Override
    public NanoHTTPD.Response onHttpRequest(String uri, Map<String, String> paramter) {
        if (uri.endsWith("/favicon.ico")) {
            return null;
        }
        if (paramter != null) {
            mDatabase = paramter.get("db");
            mTableName = paramter.get("table");
            if (paramter.containsKey("page")) {
                try {
                    mCurrentPage = Integer.parseInt(paramter.get("page"));
                } catch (Exception e){}
            }
            if (paramter.containsKey("size")) {
                try {
                    mPageSize = Integer.parseInt(paramter.get("size"));
                } catch (Exception e){}
            }
        }
        NanoHTTPD.Response response = new NanoHTTPD.Response(NanoHTTPD.Response.Status.OK, "text/html", getResponse());
        response.addHeader("Content-Type", "text/html;charset=UTF-8");
        return response;
    }

    String getDatabases() {
        StringBuffer table = new StringBuffer("");

        String[] databaseList = mContext.databaseList();
        for (String database : databaseList) {
            if (database.endsWith(".db")) {
                if (TextUtils.isEmpty(mDatabase)) {
                    mDatabase = database;
                }
                table.append("Database&nbsp;&nbsp;:&nbsp;&nbsp;&nbsp;&nbsp;").append(database).append("<br/>");
                writeTables(table, database);
                table.append("<br/><br/>");
            }
        }
        table.append("");
        return table.toString();
    }

    void writeTables(StringBuffer stringBuffer, String database) {
        SQLiteDatabase sqLiteDatabase = mContext.openOrCreateDatabase(database, Context.MODE_PRIVATE, null);
        Cursor cursor = sqLiteDatabase.rawQuery("SELECT tbl_name FROM sqlite_master WHERE type='table' order by name", null);
        if (cursor != null) {
            int indexName = cursor.getColumnIndex("tbl_name");
            while (cursor.moveToNext()) {
                String name = cursor.getString(indexName);
                if (mTableName == null) {
                    mTableName = name;
                    mDatabase = database;
                }
                stringBuffer.append(String.format("<a href='?db=%s&table=%s&page=0'>%s</a>", database, name, name)).append("&nbsp;&nbsp;&nbsp;&nbsp;");
            }
            cursor.close();
        }
        sqLiteDatabase.close();
    }

    String getUrl(int current, int size) {
        return String.format("?db=%s&table=%s&page=%d&size=%d", mDatabase, mTableName, current, size);
    }

    void applyPage(SQLiteDatabase sqLiteDatabase, StringBuffer stringBuffer) {
        stringBuffer.append("</div>count:");
        Cursor cursor = sqLiteDatabase.rawQuery("select count(*) from " + mTableName, null);
        int count = 0;
        if (cursor != null && cursor.moveToNext()) {
            count = cursor.getInt(0);
            cursor.close();
        }
        stringBuffer.append(count).append("&nbsp;&nbsp;&nbsp;&nbsp;").append("current:").append(mCurrentPage).append("&nbsp;&nbsp;&nbsp;&nbsp;").append("size:").append(mPageSize).append("&nbsp;&nbsp;&nbsp;&nbsp;");
        if (mCurrentPage > 0) {
            stringBuffer.append("<a href='").append(getUrl(mCurrentPage - 1, mPageSize)).append("'>pre</a>&nbsp;&nbsp;&nbsp;&nbsp;");
        }
        if (mCurrentPage < ((count + mPageSize - 1) / mPageSize) - 1) {
            stringBuffer.append("<a href='").append(getUrl(mCurrentPage + 1, mPageSize)).append("'>next</a>");
        }
        stringBuffer.append("</div><br/>");
    }

    String getDetail() {
        if (TextUtils.isEmpty(mTableName)) {
            return "";
        }
        SQLiteDatabase sqLiteDatabase = mContext.openOrCreateDatabase(mDatabase, Context.MODE_PRIVATE, null);
        StringBuffer table = new StringBuffer("");
        applyPage(sqLiteDatabase, table);
        table.append("<table class=\"table\">");
        Cursor cursor = sqLiteDatabase.rawQuery("SELECT * FROM "+ mTableName + " LIMIT " + (mCurrentPage * mPageSize)  + "," + mPageSize, null);
        if (cursor != null) {
            int indexName = cursor.getColumnIndex("tbl_name");
            int types[] = new int[cursor.getColumnCount()];
            table.append("<tr>");
            for (int i = 0; i < cursor.getColumnCount(); i++) {
                String name = cursor.getColumnName(i);
                table.append("<th>").append(name).append("</th>");
                //types[i] = cursor.getType(i);
            }
            table.append("</tr>");
            int row = 0;
            while (cursor.moveToNext()) {
                if (row % 2 == 0) {
                    table.append("<tr>");
                } else {
                    table.append("<tr class=\"alter\">");
                }
                row++;
                for (int i = 0; i < cursor.getColumnCount(); i++) {
                    table.append("<td>");
                    try {
                        table.append(cursor.getString(i));
                    } catch (Exception e) {
                        table.append(cursor.getBlob(i).length);
                    }
                    table.append("</td>");
                }
                table.append("</tr>");
            }
            cursor.close();
        }
        sqLiteDatabase.close();
        table.append("</table>");
        return table.toString();
    }

    String getResponse() {
        String tables = getDatabases();
        String detail = TextUtils.isEmpty(mTableName) ? "" : getDetail();
        return "<html>" + getStyle() + "<body><div>" + tables + "</div><div>" + detail + "</div></body></html>";
    }

    String getStyle() {
        return "<style type=\"text/css\"> \n" +
                "body,table{ \n" +
                "font-size:12px; \n" +
                "} \n" +
                "table{ \n" +
                "table-layout:fixed; " +
                "float:left;\n" +
                "empty-cells:show; \n" +
                "border-collapse: collapse; \n" +
                "margin:0 auto; \n" +
                "} \n" +
                "td{ \n" +
                "height:30px; \n" +
                "} \n" +
                "h1,h2,h3{ \n" +
                "font-size:12px; \n" +
                "margin:0; \n" +
                "padding:0; \n" +
                "} \n" +
                ".table{ \n" +
                "border:1px solid #cad9ea; \n" +
                "color:#666; \n" +
                "} \n" +
                ".table th { \n" +
                "background-repeat:repeat-x; \n" +
                "height:30px; \n" +
                "} \n" +
                ".table td,.table th{ \n" +
                "border:1px solid #cad9ea; \n" +
                "padding:0 1em 0; \n" +
                "} \n" +
                ".table tr.alter{ \n" +
                "background-color:#f5fafe; \n" +
                "} \n" +
                "</style> ";
    }


}