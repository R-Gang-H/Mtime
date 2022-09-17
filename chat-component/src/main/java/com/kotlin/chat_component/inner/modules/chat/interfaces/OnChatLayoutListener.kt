package com.kotlin.chat_component.inner.modules.chat.interfaces

import android.view.View
import com.hyphenate.chat.EMMessage

/**
 * 用于监听[com.kotlin.chat_component.modules.chat.EaseChatLayout]中的变化
 */
interface OnChatLayoutListener {
    /**
     * 点击消息bubble区域
     * @param message
     * @return
     */
    fun onBubbleClick(message: EMMessage?): Boolean

    /**
     * 长按消息bubble区域
     * @param v
     * @param message
     * @return
     */
    fun onBubbleLongClick(v: View?, message: EMMessage?): Boolean

    /**
     * 点击头像
     * @param username
     */
    fun onUserAvatarClick(username: String?)

    /**
     * 长按头像
     * @param username
     */
    fun onUserAvatarLongClick(username: String?)

    /**
     * 条目点击
     * @param view
     * @param itemId
     */
    fun onChatExtendMenuItemClick(view: View?, itemId: Int)

    /**
     * EditText文本变化监听
     * @param s
     * @param start
     * @param before
     * @param count
     */
    fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int)

    /**
     * 发送消息成功后的回调
     * @param message
     */
    fun onChatSuccess(message: EMMessage?) {}

    /**
     * 聊天中错误信息
     * @param code
     * @param errorMsg
     */
    fun onChatError(code: Int, errorMsg: String?)
}