package com.kotlin.android.app.data.entity.comment

import com.kotlin.android.app.data.ProguardRule

/**
 * create by lushan on 2022/4/19
 * des:保存回复
 **/
data class SaveCommentResult(
    var bizCode: Long = -1L,
    var bizMsg: String? = "",
    var commentId: Long = 0L
) : ProguardRule