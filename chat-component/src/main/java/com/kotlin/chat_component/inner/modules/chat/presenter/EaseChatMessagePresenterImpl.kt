package com.kotlin.chat_component.inner.modules.chat.presenter

import android.text.TextUtils
import com.hyphenate.EMValueCallBack
import com.hyphenate.chat.EMChatRoom
import com.hyphenate.chat.EMClient
import com.hyphenate.chat.EMConversation.EMSearchDirection
import com.hyphenate.chat.EMCursorResult
import com.hyphenate.chat.EMMessage
import com.kotlin.android.ktx.ext.log.e

class EaseChatMessagePresenterImpl : EaseChatMessagePresenter() {
    override fun joinChatRoom(username: String?) {
        EMClient.getInstance().chatroomManager()
            .joinChatRoom(username, object : EMValueCallBack<EMChatRoom?> {
                override fun onSuccess(value: EMChatRoom?) {
                    runOnUI {
                        if (isActive) {
                            mView!!.joinChatRoomSuccess(value)
                        }
                    }
                }

                override fun onError(error: Int, errorMsg: String) {
                    runOnUI {
                        if (isActive) {
                            mView!!.joinChatRoomFail(error, errorMsg)
                        }
                    }
                }
            })
    }

    override fun loadLocalMessages(pageSize: Int) {
        if (conversation == null) {
            throw NullPointerException("should first set up with conversation")
        }
        var messages: List<EMMessage>? = null
        try {
            messages = conversation!!.loadMoreMsgFromDB(null, pageSize)
        } catch (e: Exception) {
            e.printStackTrace()
        }
        if (messages == null || messages.isEmpty()) {
            if (isActive) {
                runOnUI { mView?.loadNoLocalMsg() }
            }
            return
        }
        if (isActive) {
            checkMessageStatus(messages)
            val finalMessages: List<EMMessage> = messages
            runOnUI { mView?.loadLocalMsgSuccess(finalMessages) }
        }
    }

    override fun loadMoreLocalMessages(msgId: String?, pageSize: Int) {
        if (conversation == null) {
            throw NullPointerException("should first set up with conversation")
        }
        require(isMessageId(msgId)) { "please check if set correct msg id" }
        var moreMsgs: List<EMMessage>? = null
        try {
            moreMsgs = conversation?.loadMoreMsgFromDB(msgId, pageSize)
        } catch (e: Exception) {
            e.printStackTrace()
        }
        if (moreMsgs == null || moreMsgs.isEmpty()) {
            if (isActive) {
                runOnUI { mView?.loadNoMoreLocalMsg() }
            }
            return
        }
        if (isActive) {
            checkMessageStatus(moreMsgs)
            val finalMoreMsgs: List<EMMessage> = moreMsgs
            runOnUI { mView!!.loadMoreLocalMsgSuccess(finalMoreMsgs) }
        }
    }

    override fun loadMoreLocalHistoryMessages(
        msgId: String?,
        pageSize: Int,
        direction: EMSearchDirection?
    ) {
        if (conversation == null) {
            throw NullPointerException("should first set up with conversation")
        }
        require(isMessageId(msgId)) { "please check if set correct msg id" }
        val message = conversation!!.getMessage(msgId, false)
        val messages = conversation!!.searchMsgFromDB(
            message.msgTime - 1,
            pageSize, direction
        )
        if (isActive) {
            runOnUI {
                if (messages == null || messages.isEmpty()) {
                    mView?.loadNoMoreLocalHistoryMsg()
                } else {
                    mView?.loadMoreLocalHistoryMsgSuccess(messages, direction)
                }
            }
        }
    }

    override fun loadServerMessages(pageSize: Int) {
        if (conversation == null) {
            throw NullPointerException("should first set up with conversation")
        }
        EMClient.getInstance().chatManager().asyncFetchHistoryMessage(
            conversation?.conversationId(),
            conversation?.type, pageSize, "",
            object : EMValueCallBack<EMCursorResult<EMMessage?>> {
                override fun onSuccess(value: EMCursorResult<EMMessage?>) {
                    //需要从数据将下载的数据放到缓存中
                    conversation?.loadMoreMsgFromDB("", pageSize)
                    runOnUI {
                        if (isActive) {
                            mView?.loadServerMsgSuccess(value.data)
                        }
                    }
                }

                override fun onError(error: Int, errorMsg: String) {
                    runOnUI {
                        if (isActive) {
                            mView?.loadMsgFail(error, errorMsg)
                            loadLocalMessages(pageSize)
                        }
                    }
                }
            })
    }

    override fun loadMoreServerMessages(msgId: String?, pageSize: Int) {
        if (conversation == null) {
            throw NullPointerException("should first set up with conversation")
        }
        require(isMessageId(msgId)) { "please check if set correct msg id" }
        EMClient.getInstance().chatManager().asyncFetchHistoryMessage(
            conversation?.conversationId(),
            conversation?.type, pageSize, msgId,
            object : EMValueCallBack<EMCursorResult<EMMessage?>> {
                override fun onSuccess(value: EMCursorResult<EMMessage?>) {
                    //需要从数据将下载的数据放到缓存中
                    conversation?.loadMoreMsgFromDB(msgId, pageSize)
                    runOnUI {
                        if (isActive) {
                            mView?.loadMoreServerMsgSuccess(value.data)
                        }
                    }
                }

                override fun onError(error: Int, errorMsg: String) {
                    runOnUI {
                        if (isActive) {
                            mView?.loadMsgFail(error, errorMsg)
                            loadMoreLocalMessages(msgId, pageSize)
                        }
                    }
                }
            })
    }

    override fun refreshCurrentConversation() {
        conversation?.let {
            it.markAllMessagesAsRead()
            val allMessages = conversation?.allMessages
            if (isActive) {
                runOnUI { mView?.refreshCurrentConSuccess(allMessages, false) }
            }
        }
    }

    override fun refreshToLatest() {
//        if (conversation == null) {
//            throw NullPointerException("should first set up with conversation")
//        }
        conversation?.markAllMessagesAsRead()
        val allMessages = conversation?.allMessages
        if (isActive) {
            runOnUI { mView?.refreshCurrentConSuccess(allMessages, true) }
        }
    }

    /**
     * 判断是否是消息id
     * @param msgId
     * @return
     */
    fun isMessageId(msgId: String?): Boolean {
        if (TextUtils.isEmpty(msgId)) {
            //可以允许消息id为空
            return true
        }
        val message = conversation?.getMessage(msgId, false)
        return message != null
    }

    /**
     * Check message's status, if is not success or fail, set to [EMMessage.Status.FAIL]
     * @param messages
     */
    private fun checkMessageStatus(messages: List<EMMessage>?) {
        if (messages == null || messages.isEmpty()) {
            return
        }
        for (message in messages) {
            if (message.status() != EMMessage.Status.SUCCESS && message.status() != EMMessage.Status.FAIL) {
                message.setStatus(EMMessage.Status.FAIL)
            }
        }
    }
}