package com.kotlin.android.app.data.entity.mine

import com.kotlin.android.app.data.ProguardRule

/**
 *
 * @ProjectName:    b2c
 * @Package:        com.kotlin.android.app.data.entity.mine
 * @ClassName:      DataCenterDetailBean
 * @Description:    单篇分析详细说明
 * @Author:         haoruigang
 * @CreateDate:     2022/4/6 15:11
 */
data class DataCenterDetailBean(
    val collectCount: Long = 0,
    val commentCount: Long = 0,
    val contentId: Int = 0,
    val imageUrl: String = "",
    val statisticsDetailsInfos: List<StatisticsDetailsInfo> = listOf(),
    val title: String = "",
    val type: Int = 0,
    val upCount: Long = 0,
    val userCreateTime: UserCreateTime = UserCreateTime(),
    val viewsCount: Long = 0,
    val contentCount: Long = 0,
) : ProguardRule {
    data class StatisticsDetailsInfo(
        val collectCount: Long = 0,
        val commentCount: Long = 0,
        val time: String,
        val upCount: Long = 0,
        val viewsCount: Long = 0,
        val contentCount: Long = 0,
    ) : ProguardRule

    data class UserCreateTime(
        val show: String = "",
        val stamp: Long = 0,
    ) : ProguardRule
}

