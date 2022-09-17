package com.kotlin.android.app.data.entity.community.group

import com.kotlin.android.app.data.ProguardRule

/**
 * Created by zhousuqiang on 2020-08-31
 */
data class Group(
    var groupPeopleCountStr: String?,
    var hasJoin: Long = 0L, //当前用户是否加入此群组,0:未加入 1：已加入成功 2 加入中（待审核） 3 黑名单人员 （202003需求变更：只返回状态为0的数据）
    var uploadId: String?, //图片的ID 编辑群组的时候使用
    var primaryCategoryName: String?, //群组一级分类名称
    var bizCode: Long = 0L,
    var groupId: Long = 0L, //
    var joinPermission: Long = 1L, //加入群组的权限 1.任何人均可自由加入 2.需要管理员审核
    var groupImg: String?,
    var groupPeopleCount: Long = 0L,
    var groupPostsCount: Long = 0L,
    var groupCreateUser: GroupUser?,
    var groupName: String?,
    var bizMsg: String?,
    var groupPostsCountStr: String?,
    var userType: Long = 5L, //用户类型 5:未加入（即和本群组没有任何关系） -1:申请者 1：群主 2：管理员 3：普通成员 4：黑名单 （202003需求变更：只返回状态为5的数据）
    var administratorCount: Long = 0L, //群组管理员人数
    var primaryCategoryId: Long = 0L, //群组一级分类ID
    var groupDes: String?,
    var status: Long = 2L, //群组状态 1 待审核 2 审核通过
    var posting: Boolean = false, //是否可以发帖
    var groupAuthority: Long = 0L, //群组的发帖和评论的权限 //加入发帖加入评论 1,"加入发帖加入评论" 自由发帖自由评论 2,"自由发帖自由评论" 管理员发帖自由评论 3,"管理员发帖自由评论"
    var groupJoinUserList: List<GroupUser>?,
    var commenting:Boolean? = false,
    var createTime:String? = "",
    var groupSections:List<GroupSection>? = null
) : ProguardRule



