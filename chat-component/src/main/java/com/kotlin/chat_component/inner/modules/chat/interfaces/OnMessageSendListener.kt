package com.kotlin.chat_component.inner.modules.chat.interfaces

import com.hyphenate.chat.EMMessage

/**
 * 消息发送成功回调
 */
interface OnMessageSendListener {
    fun onSendSuccess(message: EMMessage)
}