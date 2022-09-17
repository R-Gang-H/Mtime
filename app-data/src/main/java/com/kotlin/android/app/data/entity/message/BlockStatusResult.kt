package com.kotlin.android.app.data.entity.message

import com.kotlin.android.app.data.ProguardRule

/**
 * Created by zhaoninglongfei on 2022/3/21
 * 获取好友是否在黑名单
 */
data class BlockStatusResult(
    var bizCode: Long? = 0L,//1：成功 其他：失败
    var err: String? = null,
    var bizData: BlockStatus? = null
) : ProguardRule {
    data class BlockStatus(
        var isBlock: Long? = null,//黑名单状态 1：拉黑中 2：正常
        var isOfficial: Boolean? = null//是否官方账号 true：是 false : 不是
    ) : ProguardRule
}
