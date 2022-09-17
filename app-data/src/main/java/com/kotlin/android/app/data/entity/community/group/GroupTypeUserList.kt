package com.kotlin.android.app.data.entity.community.group

import com.kotlin.android.app.data.ProguardRule

/**
 * @author vivian.wei
 * @date 2020/9/11
 * @desc 群组分类成员列表
 */
data class GroupTypeUserList(
     var typeName: String? = null,
     var userList: List<GroupUser>? = null
): ProguardRule