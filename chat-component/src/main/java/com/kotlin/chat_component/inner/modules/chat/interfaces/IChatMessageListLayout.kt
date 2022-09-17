package com.kotlin.chat_component.inner.modules.chat.interfaces

import com.kotlin.chat_component.inner.adapter.EaseMessageAdapter
import com.kotlin.chat_component.inner.interfaces.MessageListItemClickListener
import com.kotlin.chat_component.inner.modules.chat.EaseChatMessageListLayout.OnChatErrorListener
import com.kotlin.chat_component.inner.modules.chat.EaseChatMessageListLayout.OnMessageTouchListener
import com.kotlin.chat_component.inner.modules.chat.presenter.EaseChatMessagePresenter
import com.kotlin.chat_component.inner.modules.interfaces.IRecyclerView

interface IChatMessageListLayout : IRecyclerView {
    /**
     * 设置presenter
     * @param presenter
     */
    fun setPresenter(presenter: EaseChatMessagePresenter?)

    /**
     * 获取adapter
     * @return
     */
    val messageAdapter: EaseMessageAdapter?

    /**
     * 设置聊天区域的touch监听，判断是否点击在条目消息外，是否正在拖拽列表
     * @param listener
     */
    fun setOnMessageTouchListener(listener: OnMessageTouchListener?)

    /**
     * 设置聊天过程中的错误监听
     * @param listener
     */
    fun setOnChatErrorListener(listener: OnChatErrorListener?)

    /**
     * 设置聊天列表条目中各个控件的点击事件
     * @param listener
     */
    fun setMessageListItemClickListener(listener: MessageListItemClickListener?)
}