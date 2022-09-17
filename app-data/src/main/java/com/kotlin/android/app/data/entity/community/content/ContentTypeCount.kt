package com.kotlin.android.app.data.entity.community.content

import com.kotlin.android.app.data.ProguardRule

data class ContentTypeCount(
    val allDraftCount: Long? = 0L, //全部类型草稿数量
    val allCount: Long? = 0L, //全部数量
    val waitReviewCount: Long? = 0L, //待审核数量
    val reviewFailCount: Long? = 0L, //审核未通过数量
    val releasedCount: Long? = 0L //已发布(审核通过)数量
) : ProguardRule