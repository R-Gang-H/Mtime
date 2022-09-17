package com.kotlin.chat_component.inner.interfaces

interface OnCallBack<T> {
    fun onSuccess(models: T)
    fun onError(code: Int, error: String?)
}