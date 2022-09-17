package com.kotlin.android.app.data.entity.comment

import com.kotlin.android.app.data.ProguardRule

/**
 * create by lushan on 2020/10/12
 * description:保存回复
 */
data class SaveReplyResult(
    var bizCode: Long = -1L,
    var bizMsg: String? = "",
    var replyId:Long = 0L//回复id
): ProguardRule