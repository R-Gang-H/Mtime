package com.kotlin.chat_component.inner.modules.chat.interfaces

import com.hyphenate.chat.EMMessage

interface IRecyclerViewHandle {
    /**
     * 是否可以使用默认的刷新
     * @param canUseRefresh
     */
    fun canUseDefaultRefresh(canUseRefresh: Boolean)

    /**
     * 刷新数据
     */
    fun refreshMessages()

    /**
     * 刷新并移动到最新的一条数据
     */
    fun refreshToLatest()

    /**
     * 刷新单条数据
     * @param message
     */
    fun refreshMessage(message: EMMessage?)

    /**
     * 删除单条数据
     * @param message
     */
    fun removeMessage(message: EMMessage?)

    /**
     * 移动到指定position的位置
     * @param position
     */
    fun moveToPosition(position: Int)
    /**
     * 判断是最后一条消息滚动到底部
     * @param message
     */
    fun lastMsgScrollToBottom(message: EMMessage?)
}