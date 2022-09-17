package com.mtime.bussiness.home.api;

import com.mtime.network.ConstantUrl;

/**
 * Created by ZhouSuQiang on 2017/11/25.
 * home模块统一API地址常量类
 */

public interface HomeApiConstant {
    //home票房页面
    String API_HOME_BOXOFFICE_TABLIST_DETAIL = ConstantUrl.HOST + "/TopList/TopListDetailsByRecommend.api"; //票房排行榜列表信息
    String API_HOME_BOXOFFICE_TABLIST = ConstantUrl.HOST + "/TopList/TopListFixedNew.api"; //票房固定标签

}
