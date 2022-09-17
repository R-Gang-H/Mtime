package com.kotlin.chat_component

import com.hyphenate.EMCallBack
import com.hyphenate.EMConversationListener
import com.hyphenate.EMValueCallBack
import com.hyphenate.chat.EMClient
import com.hyphenate.chat.EMConversation
import com.hyphenate.chat.EMMessage
import com.kotlin.android.core.CoreApp
import com.kotlin.android.ktx.ext.log.e
import com.kotlin.chat_component.inner.constants.EaseConstant
import com.kotlin.chat_component.inner.modules.conversation.model.EaseConversationInfo
import com.kotlin.chat_component.inner.utils.EaseCommonUtils
import com.kotlin.chat_component.manager.ChatUserCacheManager
import com.kotlin.chat_component.model.MtimeChatItem
import com.kotlin.chat_component.model.MtimeUserSimple
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import org.json.JSONObject
import java.util.*
import kotlin.coroutines.resume
import kotlin.coroutines.suspendCoroutine

/**
 * Created by zhaoninglongfei on 2022/3/28
 * 环信会话
 */
object HuanxinConversationManager {

    private var emConversationListener = object : EMConversationListener {
        override fun onCoversationUpdate() {
            MtimeConversationObserve.noticeOnConversationUpdate()
        }

        override fun onConversationRead(from: String?, to: String?) {
            MtimeConversationObserve.noticeOnConversationRead()
        }
    }

    //将本地聊天监听注册到环信的会话监听
    fun registerMtimeConversationListenerToHuanxin() {
        "将本地聊天监听注册到环信的会话监听".e()
        EMClient.getInstance().chatManager().addConversationListener(emConversationListener)
    }

    //注销本地聊天监听
    fun unRegisterMtimeConversationListenerFromHuanxin() {
        "注销本地聊天监听".e()
        EMClient.getInstance().chatManager().removeConversationListener(emConversationListener)
    }


    /**
     * 所有未读消息数清零
     */
    fun clearUnreadMessages() {
//        val conversation = EMClient.getInstance().chatManager().getConversation(username)
//        //指定会话消息未读数清零
//        conversation.markAllMessagesAsRead()
//        //把一条消息置为已读
//        conversation.markMessageAsRead(messageId)
        //所有未读消息数清零
        EMClient.getInstance().chatManager().markAllConversationsAsRead()
    }

    interface DeleteConversationListener {
        fun onSuccess()
        fun onError()
    }

    /**
     * 删除会话
     */
    fun deleteConversation(
        conversationId: String?,
        type: EMConversation.EMConversationType,
        listener: DeleteConversationListener
    ) {
        EMClient.getInstance().chatManager().deleteConversation(conversationId, true)
        listener.onSuccess()
        conversationId?.let {
            EMClient.getInstance().chatManager()
                .deleteConversationFromServer(it, type, true, object : EMCallBack {
                    override fun onSuccess() {
//                        EMClient.getInstance().chatManager().deleteConversation(it, true)
//                        listener.onSuccess()
                    }

                    override fun onError(p0: Int, p1: String?) {
                        p1.e()
//                        listener.onError()
                    }
                })
        }
    }

    /**
     * 获取所有未读消息数量
     */
    fun getUnreadMessageCount(): Int {
        return EMClient.getInstance().chatManager().unreadMessageCount
    }

    fun loadHuanxinConversations() {
        CoroutineScope(Dispatchers.Main).launch {
            val result: List<MtimeChatItem> = withContext(Dispatchers.IO) {
                loadMtimeChatItems()
            }
            MtimeUnReadMessageObserve.notice(null, getUnreadMessageCount())
        }
    }

    suspend fun loadMtimeChatItems(): List<MtimeChatItem> {
        val conversations: List<EaseConversationInfo> = getConversations()
        val mtimeChatItems: ArrayList<MtimeChatItem> = arrayListOf<MtimeChatItem>()

        conversations.forEach { conversation ->
            val info = conversation.info
            if (info is EMConversation) {

                val imId: String = info.conversationId() ?: ""
                collectUserInfo(info)
                val lastMessage: EMMessage? = info.lastMessage
                var mtimeChatItem: MtimeChatItem? = null

                lastMessage?.let {
                    mtimeChatItem = MtimeChatItem(
                        conversationId = imId,
                        recentMessage = EaseCommonUtils.getMessageDigest(
                            lastMessage,
                            CoreApp.instance
                        ),
                        lastChatTime = lastMessage.msgTime,
                        messageNotify = info.unreadMsgCount,
                        other = MtimeChatItem.Other(
                            imId = info.conversationId(),
                            headPic = ChatUserCacheManager.getMtimeUserSimple(imId)?.headPic,
                            name = ChatUserCacheManager.getMtimeUserSimple(imId)?.nickName,
                            authType = ChatUserCacheManager.getMtimeUserSimple(imId)?.authType,
                            authRole = ChatUserCacheManager.getMtimeUserSimple(imId)?.authRole
                        )
                    )
                }

                mtimeChatItem?.let { mtimeChatItems.add(it) }
            }
        }

        return mtimeChatItems
    }

    private fun collectUserInfo(conversation: EMConversation?) {
        val latestMessageFromOthers: EMMessage? = conversation?.latestMessageFromOthers
        val lastMessage: EMMessage? = conversation?.lastMessage

        if (latestMessageFromOthers == null) {
            lastMessage?.let {
                collectOtherUserInfo(it)
            }
        }

        try {
            latestMessageFromOthers?.let {
                val mtimeId =
                    latestMessageFromOthers.getLongAttribute(ChatUserCacheManager.KEY_MTIME_ID, 0L)
                val nickName =
                    latestMessageFromOthers.getStringAttribute(
                        ChatUserCacheManager.KEY_NICKNAME,
                        ""
                    )
                val headPic =
                    latestMessageFromOthers.getStringAttribute(ChatUserCacheManager.KEY_AVATAR, "")
                val authRole =
                    latestMessageFromOthers.getStringAttribute(
                        ChatUserCacheManager.KEY_MTIME_AUTH_ROLE, ""
                    )
                val isOfficial: String? =
                    latestMessageFromOthers.getStringAttribute(
                        ChatUserCacheManager.KEY_MTIME_ROLE,
                        ""
                    )
                val authType =
                    latestMessageFromOthers.getLongAttribute(
                        ChatUserCacheManager.KEY_MTIME_AUTH_TYPE, 0L
                    )

                latestMessageFromOthers.conversationId()?.let { imId ->
                    ChatUserCacheManager.put(
                        imId,
                        MtimeUserSimple(
                            imId,
                            mtimeId,
                            nickName,
                            headPic,
                            authType,
                            authRole,
                            !isOfficial.isNullOrEmpty()
                        )
                    )
                }
            }
        } catch (e: Exception) {
            e.e()
        }
    }

    private fun collectOtherUserInfo(lastMessage: EMMessage) {
        try {
            //获取对方的信息
            val other: JSONObject? =
                lastMessage.getJSONObjectAttribute(ChatUserCacheManager.KEY_MTIME_OTHER)

            other?.let {
                val user = MtimeUserSimple.fromJSONObject(it)

                user.imId?.let { imId ->
                    ChatUserCacheManager.put(imId, user)
                }
            }
        } catch (e: Exception) {
            e.e()
        }
    }

    /**
     * 封装环信提供的方式 获取 会话列表
     */
    private suspend fun getConversations(): List<EaseConversationInfo> {
        var list: List<EaseConversationInfo>
        if (EMClient.getInstance()?.isSdkInited == false) {
            return emptyList()
        }

        //本地没有聊天记录从服务器上获取
        if (EMClient.getInstance().chatManager().allConversations.isEmpty()) {
            list = callConversationFromServer()
        } else {
            //加载本地聊天记录
            list = suspendCoroutine { continuation ->
                continuation.resume(loadLocalConversation())
            }
        }
        return list
    }

    /**
     * 从服务器获取会话列表
     *  //todo  好像只有正式环境的key才开通了历史消息功能？
     */
    private suspend fun callConversationFromServer(): ArrayList<EaseConversationInfo> {
        "从服务器获取会话列表".e()
        var list: ArrayList<EaseConversationInfo> = suspendCoroutine { continuation ->
            EMClient.getInstance().chatManager().asyncFetchConversationsFromServer(
                object : EMValueCallBack<Map<String?, EMConversation>> {
                    override fun onSuccess(value: Map<String?, EMConversation>) {
                        "从服务器获取会话列表成功".e()
                        val conversations: List<EMConversation> = ArrayList(value.values)
                        val infoList: ArrayList<EaseConversationInfo> = ArrayList()
                        if (conversations.isNotEmpty()) {
                            var info: EaseConversationInfo? = null
                            for (conversation in conversations) {
                                info = EaseConversationInfo()
                                info.info = conversation
                                info.timestamp = conversation.lastMessage?.msgTime ?: 0L
                                infoList.add(info)
                            }
                        }
                        continuation.resume(infoList)
                    }

                    override fun onError(error: Int, errorMsg: String) {
                        continuation.resume(arrayListOf())
                    }
                })
        }
        return list
    }

    /**
     * 从本地加载会话列表
     */
    private fun loadLocalConversation(): ArrayList<EaseConversationInfo> {
        // get all conversations
        val conversations = EMClient.getInstance().chatManager().allConversations
        if (conversations.isEmpty()) {
            return arrayListOf()
        }
        val infos: ArrayList<EaseConversationInfo> = arrayListOf()
        synchronized(this) {
            var info: EaseConversationInfo? = null

            for (conversation in conversations.values) {
                if (conversation.allMessages.size != 0) {
                    //移除系统相关消息
                    if (conversation.conversationId() == EaseConstant.DEFAULT_SYSTEM_MESSAGE_ID)
                        continue

                    info = EaseConversationInfo()
                    info.info = conversation

                    //获取会话扩展字段
                    val extField = conversation.extField

                    //获取消息的时间戳(服务器时间)
                    val lastMsgTime = conversation.lastMessage.msgTime
                    if (extField.isNotEmpty() && EaseCommonUtils.isTimestamp(extField)) {
                        info.isTop = true
                        val makeTopTime = extField.toLong()
                        if (makeTopTime > lastMsgTime) {
                            info.timestamp = makeTopTime
                        } else {
                            info.timestamp = lastMsgTime
                        }
                    } else {
                        info.timestamp = lastMsgTime
                    }
                    infos.add(info)
                }
            }
            sortByTimestamp(infos)
            return infos
        }
    }

    private fun sortByTimestamp(list: List<EaseConversationInfo>?) {
        if (list == null || list.isEmpty()) {
            return
        }
        Collections.sort(list) { o1, o2 ->
            when {
                o2.timestamp > o1.timestamp -> {
                    1
                }
                o2.timestamp == o1.timestamp -> {
                    0
                }
                else -> {
                    -1
                }
            }
        }
    }
}