package com.kotlin.android.app.data.entity.mine

import com.kotlin.android.app.data.ProguardRule
import com.kotlin.android.app.data.entity.community.medal.MedalData

/**
 *
 * @ProjectName:    b2c
 * @Package:        com.kotlin.android.app.data.entity.mine
 * @ClassName:      CreatorCenterBean
 * @Description:    查看创作者中心首页数据
 * @Author:         haoruigang
 * @CreateDate:     2022/4/6 13:38
 */
data class CreatorCenterBean(
    val collectCount: Long = 0, // 总收藏量
    val commentCount: Long = 0, // 总评论量
    val levelInfo: LevelInfo = LevelInfo(), // 用户等级信息
    val medalCount: Long = 0L, // 创作者勋章数
    val points: Long = 0L, // 创作者积分
    val taskInfos: List<NoviceTaskInfo> = listOf(), // 任务集合
    val upCount: Long = 0,  // 总点赞量
    val userId: Long = 0, // 创作者用户ID
    val viewsCount: Long = 0,   // 总阅读量
    val medalInfos: List<MedalData.MedalInfos>? = listOf(), // 勋章集合
) : ProguardRule