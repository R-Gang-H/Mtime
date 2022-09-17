package com.kotlin.android.app.data.entity.monopoly

import com.kotlin.android.app.data.ProguardRule

/**
 * 卡片大富翁api - 我的卡友（/friendList.api）
 *
 * Created on 2020/9/27.
 *
 * @author o.s
 */
data class Friends(
        var userInfo: UserInfo? = null, // 当前用户信息（pageIndex=1时返回）
        var hasMore: Boolean = false, // 是否有更多
        var totalCount: Long = 0, // 总记录数
        var robotList: List<Robot>? = null, // 机器人好友列表（pageIndex=1时返回）
        var friendList: List<Friend>? = null // 好友列表
) : ProguardRule