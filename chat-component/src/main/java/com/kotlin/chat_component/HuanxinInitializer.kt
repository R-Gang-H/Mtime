package com.kotlin.chat_component

import android.content.Context
import com.hyphenate.chat.EMClient
import com.hyphenate.chat.EMOptions
import com.kotlin.chat_component.inner.EaseIM
import com.kotlin.chat_component.inner.domain.EaseUser
import com.kotlin.chat_component.inner.provider.EaseUserProfileProvider
import com.kotlin.chat_component.manager.ChatUserCacheManager

/**
 * Created by zhaoninglongfei on 2021/11/18.
 *
 */
object HuanxinInitializer {

    fun init(context: Context) {
        if (initSDK(context)) {
            EMClient.getInstance().setDebugMode(BuildConfig.DEBUG)
            EaseIM.getInstance().userProvider = object : EaseUserProfileProvider {
                override fun getUser(username: String?): EaseUser? {
                    val mtimeUserSimple = ChatUserCacheManager.getMtimeUserSimple(username)
                    return mtimeUserSimple?.let {
                        username?.let {
                            var user = EaseUser(it)
                            user.setNickname(mtimeUserSimple.nickName)
                            user.setAvatar(mtimeUserSimple.headPic)
                            user
                        }
                    }
                }
            }
        }
    }


    private fun initSDK(context: Context): Boolean {
        // 根据项目需求对SDK进行配置
        val options: EMOptions = initChatOptions(context)
        return EaseIM.getInstance().init(context, options)
    }

    /**
     * 设置环信SDK的一些参数
     */
    private fun initChatOptions(context: Context): EMOptions {
        val options = EMOptions()
        // 设置是否自动接受加好友邀请,默认是true
        options.acceptInvitationAlways = false
        // 设置是否需要接受方已读确认
        options.requireAck = false
        // 设置是否需要接受方送达确认,默认false
        options.requireDeliveryAck = false

        // 设置是否允许聊天室owner离开并删除会话记录，意味着owner再不会收到任何消息
        options.allowChatroomOwnerLeave(true)
        // 设置退出(主动和被动退出)群组时是否删除聊天消息
        options.isDeleteMessagesAsExitGroup = false
        // 设置是否自动接受加群邀请
        options.isAutoAcceptGroupInvitation = false
        // 是否自动将消息附件上传到环信服务器，默认为True是使用环信服务器上传下载
        options.autoTransferMessageAttachments = true

        options.usingHttpsOnly = true

        // 是否自动下载缩略图，默认是true为自动下载
        options.setAutoDownloadThumbnail(true)
        return options
    }
}