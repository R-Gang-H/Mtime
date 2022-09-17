package com.kotlin.android.app.data.entity.monopoly

/**
 * 卡片大富翁api - 排行榜（/topUserList.api）
 *
 * Created on 2020/9/28.
 *
 * @author o.s
 */
data class Rank(
        var rankName: String? = null, // 榜单名称
        var rankDesc: String? = null, // 榜单描述
        var totalCount: Long = 0, // 榜单人数
        var userList: List<UserList>? = null // 用户列表
) {
    data class UserList(
            var userInfo: UserInfo? = null, // 用户信息
            var orderNumber: Long = 0, // 序号
            var rankFluctuation: Long = 0, // 排名波动（正数为上升，负数为下降）
            var rewardGold: Long = 0, // 奖励金币数
            var rewardGoldDesc: String? = null, // 奖励金描述
            var reasonDesc: String? = null // 入榜理由
    )
}