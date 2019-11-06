package com.surecn.moat.net.httphandler;


import com.surecn.moat.http.HttpForm;
import com.surecn.moat.net.IHttpResult;

/**
 * User: surecn(surecn@163.com)
 * Date: 2015-08-30
 * Time: 14:54
 */
public interface IHttpHandler {
    public IHttpResult request(HttpForm form);
}
