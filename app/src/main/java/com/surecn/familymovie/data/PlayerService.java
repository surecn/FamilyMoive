package com.surecn.familymovie.data;

import com.surecn.familymovie.domain.Channel;
import com.surecn.familymovie.domain.LiveRoot;
import com.surecn.familymovie.domain.RecommendInfo;
import com.surecn.familymovie.domain.UpdateInfo;
import com.surecn.moat.net.annotation.FIELD;
import com.surecn.moat.net.annotation.GET;

import java.util.List;

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

    @GET("/player/channels")
    List<LiveRoot> getChannels();
}
