package com.kotlin.android.app.data.entity.community.group

import com.kotlin.android.app.data.ProguardRule

/**
 * Created by mtime on 2020-08-28
 */
data class GroupAdminActiveMemberList(
        var administratorList: List<GroupUser> ?= null, // 管理员列表
        var activeMemberList: List<GroupUser> ?= null   // 最近活跃列表
): ProguardRule