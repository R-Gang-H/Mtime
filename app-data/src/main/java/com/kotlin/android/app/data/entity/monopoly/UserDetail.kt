package com.kotlin.android.app.data.entity.monopoly

import com.kotlin.android.app.data.ProguardRule

/**
 * 卡片大富翁api - 主页用户详情弹层数据（/userDetail.api）
 *
 * Created on 2021/7/23.
 *
 * @author o.s
 */
data class UserDetail(
    var rankInfo: RankInfo? = null, // 排行榜信息
    var mixSuitCount: MixSuitCount? = null, // 合成套装统计数
    var statisticCount: StatisticCount? = null, // 个人统计数据（我的主页专用）
    var suitShowList: List<Suit>? = null, // 套装展示列表
    var userId: Long = 0L, // 用户ID
    var nickName: String? = null, // 用户昵称
    var avatarUrl: String? = null, // 用户头像
    var signature: String? = null, // 用户签名
    var isOnline: Boolean = false, // 是否在线
) : ProguardRule
