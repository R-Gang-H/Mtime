package com.kotlin.android.community.ui.person.bean

import com.kotlin.android.community.R
import com.kotlin.android.community.ui.person.binder.CommunityPersonFamilyBinder
import com.kotlin.android.app.data.entity.community.person.PersonFamilyList
import com.kotlin.android.app.data.ProguardRule
import com.kotlin.android.mtime.ktx.formatCount
import com.kotlin.android.mtime.ktx.getString

/**
 * 个人主页 家族列表bean
 */
data class PersonFamilyViewBean(var groupId: Long = 0L,//群组id
                                var groupName: String = "",//群组名称
                                var groupImg: String = "",//群组封面
                                var userType: Long = 0L,//用户类型 -1:申请者 1：群主 2：管理员 3：普通成员 4：黑名单 5:未加入（即和本群组没有任何关系）
                                var groupPostsCount: Long = 0L,//群组帖子总数
                                var groupPostsCountStr: String = "",//群组帖子总数show
                                var status: Long = 0,//群组状态 1 待审核 2 审核通过
                                var memberCount: Long = 0,//群组成员数目
                                var desc: String = ""//描述
) : ProguardRule {

    /**
     * 获取成员数量
     */
    fun getFamilyCountContent(): String {
        return getString(R.string.community_family_member_count_format).format(formatCount(if (memberCount < 0) 0 else memberCount))
    }

    companion object {
        fun converter2Binder(item: PersonFamilyList.PersonFamilyItem): CommunityPersonFamilyBinder {
            var data = PersonFamilyViewBean()
            data.groupId = item.groupId
            data.groupName = item.groupName
            data.groupImg = item.groupImg
            data.groupPostsCount = item.groupPostsCount
            data.groupPostsCountStr = item.groupPostsCountStr
            data.memberCount = item.memberCount
            data.desc = item.desc
            return CommunityPersonFamilyBinder(data)
        }
    }
}