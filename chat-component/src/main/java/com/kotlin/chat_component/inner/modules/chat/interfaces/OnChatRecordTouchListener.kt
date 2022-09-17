package com.kotlin.chat_component.inner.modules.chat.interfaces

import android.view.MotionEvent
import android.view.View

interface OnChatRecordTouchListener {
    /**
     * 语音按压事件
     * @param v
     * @param event
     * @return
     */
    fun onRecordTouch(v: View?, event: MotionEvent?): Boolean
}