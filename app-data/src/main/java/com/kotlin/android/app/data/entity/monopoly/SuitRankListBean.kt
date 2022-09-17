package com.kotlin.android.app.data.entity.monopoly

import com.kotlin.android.app.data.ProguardRule

/**
 * @desc 限量套装展示的列表页面
 * @author zhangjian
 * @date 2020/9/11 10:05
 */
data class SuitRankListBean(
        var earliestUser: UserInfo = UserInfo(),
        var maxinumUser: UserInfo = UserInfo(),
        var suitCover: String = "", // bJK7
        var suitName: String = "", // U9v3k5P
        var userCount: Long = 0, // 8685
        var userList: List<UserInfo> = listOf()
) : ProguardRule