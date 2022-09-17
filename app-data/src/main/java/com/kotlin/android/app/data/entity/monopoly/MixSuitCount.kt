package com.kotlin.android.app.data.entity.monopoly

import com.kotlin.android.app.data.ProguardRule

/**
 * 合成套装统计
 *
 * Created on 2021/7/21.
 *
 * @author o.s
 */
data class MixSuitCount(
    var mixCount: Long = 0, // 已合成套装数
    var totalCount: Long = 0, // 全部套装数
    var earliestCommonCount: Long = 0, // 最早合成的普通套装数
    var earliestLimitCount: Long = 0, // 最早合成的限量套装数
    var earliestTotalCount: Long = 0, // 最早合成的套装数
    var isGrandSlam: Boolean = false, // 是否大满贯
) : ProguardRule
