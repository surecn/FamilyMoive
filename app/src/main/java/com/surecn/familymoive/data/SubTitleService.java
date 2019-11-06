package com.surecn.familymoive.data;

import com.surecn.familymoive.domain.SubDetailItem;
import com.surecn.familymoive.domain.SubTitleItem;
import com.surecn.moat.net.annotation.FIELD;
import com.surecn.moat.net.annotation.GET;

import java.util.List;

/**
 * User: surecn(surecn@163.com)
 * Date: 2019-10-30
 * Time: 11:23
 */
public interface SubTitleService {

    @GET("sub/search")
    List<SubTitleItem> search(@FIELD("q") String q, @FIELD("pos")int pos, @FIELD("cnt")int cnt, @FIELD("is_file")int is_file, @FIELD("no_muxer")int no_muxer);

    @GET("sub/detail")
    List<SubDetailItem> detail(@FIELD("id") int id);

}
