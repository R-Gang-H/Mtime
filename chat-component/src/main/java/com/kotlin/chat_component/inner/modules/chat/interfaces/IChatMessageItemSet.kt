package com.kotlin.chat_component.inner.modules.chat.interfaces

import android.graphics.drawable.Drawable
import androidx.annotation.ColorInt
import com.kotlin.chat_component.inner.modules.chat.EaseChatMessageListLayout.ShowType

interface IChatMessageItemSet {
    /**
     * 设置默认头像
     * @param src
     */
    fun setAvatarDefaultSrc(src: Drawable?)

    /**
     * 设置头像样式
     * @param shapeType
     */
    fun setAvatarShapeType(shapeType: Int)

    /**
     * 是否展示昵称
     * @param showNickname
     */
    fun showNickname(showNickname: Boolean)

    /**
     * 设置条目发送者的背景
     * @param bgDrawable
     */
    fun setItemSenderBackground(bgDrawable: Drawable?)

    /**
     * 设置接收者的背景
     * @param bgDrawable
     */
    fun setItemReceiverBackground(bgDrawable: Drawable?)

    /**
     * 设置文本消息字体大小
     * @param textSize
     */
    fun setItemTextSize(textSize: Int)

    /**
     * 设置文本消息字体颜色
     * @param textColor
     */
    fun setItemTextColor(@ColorInt textColor: Int)
    /**
     * 设置文本消息条目的最小高度
     * @param height
     */
    //void setItemMinHeight(int height);
    /**
     * 设置时间线文本大小
     * @param textSize
     */
    fun setTimeTextSize(textSize: Int)

    /**
     * 设置时间线文本颜色
     * @param textColor
     */
    fun setTimeTextColor(textColor: Int)

    /**
     * 设置时间线背景
     * @param bgDrawable
     */
    fun setTimeBackground(bgDrawable: Drawable?)

    /**
     * 聊天列表条目的展示方式
     * @param type
     */
    fun setItemShowType(type: ShowType?)
}