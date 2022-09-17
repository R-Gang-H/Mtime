package com.kotlin.android.app.data.entity.mine

import com.kotlin.android.app.data.ProguardRule

/**
 * 任务中心
 */
data class CreatorTaskEntity(
    val activityTaskInfos: List<NoviceTaskInfo>?,//活动任务
    val dailyTaskInfos: List<NoviceTaskInfo>,//日常任务
    val noviceTaskInfos: List<NoviceTaskInfo>,//新手任务
) : ProguardRule

/**
 * 新手任务 活动任务 日常任务
 */
data class NoviceTaskInfo(
    val appLogoUrl: String? = "",//图标
    val count: Long?,//累计值(完成度)
    val details: String = "",//描述
    val endTime: EndTime?,//任务结束日期 包含 >=
    val factor: String? = "",//次数/满足条件 拼接返回汇总字符串
    val points: Long?,//奖励积分
    val startTime: EndTime?,//任务开始日期 包含 <=
    val status: Long?,//状态:1进行中(去完成)/2已完成/3未开始/4已结束
    val taskName: String? = "",//任务名称
    val typeName: String? = "",//类型名称
    var theme: Long?,//分支主题 10:写文章指定字数/20:写帖子指定家族/30:写长影评指定影片/40:发布短视频指定时长/50:被推荐指定频道 /60:被点赞/70:被评论/80:提升等级
    val finishNum: Long?,//分支指定完成数量
    val finishFactor: String? = "",//分支满足条件
    val type: Long?//类型 NOVICE_TASK(20, "新手任务"), DAILY_TASK(21, "日常任务"), ACTIVITY_TASK(22, "活动任务")
) : ProguardRule

/**
 * 结束时间 开始时间
 */
data class EndTime(
    val show: String? = "",
    val stamp: Long,
) : ProguardRule

/**
 * 权益说明
 */
data class RewardEntity(
    val creatorRewardInfos: List<CreatorRewardInfo>,
    val levelInfo: LevelInfo,
) : ProguardRule

/**
 * 权益集合
 */
data class CreatorRewardInfo(
    val appLogoUrl: String?,//app图标url
    val details: String?,//权益描述
    val levelId: Long?,//等级id
    val levelName: String?,//等级名称
    val pcLogoUrl: String?,//pc图标url
    val rewardName: String?,//权益名称
    val status: Long?,//开通标识 1未开通,2已开通
    val rewardId: Long?,//权益id
) : ProguardRule

/**
 * 用户等级信息
 */
data class LevelInfo(
    val appLogoUrl: String? = "",//app等级图标
    val endPoints: Long = 0L,//等级分数区间:结束分数
    val levelId: String? = "",//等级id
    val levelName: String? = "",// 等级名称
    val pcLogoUrl: String? = "",//pc等级图标
    val startPoints: Long = 0L,//等级分数区间:开始分数
) : ProguardRule