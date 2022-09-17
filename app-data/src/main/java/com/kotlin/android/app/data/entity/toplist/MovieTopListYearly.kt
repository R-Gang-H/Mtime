package com.kotlin.android.app.data.entity.toplist

import com.kotlin.android.app.data.ProguardRule

/**
 * @author vivian.wei
 * @date 2020/8/17
 * @desc APP榜单列表_影片年度片单
 */
data class MovieTopListYearly(
    var years: List<Long> ?= null,                        // 年份列表，倒序(2020,2019,...)
    var topListInfosYearly: List<TopListInfos> ?= null    // 历年的榜单列表
): ProguardRule