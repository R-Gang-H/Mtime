package com.kotlin.android.app.data.entity.monopoly

import com.kotlin.android.app.data.ProguardRule

/**
 * 卡片大富翁api - 全部套装列表（/allSuitList.api）
 * 卡片大富翁api - 我/TA的套装（/suitList.api）
 *
 * Created on 2020/9/27.
 *
 * @author o.s
 */
data class SuitList(
        var hasMore: Boolean = false, // 是否有更多
        var totalCount: Long = 0, // 总记录数
        var suitList: List<Suit>? = null, // 套装列表
        var categoryInfos: List<SuitCategory>? = null, // 套装分类列表（用于套装列表上面的分类标签展示）
        var mixSuitCount: MixSuitInfo? = null, // 当前用户合成套装统计数（pageIndex=1时返回）
) : ProguardRule