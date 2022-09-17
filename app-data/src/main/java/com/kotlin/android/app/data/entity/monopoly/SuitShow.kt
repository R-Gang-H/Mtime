package com.kotlin.android.app.data.entity.monopoly

import com.kotlin.android.app.data.ProguardRule

/**
 * 卡片大富翁api - 设置主页展示套装（/setSuitShow.api）
 *
 * Created on 2020/9/27.
 *
 * @author o.s
 */
data class SuitShow(
        var bizCode: Long = 0, // 业务返回码：1成功，0失败，-1当前用户没有此套装，-2超出最大设置数量，-3要修改的不是当前用户推荐的套装
        var bizMessage: String? = null, // 错误提示信息
        var suitShowList: List<Suit>? = null, // 套装展示列表
) : ProguardRule