package com.kotlin.chat_component.inner.modules.chat.interfaces

import android.view.View
import android.widget.PopupWindow
import com.hyphenate.chat.EMMessage
import com.kotlin.chat_component.inner.modules.menu.EasePopupWindowHelper
import com.kotlin.chat_component.inner.modules.menu.MenuItemBean

/**
 * [EasePopupWindowHelper]中的条目点击事件
 */
interface OnMenuChangeListener {
    /**
     * 展示Menu之前
     * @param helper
     * @param message
     */
    fun onPreMenu(helper: EasePopupWindowHelper?, message: EMMessage?)

    /**
     * 点击条目
     * @param item
     * @param message
     */
    fun onMenuItemClick(item: MenuItemBean?, message: EMMessage?): Boolean

    /**
     * 消失
     * @param menu
     */
    fun onDismiss(menu: PopupWindow?) {}
}