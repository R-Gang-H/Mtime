package com.kotlin.android.app.data.entity.monopoly

import com.kotlin.android.app.data.ProguardRule

/**
 * 套装信息
 *
 * Created on 2020/9/27.
 *
 * @author o.s
 */
data class SuitInfo(
        var suitCList: List<Suit>? = null, // 简装版套装top3（按收集套数倒序排列）
        var categoryInfos: List<SuitCategory>? = null // 套装分类列表（用于套装列表上面的分类标签展示）
) : ProguardRule