package com.kotlin.chat_component.inner.modules.chat.interfaces

import android.graphics.drawable.Drawable
import android.widget.EditText
import com.kotlin.chat_component.inner.modules.chat.EaseInputMenuStyle

/**
 * 输入菜单
 */
interface IChatPrimaryMenu {
    /**
     * 获取EditText
     */
    val editTextMessage: EditText?

    /**
     * 菜单展示类型
     */
    fun setMenuShowType(style: EaseInputMenuStyle?)

    /**
     * 常规模式
     */
    fun showNormalStatus()

    /**
     * 文本输入模式
     */
    fun showTextStatus()

    /**
     * 语音输入模式
     */
    fun showVoiceStatus()

    /**
     * 表情输入模式
     */
    fun showEmojiconStatus()

    /**
     * 更多模式
     */
    fun showMoreStatus()

    /**
     * 隐藏扩展区模式
     */
    fun hideExtendStatus()

    /**
     * 隐藏软键盘
     */
    fun hideSoftKeyboard()

    /**
     * 输入表情
     */
    fun onEmojiconInputEvent(emojiContent: CharSequence?)

    /**
     * 删除表情
     */
    fun onEmojiconDeleteEvent()

    /**
     * 输入文本
     */
    fun onTextInsert(text: CharSequence?)

    /**
     * 设置输入框背景
     */
    fun setMenuBackground(bg: Drawable?)

    /**
     * 设置发送按钮背景
     */
    fun setSendButtonBackground(bg: Drawable?)

    /**
     * 设置监听
     */
    fun setEaseChatPrimaryMenuListener(listener: EaseChatPrimaryMenuListener?)
}