package com.kotlin.android.app.data.entity.community.group

import com.kotlin.android.app.data.ProguardRule

/**
 * Created by vivian.wei on 2020-08-28
 * 群组分类
 */
data class GroupCategory(
        var primaryCategoryId: Long = 0,            // 分类id
        var primaryCategoryName: String ?= "",      // 分类名称
        var logo: String ?= "",
        // 自定义
        var isSelect: Boolean = false               // 是否选中
): ProguardRule