package com.kotlin.android.app.data.entity.message

import com.kotlin.android.app.data.ProguardRule

/**
 * Created by zhaoninglongfei on 2022/4/7
 * 用户的关注列表返回
 */
data class UserFollowListResult(
    var count: Long? = null,//总记录数
    var nextStamp: String? = null,
    var pageSize: Long? = 0L,
    var items: List<Follow>? = null,
    var hasNext: Boolean? = null//是否有下一页
) : ProguardRule {
    data class Follow(
        var imId: String? = null,//IM即时通讯Id
        var followCount: Long? = null,//用户关注数
        var fansCount: Long? = null,//用户粉丝数
        var enterTime: EnterTime? = null,// 关注时间
        var followed: Boolean? = null,//当前用户是否关注该用户 true:已关注 false:未关注 null:用户本人
        var userId: Long? = null,// 用户ID
        var nikeName: String? = null,// 用户昵称
        var avatarUrl: String? = null,//头像
        var gender: Long? = 3L,// 性别 1:男2:女3:保密
        var authType: Long? = null,//用户认证类型 PERSONAL(1, "个人"), FILM_CRITIC(2, "影评人"), FILM_MAKER(3, "电影人"), INSTITUTION(4, "机构");
        var authRole: String? = null,//认证角色 用户认证类型为"电影人"，才有的角色字段，其余为空
        var userSign: String? = null//个人签名
    ) : ProguardRule

    //关注时间
    data class EnterTime(
        var stamp: Long? = null,//Unix时间戳:毫秒
        var show: String? = null,//展示 默认格式:yyyy-MM-dd HH:mm:ss
    ) : ProguardRule
}