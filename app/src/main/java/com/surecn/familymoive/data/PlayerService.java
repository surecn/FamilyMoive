package com.surecn.familymoive.data;

import com.surecn.familymoive.domain.RecommendInfo;
import com.surecn.familymoive.domain.UpdateInfo;
import com.surecn.moat.net.annotation.FIELD;
import com.surecn.moat.net.annotation.GET;

/**
 * User: surecn(surecn@163.com)
 * Date: 2019-11-05
 * Time: 15:28
 */
public interface PlayerService {

    @GET("/player/update")
    UpdateInfo update(@FIELD("versionCode") long versionCode);

    @GET("/player/main_recommend")
    RecommendInfo recommend();
}
