package com.surecn.moat.rest.httphandler;


import com.surecn.moat.http.HttpForm;
import com.surecn.moat.rest.IHttpResult;

/**
 * User: surecn(surecn@163.com)
 * Date: 2015-08-30
 * Time: 14:54
 */
public interface IHttpHandler {
    public IHttpResult request(HttpForm form);
}
