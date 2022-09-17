package com.kotlin.chat_component.inner.modules.chat.interfaces

/**
 * 自定义菜单
 */
interface IChatInputMenu {

    /**
     * 设置自定义菜单
     */
    fun setCustomPrimaryMenu(menu: IChatPrimaryMenu?)

    /**
     * 设置自定义表情
     */
    fun setCustomEmojiconMenu(menu: IChatEmojiconMenu?)

    /**
     * 设置自定义扩展菜单
     */
    fun setCustomExtendMenu(menu: IChatExtendMenu?)

    /**
     * 隐藏扩展区域（包含表情和扩展菜单）
     */
    fun hideExtendContainer()

    /**
     * 是否展示表情菜单
     */
    fun showEmojiconMenu(show: Boolean)

    /**
     * 是否展示扩展菜单
     */
    fun showExtendMenu(show: Boolean)

    /**
     * 隐藏软键盘
     */
    fun hideSoftKeyboard()

    /**
     * 设置菜单监听事件
     */
    fun setChatInputMenuListener(listener: ChatInputMenuListener?)

    /**
     * 获取菜单
     */
    val primaryMenu: IChatPrimaryMenu?

    /**
     * 获取表情菜单
     */
    val emojiconMenu: IChatEmojiconMenu?

    /**
     * 获取扩展菜单
     */
    val chatExtendMenu: IChatExtendMenu?

    /**
     * 点击返回
     */
    fun onBackPressed(): Boolean
}