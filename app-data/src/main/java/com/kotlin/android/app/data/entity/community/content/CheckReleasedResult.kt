package com.kotlin.android.app.data.entity.community.content

import com.kotlin.android.app.data.ProguardRule

/**
 * 创建者: vivian.wei
 * 创建时间: 2022/4/11
 * 描述: 检查内容是否为发布状态Result
 */
data class CheckReleasedResult(
        var isReleased: Boolean? = null, // 内容是否为发布状态
        var title: String? = null,       // 标题
): ProguardRule
