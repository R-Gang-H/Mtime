package com.kotlin.android.app.data.entity.monopoly

/**
 * 拥有道具卡效果信息
 *
 * Created on 2021/7/22.
 *
 * @author o.s
 */
data class BufferInfo(
    var hasHackerBuffer: Boolean = false, // 是否有黑客卡效果
    var hasSlaveBuffer: Boolean = false, // 是否有奴隶卡效果
    var hasMammonBuffer: Boolean = false, // 是否有财神卡效果
    var hasGuardBuffer: Boolean = false, // 是否有防盗卡效果
    var hasBounceBuffer: Boolean = false, // 是否有反弹卡效果
    var hasScampBuffer: Boolean = false, // 是否有流氓卡效果
    var hasStealthBuffer: Boolean = false, // 是否有隐身卡效果
    var hasPocketBuffer: Boolean = false, // 是否有口袋卡效果
    var hasRobBuffer: Boolean = false, // 是否有打劫卡效果
    var protectBufferDescList: List<String>? = null, // 保护效果描述列表
    var effectBufferDescList: List<String>? = null, // 特效效果描述列表
)
