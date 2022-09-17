package com.kotlin.android.app.data.entity.message

import com.kotlin.android.app.data.ProguardRule

/**
 * Created by zhaoninglongfei on 2022/3/18
 * 清空消息返回
 */
data class ClearResult(
    var errorCode: Long = 0L,//错误码
    var errorMsg: String? = "",//错误信息
) : ProguardRule
