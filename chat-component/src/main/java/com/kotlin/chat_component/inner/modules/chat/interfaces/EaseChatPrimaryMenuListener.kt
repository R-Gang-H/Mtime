package com.kotlin.chat_component.inner.modules.chat.interfaces

import android.view.MotionEvent
import android.view.View

interface EaseChatPrimaryMenuListener {
    /**
     * when send button clicked
     * @param content
     */
    fun onSendBtnClicked(content: String?)

    /**
     * when typing on the edit-text layout.
     */
    fun onTyping(s: CharSequence?, start: Int, before: Int, count: Int)

    /**
     * when speak button is touched
     * @return
     */
    fun onPressToSpeakBtnTouch(v: View?, event: MotionEvent?): Boolean

    /**
     * toggle on/off voice button
     */
    fun onToggleVoiceBtnClicked()

    /**
     * toggle on/off text button
     */
    fun onToggleTextBtnClicked()

    /**
     * toggle on/off extend menu
     * @param extend
     */
    fun onToggleExtendClicked(extend: Boolean)

    /**
     * toggle on/off emoji icon
     * @param extend
     */
    fun onToggleEmojiconClicked(extend: Boolean)

    /**
     * on text input is clicked
     */
    fun onEditTextClicked()

    /**
     * if edit text has focus
     */
    fun onEditTextHasFocus(hasFocus: Boolean)
}