package com.kotlin.android.app.data.entity.live

import com.kotlin.android.app.data.ProguardRule

/**
 * create by lushan on 2021/3/30
 * description:信令轮询
 */
data class SignalPolling(
        var cmdCode: Long = 0L,
        var messageId: String? = "",
        var roomNum: String? = "",
        var cmdParams:HashMap<String,Any?>? = hashMapOf()
) : ProguardRule