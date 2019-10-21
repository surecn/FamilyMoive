package com.surecn.moat.http;

import java.io.OutputStream;

/**
 * User: surecn(surecn@163.com)
 * Date: 2016-12-02
 * Time: 11:35
 */
public interface HttpRequestData {

     String getRequestUrl();

     String getPostData();

     void writePutData(OutputStream outputStream);
}
