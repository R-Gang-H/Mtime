package com.kotlin.android.app.data.entity.message

import com.kotlin.android.app.data.ProguardRule

/**
 * Created by zhaoninglongfei on 2022/3/21
 * 指定消息的点赞人列表返回
 */
data class PraiseUserResult(
    var nextStamp: String? = null,
    var pageSize: Long? = 0L,
    var items: List<PraiseUser>? = null,
    var hasNext: Boolean? = null//是否有下一页
) : ProguardRule {
    data class PraiseUser(
        var followed: Boolean? = null,//当前用户是否关注该用户 true:已关注 false:未关注 null:用户本人
        var userSign: String? = null,//个人签名
        var unread: Boolean? = null,//是否未读
        var userId: Long? = null,// 用户ID
        var nickName: String? = null,// 用户昵称
        var avatarUrl: String? = null,//头像
        var gender: Long? = 3L,// 性别 1:男2:女3:保密
        var authType: Long? = null,//用户认证类型 PERSONAL(1, "个人"), FILM_CRITIC(2, "影评人"), FILM_MAKER(3, "电影人"), INSTITUTION(4, "机构");
        var authRole: String? = null//认证角色 用户认证类型为"电影人"，才有的角色字段，其余为空
    ) : ProguardRule
}