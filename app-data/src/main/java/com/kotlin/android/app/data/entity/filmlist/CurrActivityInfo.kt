package com.kotlin.android.app.data.entity.filmlist

import com.kotlin.android.app.data.ProguardRule

/**
 * 创建者: SunHao
 * 创建时间: 2022/3/25
 * 描述:片单活动
 **/
data class CurrActivityInfo(
    val talentStatistics: MutableList<TalentStatistics>? = mutableListOf(),//入围达人信息,最多显示5条记录 (参数type==1的时候有值)
    val nextStamp: String? = "",//下一页标识
    val hasNext: Boolean? = true,// 是否有下一页
    val activitys: MutableList<Activitys>? = mutableListOf(),//活动信息
) : ProguardRule

data class TalentStatistics(
    val userId: Long? = 0,//入围达人id
    val userNickName: String? = "",//入围达人昵称
    val userAvatarUrl: String? = "",//入围达人头像
    val serialNumber: Long? = 0,//顺序号
    val numFinalists: Long? = 0,//入围次数
) : ProguardRule

data class Activitys(
    val activityId: Long? = 0,//活动id
    val title: String? = "",//活动标题、名称
    val coverUrl: String? = "",//封面图url
    val synopsis: String? = "",//简介、说明
    val startTime: Long? = 0,//活动开始时间（单位：毫秒）
    val endTime: Long? = 0,//活动结束时间（单位：毫秒）
    val deliveryManuscriptsPersonTime: Long? = 0,//投稿人数
    var isHistory:Boolean = false//区分历史主题、当前主题
) : ProguardRule