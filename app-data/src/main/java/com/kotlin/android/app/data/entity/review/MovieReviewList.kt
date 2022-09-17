package com.kotlin.android.app.data.entity.review

import com.kotlin.android.app.data.ProguardRule

data class MovieReviewList (
        var count: Long ?= 0,                       // 已审核列表总记录数
        var hasMore: Boolean ?= false,              // 已审核列表是否有更多
        var list: List<MovieReview> ?= null,        // 已审核影评列表
        var auditingList: List<MovieReview> ?= null // 未审核影评列表（pageIndex=1时返回）
): ProguardRule