package com.kotlin.chat_component.inner.interfaces

import com.hyphenate.chat.EMMessage
import com.kotlin.chat_component.inner.adapter.EaseBaseMessageAdapter

interface IChatAdapterProvider {
    /**
     * provide chat message's adapter
     * if is null , will use default [com.kotlin.chat_component.inner.adapter.EaseMessageAdapter]
     * @return
     */
    fun provideMessageAdaper(): EaseBaseMessageAdapter<EMMessage?>?
}