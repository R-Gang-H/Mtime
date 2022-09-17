package com.kotlin.chat_component.inner.interfaces

import com.hyphenate.EMCallBack
import com.hyphenate.chat.EMMessage

abstract class EaseMessageCallback : EMCallBack {
    override fun onSuccess() {}
    abstract fun onSuccess(message: EMMessage?, position: Int)
    override fun onError(code: Int, error: String) {}
    override fun onProgress(progress: Int, status: String) {}
}