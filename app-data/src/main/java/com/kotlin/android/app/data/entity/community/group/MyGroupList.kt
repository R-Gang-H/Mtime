package com.kotlin.android.app.data.entity.community.group

import com.kotlin.android.app.data.ProguardRule

/**
 * Created by zhousuqiang on 2020-08-28
 *
 * 我加入的和我管理的群组
 */
data class MyGroupList(
    var joinedGroupHasMore: Boolean = false,
    var managedGroupList: List<Group>? = null,
    var joinedGroupList: List<Group>? = null
) : ProguardRule {

    data class Group(
        var groupName: String? = null,
        var name: String? = null,
        var groupId: Long = 0,
        var groupImg: String? = null,
        var userType: Long = 0, //用户类型 -1:申请者 1：群主 2：管理员 3：普通成员 4：黑名单 5:未加入（即和本群组没有任何关系）
        var groupPostsCountStr: String? = null,
        var groupPostsCount: Long = 0,
        var groupAuthority: Long? = null, //群组的发帖和评论的权限 1,"加入发帖加入评论" 2,"自由发帖自由评论" 3,"管理员发帖自由评论" null, 无此属性，在待审核家族里
        var memberCount: Long? = null, // Number用户数量，待审核家族此字段无用
        var status: Long = 0 //群组状态 1 待审核 2 审核通过
    ) : ProguardRule
}