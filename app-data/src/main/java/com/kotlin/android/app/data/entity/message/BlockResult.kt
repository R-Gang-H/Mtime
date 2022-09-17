package com.kotlin.android.app.data.entity.message

import com.kotlin.android.app.data.ProguardRule

/**
 * Created by zhaoninglongfei on 2022/3/21
 * 拉黑/取消拉黑
 */
data class BlockResult(
    var bizCode: Long? = 0L,//1：成功 其他：失败
    var err: String? = null,
    var bizData: String? = null
) : ProguardRule
