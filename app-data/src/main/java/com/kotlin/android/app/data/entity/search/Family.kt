package com.kotlin.android.app.data.entity.search

import com.kotlin.android.app.data.ProguardRule

/**
 * 联合搜索 家族
 */
data class Family(
    var familyId: Long? = 0L,           // 家族id
    var name: String? = "",             // 名称
    var imageUrl: String? = "",         // 图片地址
    var summary: String? = "",          // 简介
    var memberNum: Long? = 0L,          // 成员数
    var isJoin: Long? = 0L,             // 用户类型：已登录 申请者-1、族长1、管理员2、普通成员3、黑名单4、未加入 5
                                        //         未登录 0
    var showIsJoin: Boolean? = false,   // 是否展示是否加入
    var createTime: String? = ""        // 创建时间
): ProguardRule {

    companion object {
        // 用户类型 isJoin
        const val IS_JOIN_LOGOUT = 0L
        const val IS_JOIN_APPLY = -1L
        const val IS_JOIN_CREATOR = 1L
        const val IS_JOIN_MANAGER = 2L
        const val IS_JOIN_MEMBER = 3L
        const val IS_JOIN_BLACK_LIST = 4L
        const val IS_JOIN_NOT_MEMBER = 5L
    }

}