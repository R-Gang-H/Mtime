package com.kotlin.android.app.data.entity.monopoly

import com.kotlin.android.app.data.ProguardRule

/**
 * 卡片大富翁api - 我的套装搜索结果列表（/searchSuitList.api）
 *
 * Created on 2020/9/27.
 *
 * @author o.s
 */
data class SearchSuitList(
        var suitList: List<Suit>? = null, // 套装展示列表
) : ProguardRule