package com.kotlin.chat_component.inner.modules.chat.interfaces

import com.hyphenate.chat.EMMessage

interface OnRecallMessageResultListener {
    /**
     * 撤回成功
     * @param message
     */
    fun recallSuccess(message: EMMessage?)

    /**
     * 撤回失败
     * @param code
     * @param errorMsg
     */
    fun recallFail(code: Int, errorMsg: String?)
}