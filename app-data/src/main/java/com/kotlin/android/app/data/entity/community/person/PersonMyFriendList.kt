package com.kotlin.android.app.data.entity.community.person

import com.kotlin.android.app.data.ProguardRule

/**
 * 我的好友：关注、粉丝列表
 */
data class PersonMyFriendList(
        val hasNext: Boolean,
        val items: List<Item>,
        val pageIndex: Int,
        val pageSize: Int,
        val nextStamp: String,
        val count: Long

) : ProguardRule {
    data class Item(
            var followCount:Long,
            var fansCount:Long,
            val authType: Long,//用户认证类型 PERSONAL(1, "个人"), FILM_CRITIC(2, "影评人"), FILM_MAKER(3, "电影人"), INSTITUTION(4, "机构");
            val avatarUrl: String? = "",
            val followed: Boolean,
            val gender: Long,//性别 1:男2:女3:保密
            val nikeName: String? = "",
            val authRole: String? = "",//认证角色 用户认证类型为"电影人"，才有的角色字段，其余为空
            val userId: Long,
            val userSign: String? = ""//个人签名
    ) : ProguardRule
}

