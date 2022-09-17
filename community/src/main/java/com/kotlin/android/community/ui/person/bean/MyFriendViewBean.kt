package com.kotlin.android.community.ui.person.bean

import com.kotlin.android.app.data.ProguardRule
import com.kotlin.android.app.data.entity.community.person.PersonMyFriendList
import com.kotlin.android.community.ui.person.binder.CommunityPersonFriendBinder

/**
 * 关注列表、粉丝列表
 */
class MyFriendViewBean(
    var authType: Long = 0,
    var avatarUrl: String? = "",
    var followed: Boolean = false,
    var gender: Long = 0,
    var nikeName: String? = "",
    var userId: Long = 0,
    var sign: String? = "",
    var followCount: Long = 0L,
    var fansCount: Long = 0L,
    var isHead: Boolean = false,
    var isTail: Boolean = false,
    var isFollowBinder: Boolean = false,//是否是关注 binder
    var count: Long = 0L//总数
) : ProguardRule {
    companion object {
        fun converter2FriendBinder(
            item: PersonMyFriendList.Item,
            isHead: Boolean = false,
            isTail: Boolean = false,
            isFollowBinder: Boolean = false,
            count: Long = 0L
        ): CommunityPersonFriendBinder {
            var data = MyFriendViewBean()
            data.authType = item.authType
            data.avatarUrl = item.avatarUrl
            data.followed = item.followed
            data.gender = item.gender
            data.nikeName = item.nikeName
            data.userId = item.userId
            data.sign = item.userSign
            data.followCount = item.followCount
            data.fansCount = item.fansCount
            data.isHead = isHead
            data.isTail = isTail
            data.isFollowBinder = isFollowBinder
            data.count = count
            return CommunityPersonFriendBinder(data)
        }
    }

    //是不是机构认证用户
    fun isInstitutionAuthUser(): Boolean {
        return authType == 4L
    }

    //是不是认证用户
    fun isAuthUser(): Boolean {
        return authType > 1L
    }
}


