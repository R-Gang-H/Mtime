package com.kotlin.chat_component.inner.modules.chat.interfaces

import com.kotlin.chat_component.inner.domain.EaseEmojiconGroupEntity

interface IChatEmojiconMenu {
    /**
     * 添加表情
     * @param groupEntity
     */
    fun addEmojiconGroup(groupEntity: EaseEmojiconGroupEntity?)

    /**
     * 添加表情列表
     * @param groupEntitieList
     */
    fun addEmojiconGroup(groupEntitieList: List<EaseEmojiconGroupEntity?>?)

    /**
     * 移除表情
     * @param position
     */
    fun removeEmojiconGroup(position: Int)

    /**
     * 设置TabBar是否可见
     * @param isVisible
     */
    fun setTabBarVisibility(isVisible: Boolean)

    /**
     * 设置表情监听
     * @param listener
     */
    fun setEmojiconMenuListener(listener: EaseEmojiconMenuListener?)
}