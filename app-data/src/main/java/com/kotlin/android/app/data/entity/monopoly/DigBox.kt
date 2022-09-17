package com.kotlin.android.app.data.entity.monopoly

import com.kotlin.android.app.data.ProguardRule

/**
 * 卡片大富翁api - 挖宝箱（/digBox.api）
 *
 * Created on 2020/9/28.
 *
 * @author o.s
 */
data class DigBox(
        var bizCode: Long = 0, // 业务返回码：1成功，0失败，-1宝箱位置已满
        var bizMessage: String? = null,
        var newCardBox: Box? = null // 新宝箱信息
) : ProguardRule