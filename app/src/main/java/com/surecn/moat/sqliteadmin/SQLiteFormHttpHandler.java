package com.surecn.moat.sqliteadmin;

import android.content.Context;
import android.text.TextUtils;

import com.surecn.familymovie.common.http.NanoHTTPD;

import java.util.Map;

/**
 * Created by surecn on 17/1/4.
 */

public class SQLiteFormHttpHandler implements HttpServer.OnHttpRequest {

    Context mContext;

    private String mTableName = "favorites";

    private String mDatabase = "launcher.db";

    private int mPageSize = 50;

    private int mCurrentPage = 0;

    public SQLiteFormHttpHandler(Context context) {
        mContext = context;
    }

    @Override
    public NanoHTTPD.Response onHttpRequest(String uri, Map<String, String> paramter) {
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


    String getForms() {
        StringBuffer forms = new StringBuffer("<form>Database:<select name='db'>");
        String[] databaseList = mContext.databaseList();
        for (String database : databaseList) {
            if (database.endsWith(".db")) {
                if (TextUtils.isEmpty(mDatabase)) {
                    mDatabase = database;
                }
                forms.append("<option value=\"volvo\">").append(database).append("</option>");
            }
        }
        forms.append("</select><br><textarea name='sql' rows='5' cols='150'></textarea><br/><input type='submit'></form>");
        return forms.toString();
    }

    String getDetail() {
        return "";
    }


    String getResponse() {
        String forms = getForms();
        String detail = TextUtils.isEmpty(mTableName) ? "" : getDetail();
        return "<html>" + getStyle() + "<body><div>" + forms + "</div><div>" + detail + "</div></body></html>";
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