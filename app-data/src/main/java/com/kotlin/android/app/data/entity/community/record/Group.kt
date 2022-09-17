package com.kotlin.android.app.data.entity.community.record

import com.kotlin.android.app.data.annotation.GroupAuth
import com.kotlin.android.app.data.annotation.GroupRole
import com.kotlin.android.app.data.annotation.PostPermission
import com.kotlin.android.app.data.annotation.UserJoinState

/**
 * 群组
 *
 * Created on 2020/9/29.
 *
 * @author o.s
 */
data class Group(
        var id: Long = 0, // 群组ID
        var name: String? = null, // 群组名称
        var groupImgUrl: String? = null, // 群组封面URL
        var groupDesc: String? = null, // 群组简介
        var memberCount: Long = 0, // 群组人数
        @PostPermission var userPostCommentPmsn: Long = 0, // 当前用户帖子评论权限 1:可评论2:不可评论
        @UserJoinState var userJoin: Long = 0, // 当前用户是否加入此群组 0:未加入1:已加入成功2:加入中（待审核）3:黑名单
        @GroupRole var userGroupRole: Long = 0, // 当前用户群组权限 APPLICANT(-1, "申请者"), OWNER(1, "群主"), ADMINISTRATOR(2, "管理员"), MEMBER(3, "普通成员"), BLACKLIST(4, "黑名单");
        @GroupAuth var groupAuthority: Long = 0 // 群组的发帖和评论的权限 JOIN_POST_JOIN_COMMENT(1, "加入发帖加入评论"), FREE_POST_FREE_COMMENT(2, "自由发帖自由评论"), ADMIN_POST_FREE_COMMENT(3, "管理员发帖自由评论")
)