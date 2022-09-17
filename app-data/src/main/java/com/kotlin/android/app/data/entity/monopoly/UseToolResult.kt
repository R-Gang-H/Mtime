package com.kotlin.android.app.data.entity.monopoly

import com.kotlin.android.app.data.ProguardRule

/**
 * 卡片大富翁api - 使用道具卡（/useToolCard.api）
 *
 * Created on 2020/9/27.
 *
 * @author o.s
 */
data class UseToolResult(
        var bizCode: Long = 0,
        var bizMessage: String? = null, // 错误提示信息
        var toolCover: String? = null // 道具卡图片
) : ProguardRule

