package com.kotlin.android.app.data.entity.community.group

import com.kotlin.android.app.data.ProguardRule

/**
 * @author vivian.wei
 * @date 2020/8/28
 * @desc 群组成员列表
 */
data class GroupUserList(
        var hasMore: Boolean ?= false,
        var totalCount: Long ?= 0,
        var type: Long ?= 0,            //当前登录用户类型 用户类型
                                        // -1:申请者（审核中的用户）1：群主 2：管理员 3：普通成员 4：黑名单
        var list: List<GroupUser> ?= null
): ProguardRule