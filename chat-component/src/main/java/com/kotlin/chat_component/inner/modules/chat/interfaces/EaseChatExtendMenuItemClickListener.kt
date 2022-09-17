package com.kotlin.chat_component.inner.modules.chat.interfaces

import android.view.View

/**
 * extend menu item click listener
 */
interface EaseChatExtendMenuItemClickListener {
    /**
     * item click
     * @param itemId
     * @param view
     */
    fun onChatExtendMenuItemClick(itemId: Int, view: View?)
}