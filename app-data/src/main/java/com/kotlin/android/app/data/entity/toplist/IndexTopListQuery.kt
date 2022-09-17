package com.kotlin.android.app.data.entity.toplist

import com.kotlin.android.app.data.ProguardRule

/**
 * @author vivian.wei
 * @date 2020/8/27
 * @desc 类描述
 */
data class IndexTopListQuery(
        var pageIndex: Long ?= 0,               // 分页index
        var pageSize: Long ?= 0,                // 分页size
        var hasNext: Boolean ?= false,          // 是否有下页
        var totalCount: Long ?= 0,              // 总记录数
        var items: List<TopListInfo> ?= null    // 当前页记录（榜单列表）
): ProguardRule