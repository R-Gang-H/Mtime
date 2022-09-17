package com.kotlin.android.app.data.entity.monopoly

import com.kotlin.android.app.data.ProguardRule

/**
 * @desc 用于返回结果的javabean
 * @author zhangjian
 * @date 2020/9/28 09:28
 */
data class CommonBizMsgBean(
        var bizCode: Long? = 0, // 返回的状态值
        var bizMessage: String? = "" // 文案
): ProguardRule