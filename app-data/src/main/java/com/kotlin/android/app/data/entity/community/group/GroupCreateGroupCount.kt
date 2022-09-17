package com.kotlin.android.app.data.entity.community.group

import com.kotlin.android.app.data.ProguardRule

/**
 * @author vivian.wei
 * @date 2020/8/28
 * @desc 用户可创建群组数
 */
data class GroupCreateGroupCount(
        var count: Long ?= 0    // 该用户还能创建的群组的数量
): ProguardRule