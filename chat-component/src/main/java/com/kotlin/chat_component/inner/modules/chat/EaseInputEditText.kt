package com.kotlin.chat_component.inner.modules.chat

import android.content.Context
import android.graphics.Rect
import android.util.AttributeSet
import android.view.KeyEvent
import android.view.View
import android.view.inputmethod.EditorInfo
import android.widget.TextView
import androidx.appcompat.widget.AppCompatEditText

/**
 * 输入栏中的消息
 */
class EaseInputEditText @JvmOverloads constructor(
    context: Context?,
    attrs: AttributeSet? = null,
    defStyleAttr: Int = 0
) : AppCompatEditText(
    context!!, attrs, defStyleAttr
), View.OnKeyListener, TextView.OnEditorActionListener {

    private var ctrlPress = false
    private var listener: OnEditTextChangeListener? = null

    override fun onFocusChanged(focused: Boolean, direction: Int, previouslyFocusedRect: Rect?) {
        super.onFocusChanged(focused, direction, previouslyFocusedRect)
        if (listener != null) {
            listener!!.onEditTextHasFocus(focused)
        }
    }

    override fun onKey(v: View, keyCode: Int, event: KeyEvent): Boolean {
        if (keyCode == KeyEvent.KEYCODE_UNKNOWN) {
            if (event.action == KeyEvent.ACTION_DOWN) {
                ctrlPress = true
            } else if (event.action == KeyEvent.ACTION_UP) {
                ctrlPress = false
            }
        }
        return false
    }

    override fun onEditorAction(v: TextView?, actionId: Int, event: KeyEvent?): Boolean {
        return if (actionId == EditorInfo.IME_ACTION_SEND ||
            event != null && event.keyCode == KeyEvent.KEYCODE_ENTER && event.action == KeyEvent.ACTION_DOWN && ctrlPress
        ) {
            val s = text.toString()
            if (listener != null) {
                setText("")
                listener!!.onClickKeyboardSendBtn(s)
            }
            true
        } else {
            false
        }
    }

    /**
     * 设置监听
     * @param listener
     */
    fun setOnEditTextChangeListener(listener: OnEditTextChangeListener?) {
        this.listener = listener
    }

    interface OnEditTextChangeListener {
        /**
         * when send button clicked
         * @param content
         */
        fun onClickKeyboardSendBtn(content: String?)

        /**
         * if edit text has focus
         */
        fun onEditTextHasFocus(hasFocus: Boolean)
    }

    init {
        setOnKeyListener(this)
        setOnEditorActionListener(this)
    }
}