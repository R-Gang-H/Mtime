package com.kotlin.android.app.data.entity.monopoly

import com.kotlin.android.app.data.ProguardRule

/**
 * 保险箱空位列表
 *
 * Created on 2020/9/27.
 *
 * @author o.s
 */
data class StrongBoxPositionList(
        var positionList: List<StrongBoxPosition>? = null // 保险箱空位列表
) : ProguardRule