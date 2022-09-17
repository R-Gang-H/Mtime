package com.kotlin.android.app.data.entity.community.person

import com.kotlin.android.app.data.ProguardRule

/**
 * 我管理和我加入的群组bean
 */
class PersonFamilyList(val managedGroupList: List<PersonFamilyItem>, val joinedGroupHasMore: Boolean = false, val joinedGroupList: List<PersonFamilyItem>) :
    ProguardRule {
    data class PersonFamilyItem(var groupId: Long = 0L,//群组id
                                var groupName: String = "",//群组名称
                                var groupImg: String = "",//群组封面
                                var userType: Long = 0L,//用户类型 -1:申请者 1：群主 2：管理员 3：普通成员 4：黑名单 5:未加入（即和本群组没有任何关系）
                                var groupPostsCount: Long = 0L,//群组帖子总数
                                var groupPostsCountStr: String = "",//群组帖子总数show
                                var status: Long = 0,//群组状态 1 待审核 2 审核通过
                                var memberCount: Long = 0,//群组成员数目
                                var desc: String = ""
    ) : ProguardRule
}
