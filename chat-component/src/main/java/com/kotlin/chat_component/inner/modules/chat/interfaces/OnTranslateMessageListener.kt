package com.kotlin.chat_component.inner.modules.chat.interfaces

import com.hyphenate.chat.EMMessage

interface OnTranslateMessageListener {
    /**
     * 翻译成功
     * @param message
     */
    fun translateMessageSuccess(message: EMMessage?)

    /**
     * 翻译失败
     * @param message
     * @param code
     * @param error
     */
    fun translateMessageFail(message: EMMessage?, code: Int, error: String?)
}