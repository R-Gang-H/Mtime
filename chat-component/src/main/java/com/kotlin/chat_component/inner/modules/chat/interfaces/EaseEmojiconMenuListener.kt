package com.kotlin.chat_component.inner.modules.chat.interfaces

interface EaseEmojiconMenuListener {
    /**
     * on emojicon clicked
     * @param emojicon
     */
    fun onExpressionClicked(emojicon: Any?)

    /**
     * on delete image clicked
     */
    fun onDeleteImageClicked() {}
}