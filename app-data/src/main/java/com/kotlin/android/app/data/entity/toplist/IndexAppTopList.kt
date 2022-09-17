package com.kotlin.android.app.data.entity.toplist

import com.kotlin.android.app.data.ProguardRule

/**
 * @author vivian.wei
 * @date 2020/8/17
 * @desc APP榜单列表
 * http://front-gateway.mtime.cn/library/index/app/topList.api
 * http://apidocs.mt-dev.com/library-front-api/index.html#api-IndexController-appTopList
 */
data class IndexAppTopList(
    var movieTopList: TopListInfos ?= null,                 // 影片榜单
    var movieTopListYearly: MovieTopListYearly ?= null,     // 影片年度片单（所有年度的）
    var tvTopList: TopListInfos ?= null,                    // 电视剧榜单
    var personTopList: TopListInfos ?= null,                // 影人榜单
): ProguardRule