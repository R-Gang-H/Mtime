package com.kotlin.chat_component.inner.modules.chat.interfaces

interface IChatExtendMenu {
    /**
     * 清理扩展功能
     */
    fun clear()

    /**
     * 设置条目的排序
     * @param itemId
     * @param order
     */
    fun setMenuOrder(itemId: Int, order: Int)

    /**
     * 添加新的扩展功能
     * @param name
     * @param drawableRes
     * @param itemId
     */
    fun registerMenuItem(name: String?, drawableRes: Int, itemId: Int)

    /**
     * 添加新的扩展功能
     * @param name
     * @param drawableRes
     * @param itemId
     * @param order
     */
    fun registerMenuItem(name: String?, drawableRes: Int, itemId: Int, order: Int)

    /**
     * 添加新的扩展功能
     * @param nameRes
     * @param drawableRes
     * @param itemId
     */
    fun registerMenuItem(nameRes: Int, drawableRes: Int, itemId: Int)

    /**
     * 添加新的扩展功能
     * @param nameRes
     * @param drawableRes
     * @param itemId
     * @param order
     */
    fun registerMenuItem(nameRes: Int, drawableRes: Int, itemId: Int, order: Int)

    /**
     * 设置条目监听
     * @param listener
     */
    fun setEaseChatExtendMenuItemClickListener(listener: EaseChatExtendMenuItemClickListener?)
}