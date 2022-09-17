package com.kotlin.chat_component.inner.modules.chat.presenter

import com.hyphenate.chat.EMConversation
import com.hyphenate.chat.EMConversation.EMSearchDirection
import com.kotlin.chat_component.inner.modules.EaseBasePresenter
import com.kotlin.chat_component.inner.modules.ILoadDataView

abstract class EaseChatMessagePresenter : EaseBasePresenter() {
    @JvmField
    var mView: IChatMessageListView? = null

    @JvmField
    var conversation: EMConversation? = null

    override fun attachView(view: ILoadDataView?) {
        mView = view as IChatMessageListView
    }

    override fun detachView() {
        mView = null
    }

    override fun onDestroy() {
        super.onDestroy()
        detachView()
    }

    /**
     * 与会话绑定
     * @param conversation
     */
    fun setupWithConversation(conversation: EMConversation?) {
        this.conversation = conversation
    }

    abstract fun joinChatRoom(username: String?)

    /**
     * 加载本地数据
     * @param pageSize
     */
    abstract fun loadLocalMessages(pageSize: Int)

    /**
     * 加载更多本地数据
     * @param pageSize
     */
    abstract fun loadMoreLocalMessages(msgId: String?, pageSize: Int)

    /**
     * 从本地加载更多历史数据
     * @param msgId
     * @param pageSize
     * @param direction
     */
    abstract fun loadMoreLocalHistoryMessages(
        msgId: String?,
        pageSize: Int,
        direction: EMSearchDirection?
    )

    /**
     * 从服务器加载数据
     * @param pageSize
     */
    abstract fun loadServerMessages(pageSize: Int)

    /**
     * 从服务器加载更多数据
     * @param msgId 消息id
     * @param pageSize
     */
    abstract fun loadMoreServerMessages(msgId: String?, pageSize: Int)

    /**
     * 刷新当前的会话
     */
    abstract fun refreshCurrentConversation()

    /**
     * 刷新当前会话，并移动到最新
     */
    abstract fun refreshToLatest()
}