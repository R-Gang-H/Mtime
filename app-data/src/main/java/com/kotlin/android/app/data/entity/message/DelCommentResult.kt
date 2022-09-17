package com.kotlin.android.app.data.entity.message

import com.kotlin.android.app.data.ProguardRule

/**
 * Created by zhaoninglongfei on 2022/4/24
 * 删除指定评论返回
 */
data class DelCommentResult(
    var errorCode: Long = 0L,//错误码
    var errorMsg: String? = "",//错误信息
) : ProguardRule
