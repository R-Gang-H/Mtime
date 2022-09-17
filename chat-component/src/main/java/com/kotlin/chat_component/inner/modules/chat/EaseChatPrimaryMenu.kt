package com.kotlin.chat_component.inner.modules.chat

import android.annotation.SuppressLint
import android.app.Activity
import android.content.Context
import android.graphics.drawable.Drawable
import android.text.Editable
import android.text.TextUtils
import android.text.TextWatcher
import android.util.AttributeSet
import android.util.Log
import android.view.*
import android.view.inputmethod.InputMethodManager
import android.widget.*
import com.kotlin.chat_component.R
import com.kotlin.chat_component.inner.modules.chat.interfaces.EaseChatPrimaryMenuListener
import com.kotlin.chat_component.inner.modules.chat.interfaces.IChatPrimaryMenu
import kotlinx.android.synthetic.main.ease_widget_chat_primary_menu.view.*
import kotlinx.coroutines.*

/**
 * 输入菜单
 */
open class EaseChatPrimaryMenu @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyleAttr: Int = 0
) : RelativeLayout(context, attrs, defStyleAttr), IChatPrimaryMenu, View.OnClickListener,
    EaseInputEditText.OnEditTextChangeListener, TextWatcher {

    private var listener: EaseChatPrimaryMenuListener? = null
    private var menuType: EaseInputMenuStyle? = EaseInputMenuStyle.DISABLE_EMOJICON //菜单展示形式
    private var inputManager: InputMethodManager


    protected var activity: Activity

    init {
        LayoutInflater.from(context).inflate(R.layout.ease_widget_chat_primary_menu, this)
        activity = context as Activity
        inputManager = context.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
        initViews()
    }

    private fun initViews() {
        et_sendmessage.requestFocus()
        showNormalStatus()
        initListener()
    }

    @SuppressLint("ClickableViewAccessibility")
    private fun initListener() {
        btn_send?.setOnClickListener(this)
        btn_set_mode_keyboard?.setOnClickListener(this)
        btn_set_mode_voice?.setOnClickListener(this)
        btn_more?.setOnClickListener(this)
        rl_face?.setOnClickListener(this)
        et_sendmessage?.setOnClickListener(this)
        et_sendmessage?.setOnEditTextChangeListener(this)
        et_sendmessage?.addTextChangedListener(this)
        btn_press_to_speak.setOnTouchListener { view, motionEvent ->
            if (listener != null) {
                return@setOnTouchListener listener!!.onPressToSpeakBtnTouch(view, motionEvent)
            }
            false
        }
    }

    private fun checkSendButton() {
        if (TextUtils.isEmpty(editTextMessage?.text.toString().trim { it <= ' ' })) {
            btn_more?.visibility = VISIBLE
            btn_send?.visibility = GONE
        } else {
            btn_more?.visibility = GONE
            btn_send?.visibility = VISIBLE
        }
    }

    override fun onDetachedFromWindow() {
        super.onDetachedFromWindow()
        editTextMessage?.removeTextChangedListener(this)
    }

    override fun setMenuShowType(style: EaseInputMenuStyle?) {
        menuType = style
        checkMenuType()
    }

    override fun showNormalStatus() {
        hideSoftKeyboard()
        btn_set_mode_voice?.visibility = VISIBLE
        btn_set_mode_keyboard?.visibility = GONE
        edittext_layout?.visibility = VISIBLE
        btn_press_to_speak?.visibility = GONE
        hideExtendStatus()
        checkSendButton()
        checkMenuType()
    }

    override fun showTextStatus() {
        btn_set_mode_voice?.visibility = VISIBLE
        btn_set_mode_keyboard?.visibility = GONE
        edittext_layout?.visibility = VISIBLE
        btn_press_to_speak?.visibility = GONE
        hideExtendStatus()
        showSoftKeyboard(editTextMessage)
        checkSendButton()
        checkMenuType()
        if (listener != null) {
            listener?.onToggleTextBtnClicked()
        }
    }

    override fun showVoiceStatus() {
        hideSoftKeyboard()
        btn_set_mode_voice?.visibility = GONE
        btn_set_mode_keyboard?.visibility = VISIBLE
        edittext_layout?.visibility = GONE
        btn_press_to_speak?.visibility = VISIBLE
        hideExtendStatus()
        checkMenuType()
        if (listener != null) {
            listener?.onToggleVoiceBtnClicked()
        }
    }

    override fun showEmojiconStatus() {
        btn_set_mode_voice?.visibility = VISIBLE
        btn_set_mode_keyboard?.visibility = GONE
        edittext_layout?.visibility = VISIBLE
        btn_press_to_speak?.visibility = GONE
        btn_more?.isChecked = false
        if (iv_face_normal?.visibility == VISIBLE) {
            hideSoftKeyboard()
            showSelectedFaceImage()
        } else {
            showSoftKeyboard(editTextMessage)
            showNormalFaceImage()
        }
        checkMenuType()
        if (listener != null) {
            listener?.onToggleEmojiconClicked(iv_face_checked?.visibility == VISIBLE)
        }
    }

    override fun showMoreStatus() {
        if (btn_more?.isChecked == true) {
            hideSoftKeyboard()
            btn_set_mode_voice?.visibility = VISIBLE
            btn_set_mode_keyboard?.visibility = GONE
            edittext_layout?.visibility = VISIBLE
            btn_press_to_speak?.visibility = GONE
            showNormalFaceImage()
        } else {
            showTextStatus()
        }
        checkMenuType()
        if (listener != null) {
            btn_more?.isChecked?.let {
                CoroutineScope(Dispatchers.Main).launch {
                    //为了防止hideSoftKeyboard没有执行完 后续UI操作高度计算有误延时100ms执行后续操作
                    delay(100)
                    listener?.onToggleExtendClicked(it)
                }
            }
        }
    }

    override fun hideExtendStatus() {
        btn_more?.isChecked = false
        showNormalFaceImage()
    }

    override fun onEmojiconInputEvent(emojiContent: CharSequence?) {
        editTextMessage?.append(emojiContent)
    }

    override fun onEmojiconDeleteEvent() {
        if (!TextUtils.isEmpty(editTextMessage?.text)) {
            val event =
                KeyEvent(0, 0, 0, KeyEvent.KEYCODE_DEL, 0, 0, 0, 0, KeyEvent.KEYCODE_ENDCALL)
            editTextMessage?.dispatchKeyEvent(event)
        }
    }

    override fun onTextInsert(text: CharSequence?) {
        val start = editTextMessage?.selectionStart
        val editable = editTextMessage?.editableText
        start?.let { editable?.insert(it, text) }
        showTextStatus()
    }

    override val editTextMessage: EditText?
        get() = et_sendmessage


    override fun setMenuBackground(bg: Drawable?) {
        rl_bottom?.background = bg
    }

    override fun setSendButtonBackground(bg: Drawable?) {
        btn_send?.background = bg
    }

    override fun onClick(v: View) {
        val id = v.id
        if (id == R.id.btn_send) { //发送
            if (listener != null) {
                val s = editTextMessage?.text.toString()
                listener?.onSendBtnClicked(s)
            }
        } else if (id == R.id.btn_set_mode_voice) { //切换到语音模式
            showVoiceStatus()
        } else if (id == R.id.btn_set_mode_keyboard) { //切换到文本模式
            showTextStatus()
        } else if (id == R.id.btn_more) { //切换到更多模式
            showMoreStatus()
        } else if (id == R.id.et_sendmessage) { //切换到文本模式
            showTextStatus()
        } else if (id == R.id.rl_face) { //切换到表情模式
            showEmojiconStatus()
        }
    }

    override fun onClickKeyboardSendBtn(content: String?) {
        if (listener != null) {
            listener?.onSendBtnClicked(content)
        }
    }

    override fun onEditTextHasFocus(hasFocus: Boolean) {
        if (listener != null) {
            listener?.onEditTextHasFocus(hasFocus)
        }
    }

    private fun checkMenuType() {
        when (menuType) {
            EaseInputMenuStyle.DISABLE_VOICE -> {
                btn_set_mode_voice?.visibility = GONE
                btn_set_mode_keyboard?.visibility = GONE
                btn_press_to_speak?.visibility = GONE
            }
            EaseInputMenuStyle.DISABLE_EMOJICON -> {
                rl_face?.visibility = GONE
            }
            EaseInputMenuStyle.DISABLE_VOICE_EMOJICON -> {
                btn_set_mode_voice?.visibility = GONE
                btn_set_mode_keyboard?.visibility = GONE
                btn_press_to_speak?.visibility = GONE
                rl_face?.visibility = GONE
            }
            EaseInputMenuStyle.ONLY_TEXT -> {
                btn_set_mode_voice?.visibility = GONE
                btn_set_mode_keyboard?.visibility = GONE
                btn_press_to_speak?.visibility = GONE
                rl_face?.visibility = GONE
                btn_more?.visibility = GONE
            }
        }
    }

    private fun showSendButton(s: CharSequence) {
        if (!TextUtils.isEmpty(s)) {
            btn_more?.visibility = GONE
            btn_send?.visibility = VISIBLE
        } else {
            btn_more?.visibility = VISIBLE
            btn_send?.visibility = GONE
        }
        checkMenuType()
    }

    private fun showNormalFaceImage() {
        iv_face_normal?.visibility = VISIBLE
        iv_face_checked?.visibility = INVISIBLE
    }

    private fun showSelectedFaceImage() {
        iv_face_normal?.visibility = INVISIBLE
        iv_face_checked?.visibility = VISIBLE
    }

    /**
     * hide soft keyboard
     */
    override fun hideSoftKeyboard() {
        if (editTextMessage == null) {
            return
        }
        editTextMessage?.requestFocus()
        if (activity.window.attributes.softInputMode != WindowManager.LayoutParams.SOFT_INPUT_STATE_HIDDEN) {
            if (activity.currentFocus != null) inputManager.hideSoftInputFromWindow(
                activity.currentFocus?.windowToken, InputMethodManager.HIDE_NOT_ALWAYS
            )
        }
    }

    /**
     * show soft keyboard
     *
     * @param et
     */
    private fun showSoftKeyboard(et: EditText?) {
        if (et == null) {
            return
        }
        et.requestFocus()
        inputManager.showSoftInput(et, InputMethodManager.SHOW_IMPLICIT)
    }

    override fun setEaseChatPrimaryMenuListener(listener: EaseChatPrimaryMenuListener?) {
        this.listener = listener
    }

    override fun beforeTextChanged(s: CharSequence, start: Int, count: Int, after: Int) {}
    override fun onTextChanged(s: CharSequence, start: Int, before: Int, count: Int) {
        Log.e("TAG", this.javaClass.simpleName + " onTextChanged s:" + s)
        showSendButton(s)
        if (listener != null) {
            listener?.onTyping(s, start, before, count)
        }
    }

    override fun afterTextChanged(s: Editable) {
        Log.e("TAG", this.javaClass.simpleName + " afterTextChanged s:" + s)
    }
}