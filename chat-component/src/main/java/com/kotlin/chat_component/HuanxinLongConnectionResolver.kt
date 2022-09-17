package com.kotlin.chat_component

import com.google.gson.Gson
import com.hyphenate.chat.EMCmdMessageBody
import com.hyphenate.chat.EMMessage
import com.kotlin.android.app.data.entity.message.CmdHuanxin
import com.kotlin.android.ktx.ext.log.e
import com.kotlin.chat_component.manager.ChatUserCacheManager
import com.kotlin.chat_component.model.MtimeUserSimple

/**
 * Created by zhaoninglongfei on 2022/4/13
 * 环信长连接解析
 */
object HuanxinLongConnectionResolver {

    fun registerHuanxinMessageListener() {
        HuanxinMessageManager.addHuanxinMessageListener(object :
            HuanxinMessageManager.HuanxinMessageListener {
            override fun onMessageReceived(messages: List<EMMessage>?) {
                executeReceivedMessage(messages)
            }

            override fun onCmdMessageReceived(messages: List<EMMessage>?) {
                executeCmdMessage(messages)
            }

            override fun onMessageDelivered(messages: List<EMMessage>?) {}
        })
    }

    private fun executeCmdMessage(messages: List<EMMessage>?) {
        messages?.forEach { message ->
            try {
                val jsonString = (message.body as EMCmdMessageBody).action()
                val cmdHuanxin = Gson().fromJson(jsonString, CmdHuanxin::class.java)

                //未读消息数
                MtimeUnReadMessageObserve.notice(
                    cmdHuanxin.totalCount(),
                    HuanxinConversationManager.getUnreadMessageCount()
                )

                //长连接cmd消息
                MtimeCmdObserve.notice(cmdHuanxin)
            } catch (e: Exception) {
                //JsonSyntaxException 解析出非该场景需要的信息 忽略
                e.e()
            }
        }
    }

    private fun executeReceivedMessage(messages: List<EMMessage>?) {
        messages?.forEach { message ->
            collectUserInfo(message)
            MtimeChatObserve.noticeMessageReceived()
            MtimeUnReadMessageObserve.notice(
                cmdMessageCount = null,
                huanxinMessageCount = HuanxinConversationManager.getUnreadMessageCount()
            )
        }
    }

    /**
     * 缓存用户头像消息
     */
    private fun collectUserInfo(message: EMMessage) {
        try {
            val imId = message.getStringAttribute(ChatUserCacheManager.KEY_IM_ID)
            val nickname = message.getStringAttribute(ChatUserCacheManager.KEY_NICKNAME)
            val avatar = message.getStringAttribute(ChatUserCacheManager.KEY_AVATAR)
            val authType = message.getLongAttribute(ChatUserCacheManager.KEY_MTIME_AUTH_TYPE)
            val authRole = message.getStringAttribute(ChatUserCacheManager.KEY_MTIME_AUTH_ROLE)
            val isOfficial = message.getStringAttribute(ChatUserCacheManager.KEY_MTIME_ROLE, "")
            val mtimeId = message.getLongAttribute(ChatUserCacheManager.KEY_MTIME_ID)
            ChatUserCacheManager.put(
                imId,
                MtimeUserSimple(
                    imId,
                    mtimeId,
                    nickname,
                    avatar,
                    authType,
                    authRole,
                    !isOfficial.isNullOrEmpty()
                )
            )
        } catch (e: Exception) {
            e.e()
        }
    }
}