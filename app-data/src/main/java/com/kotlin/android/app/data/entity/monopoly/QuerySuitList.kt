package com.kotlin.android.app.data.entity.monopoly

import com.kotlin.android.app.data.ProguardRule

/**
 * 卡片大富翁api - 按名称模糊搜索套装（/querySuitByName.api）
 *
 * Created on 2020/9/27.
 *
 * @author o.s
 */
data class QuerySuitList(
        var suitList: List<Suit>? = null, // 套装列表
) : ProguardRule