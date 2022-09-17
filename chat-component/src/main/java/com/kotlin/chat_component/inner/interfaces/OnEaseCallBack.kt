package com.kotlin.chat_component.inner.interfaces

abstract class OnEaseCallBack<T> : OnCallBack<T> {
    override fun onError(code: Int, error: String?) {}
}