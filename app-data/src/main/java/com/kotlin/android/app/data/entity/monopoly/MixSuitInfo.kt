package com.kotlin.android.app.data.entity.monopoly

/**
 * 合成套装统计信息
 *
 * Created on 2021/7/21.
 *
 * @author o.s
 */
data class MixSuitInfo(
    var commonMixCount: Long = 0, // 普卡套装已合成数量
    var commonRestCount: Long = 0, // 普卡套装未合成数量
    var limitMixCount: Long = 0, // 限量套装已合成数量
    var limitRestCount: Long = 0, // 限量套装未合成数量
    var commonCountDesc: String? = null, // 普卡套装合成数量描述
    var limitCountDesc: String? = null, // 限量套装合成数量描述
)
