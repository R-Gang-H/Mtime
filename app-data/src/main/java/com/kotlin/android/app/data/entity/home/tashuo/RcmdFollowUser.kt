package com.kotlin.android.app.data.entity.home.tashuo

import com.kotlin.android.app.data.ProguardRule

data class RcmdFollowUser(
    var fansCount: Long = 0,
    var latestContent: LatestContent? = null,
    var followed: Boolean = false,
    var userSign: String? = null,
    var userId: Long = 0,
    var nikeName: String? = "",
    var avatarUrl: String? = "",
    var gender: Long = 0, //性别 1:男2:女3:保密
    var authType: Long = 1, //用户认证类型 PERSONAL(1, "个人"), FILM_CRITIC(2, "影评人"), FILM_MAKER(3, "电影人"), INSTITUTION(4, "机构");
    var authRole: String? = "" //认证角色 用户认证类型为"电影人"，才有的角色字段，其余为空
): ProguardRule {

    data class LatestContent(
        var contentId: Long = 0,
        var type: Long = 0,
        var title: String = ""
    )
}

