package com.kotlin.chat_component.model

import com.kotlin.android.app.data.ProguardRule

/**
 * Created by zhaoninglongfei on 2022/3/28
 * 时光聊天列表
 */
data class MtimeChatItem(
    var conversationId: String? = null,
    var recentMessage: String? = null,
    var lastChatTime: Long? = null,
    val messageNotify: Int? = null,
    val other: Other
) : ProguardRule {
    data class Other(
        var imId: String? = null,
        var headPic: String? = "",
        var name: String? = null,
        val authType: Long? = null,
        val authRole: String? = null
    ) : ProguardRule
}
