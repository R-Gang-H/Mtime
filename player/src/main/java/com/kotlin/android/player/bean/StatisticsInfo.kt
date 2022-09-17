package com.kotlin.android.player.bean

import com.kotlin.android.app.data.ProguardRule
import java.io.Serializable

/**
 * create by lushan on 2020/9/1
 * description:视频统计信息
 */
class StatisticsInfo(var vid: String? = "",
                     var sourceType: Long = 0L,
                     var pageRefer: String? = "",
                     var pageLabel: String? = "") : ProguardRule, Serializable