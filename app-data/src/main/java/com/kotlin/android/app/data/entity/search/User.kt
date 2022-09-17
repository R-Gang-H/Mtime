package com.kotlin.android.app.data.entity.search

import com.kotlin.android.app.data.ProguardRule

/**
 * 联合搜索 用户
 */
data class User(
    var nickName: String?  = "",         // 昵称
    var userId: Long? = 0L,              // 用户id
    var headImage: String? = "",         // 头像地址
    var sign: String? = "",              // 签名
    var isAuth: Long? = 0L,              // 是否经过认证
    var authType: Long? = 0L,            // 认证分类 1、个人 2、影评人 3、电影人 4、机构
    var fansCount: Long? = 0L,           // 粉丝数
    var isFocus: Long? = 0L,             // 是否关注 1、关注 0、未关注
    var showUserFocus: Boolean? = false  // 是否显示用户关注
): ProguardRule
