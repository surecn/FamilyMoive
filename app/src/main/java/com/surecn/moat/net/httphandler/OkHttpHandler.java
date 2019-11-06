package com.surecn.moat.net.httphandler;

import com.surecn.moat.http.HttpForm;
import com.surecn.moat.http.OKHttpUtils;
import com.surecn.moat.http.OkHttpResult;
import com.surecn.moat.net.IHttpResult;

/**
 * User: surecn(surecn@163.com)
 * Date: 2015-08-30
 * Time: 15:17
 */
public class OkHttpHandler implements IHttpHandler {
    @Override
    public IHttpResult request(HttpForm form) {
        return new OkHttpResult(OKHttpUtils.request(form));
    }
}
