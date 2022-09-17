package com.kotlin.android.app.data.entity.monopoly

import com.kotlin.android.app.data.ProguardRule

/**
 * 卡片大富翁api - 是否有恶魔卡（/hasDemonCard.api）:
 *  result	Boolean 是否有恶魔卡
 *
 * Created on 2020/9/28.
 *
 * @author o.s
 */
data class BooleanResult(
        var result: Boolean = false
) : ProguardRule