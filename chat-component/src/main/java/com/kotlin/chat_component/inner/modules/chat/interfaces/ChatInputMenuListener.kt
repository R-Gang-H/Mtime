package com.kotlin.chat_component.inner.modules.chat.interfaces

import android.view.MotionEvent
import android.view.View

interface ChatInputMenuListener {
    /**
     * when typing on the edit-text layout.
     */
    fun onTyping(s: CharSequence?, start: Int, before: Int, count: Int)

    /**
     * when send message button pressed
     *
     * @param content
     * message content
     */
    fun onSendMessage(content: String?)

    /**
     * when big icon pressed
     * @param emojicon
     */
    fun onExpressionClicked(emojicon: Any?)

    /**
     * when speak button is touched
     * @param v
     * @param event
     * @return
     */
    fun onPressToSpeakBtnTouch(v: View?, event: MotionEvent?): Boolean

    /**
     * when click the item of extend menu
     * @param itemId
     * @param view
     */
    fun onChatExtendMenuItemClick(itemId: Int, view: View?)
}