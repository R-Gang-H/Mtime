package com.kotlin.android.app.data.entity.mine

import com.kotlin.android.app.data.ProguardRule

data class CommonPush(
    val isMessage: Boolean, // 消息通知提醒是否开启
    val isRemindNewMovie: Boolean, // 上映提醒是否开启
    val isIMPush: Boolean, // 消息Push提醒,是否开启
    val isFilter: Boolean, // 是否过滤影片海报
    val interruptionFreeStart: Long? = 0L, // 免到打扰开始时间点，单位小时，范围0~23。
    val interruptionFreeEnd: Long? = 0L, // 免打扰结束时间点，单位小时，范围1~24.如果小于开始时间认为是次日。
    val isBroadcast: Boolean, // 消息广播提醒是否开启
    val isSwitchCity: Boolean, // 城市切换提醒是否开启
    val isReview: Boolean, // 评论是否开启
    val version: String? = "", // 版本号
    val isUpdateVersion: Boolean // 版本更新提醒是否开启
) : ProguardRule