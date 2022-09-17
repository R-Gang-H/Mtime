package com.kotlin.android.app.data.entity.activity

import com.kotlin.android.app.data.ProguardRule

/**
 * 创建者: vivian.wei
 * 创建时间: 2022/3/17
 * 描述: 活动列表
 */
data class ActivityList(
    var activities: List<Activity>? = null,	// 活动列表
    var activityCount: Long? = null,        // 总数量
    var nextStamp: String? = null,          // 下一页标识
    var hasNext: Boolean? = null,           // 是否有下一页
): ProguardRule
