package com.kotlin.chat_component.inner.modules.chat.presenter

import com.hyphenate.chat.EMChatRoom
import com.hyphenate.chat.EMConversation
import com.hyphenate.chat.EMConversation.EMSearchDirection
import com.hyphenate.chat.EMMessage
import com.kotlin.chat_component.inner.modules.ILoadDataView

interface IChatMessageListView : ILoadDataView {
    /**
     * 获取当前会话
     */
    val currentConversation: EMConversation?

    /**
     * 加入聊天室成功
     */
    fun joinChatRoomSuccess(value: EMChatRoom?)

    /**
     * 加入聊天室失败
     */
    fun joinChatRoomFail(error: Int, errorMsg: String?)

    /**
     * 加载消息失败
     */
    fun loadMsgFail(error: Int, message: String?)

    /**
     * 加载本地数据成功
     */
    fun loadLocalMsgSuccess(data: List<EMMessage?>?)

    /**
     * 没有加载到本地数据
     */
    fun loadNoLocalMsg()

    /**
     * 加载本地更多数据成功
     */
    fun loadMoreLocalMsgSuccess(data: List<EMMessage?>?)

    /**
     * 没有加载到更多数据
     */
    fun loadNoMoreLocalMsg()

    /**
     * 加载更多本地的历史数据
     */
    fun loadMoreLocalHistoryMsgSuccess(data: List<EMMessage?>?, direction: EMSearchDirection?)

    /**
     * 没有更多的本地历史数据
     */
    fun loadNoMoreLocalHistoryMsg()

    /**
     * 加载漫游数据
     */
    fun loadServerMsgSuccess(data: List<EMMessage?>?)

    /**
     * 加载更多漫游数据
     */
    fun loadMoreServerMsgSuccess(data: List<EMMessage?>?)

    /**
     * 刷新当前会话
     */
    fun refreshCurrentConSuccess(data: List<EMMessage?>?, toLatest: Boolean)
}