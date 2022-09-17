package com.kotlin.android.message.ui.center.viewBean

import com.kotlin.android.app.data.ProguardRule
import com.kotlin.android.message.widget.AuthHeaderView
import com.kotlin.android.mtime.ktx.formatPublishTime
import com.kotlin.chat_component.model.MtimeChatItem

/**
 * Created by zhaoninglongfei on 2022/4/27
 *
 */
data class ChatViewBean(
    val imId: String? = null,
    val conversationId: String? = null,
    val name: String?,
    val recentMessage: String?,
    val lastChatTime: String?,
    val messageNotify: Int?,
    val authHeader: AuthHeaderView.AuthHeader?
) : ProguardRule {
    companion object {
        fun convertChatViewBeanList(chatItemList: List<MtimeChatItem>): List<ChatViewBean> {
            val list = arrayListOf<ChatViewBean>()
            chatItemList.forEach {
                list.add(
                    ChatViewBean(
                        imId = it.other.imId,
                        conversationId = it.conversationId,
                        name = it.other.name,
                        recentMessage = it.recentMessage,
                        lastChatTime = formatPublishTime(it.lastChatTime ?: 0L),
                        messageNotify = if (it.messageNotify == 0) {//为0时不展示红点
                            null
                        } else {
                            it.messageNotify
                        },
                        authHeader = AuthHeaderView.AuthHeader(
                            userId = 0L,
                            headImg = it.other.headPic,
                            unread = false,
                            authType = it.other.authType,
                            authRole = it.other.authRole,
                        )
                    )
                )
            }

            return list
        }
    }
}