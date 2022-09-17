package com.kotlin.android.app.data.entity.monopoly

import com.kotlin.android.app.data.ProguardRule

/**
 * 卡片大富翁api - 获取正在发行的套装列表（/currentIssueSuitList.api）
 *
 * Created on 2020/9/27.
 *
 * @author o.s
 */
data class CurrentIssueSuitList(
        var limitSuitList: List<Suit>? = null, // 限量套装列表
        var commonSuitList: List<Suit>? = null, // 普通套装列表
) : ProguardRule