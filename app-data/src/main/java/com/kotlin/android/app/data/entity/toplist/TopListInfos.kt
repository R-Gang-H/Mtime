package com.kotlin.android.app.data.entity.toplist

import com.kotlin.android.app.data.ProguardRule

/**
 * @author vivian.wei
 * @date 2020/8/17
 * @desc APP榜单列表_历年的榜单列表
 */
data class TopListInfos(
    var year: Long ?= 0,                         // 年份
    var topListInfos: List<TopListInfo> ?= null // 榜单列表
): ProguardRule