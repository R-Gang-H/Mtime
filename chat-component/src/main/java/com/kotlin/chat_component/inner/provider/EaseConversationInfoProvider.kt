package com.kotlin.chat_component.inner.provider

import android.graphics.drawable.Drawable

interface EaseConversationInfoProvider {
    /**
     * 获取默认类型头像
     * @param type
     * @return
     */
    fun getDefaultTypeAvatar(type: String?): Drawable?
}