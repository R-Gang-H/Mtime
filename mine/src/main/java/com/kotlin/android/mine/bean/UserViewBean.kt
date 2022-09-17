package com.kotlin.android.mine.bean

import com.kotlin.android.app.data.ProguardRule
import com.kotlin.android.app.data.entity.user.User
import com.kotlin.android.ktx.ext.orZero

/**
 * 创建者: vivian.wei
 * 创建时间: 2022/3/23
 * 描述:
 */
data class UserViewBean(
        var nickname: String = "",
        var headPic: String = "",
        var userLevel: Long = 0L,        // 会员等级
        var userLevelDesc: String = "",  // 等级描述
        var userAuthType: Long = 0L,     // 用户认证类型：null代表没有认证， 1"个人", 2"影评人", 3"电影人", 4"机构", -1“审核中”;
        var userAuthRole: String = "",   // 认证角色，"电影人"认证类型 比如“认证 导演”“认证 演员”“认证 编剧”

): ProguardRule {
    companion object {
        /**
         * 转换ViewBean
         */
        fun objectToViewBean(bean: User): UserViewBean {
            return UserViewBean(
                    nickname = bean.nickname.orEmpty(),
                    headPic = bean.headPic.orEmpty(),
                    userLevel = bean.userLevel.toLong(),
                    userLevelDesc = bean.userLevelDesc.orEmpty(),
                    userAuthType = bean.userAuthType.orZero(),
                    userAuthRole = bean.userAuthRole.orEmpty(),
            )
        }
    }
}
