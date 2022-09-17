package com.kotlin.android.app.data.entity.community.group

import com.kotlin.android.app.data.ProguardRule

/**
 * @author vivian.wei
 * @date 2020/8/28
 * @desc 群组成员
 */
data class GroupUser(
        var userId: Long ?= 0,	        // 用户ID
        var userName: String ?= "",     // 用户名
        var userImg: String ?= "",      // 用户头像
        var userType: Long ?= 0,        // 用户类型 -1:申请者 1：群主 2：管理员 3：普通成员
                                        //         4：黑名单 5:未加入（即和本群组没有任何关系）
        var followed: Boolean ?= false, // 是否被关注

        //自定义
        var checked: Boolean = false    // 是否选中
): ProguardRule {
    companion object {
        // 用户类型
        const val USER_TYPE_APPLY: Long = -1
        const val USER_TYPE_CREATOR: Long = 1
        const val USER_TYPE_ADMINISTRATOR: Long = 2
        const val USER_TYPE_MEMBER: Long = 3
        const val USER_TYPE_BLACK_LIST: Long = 4
    }
}