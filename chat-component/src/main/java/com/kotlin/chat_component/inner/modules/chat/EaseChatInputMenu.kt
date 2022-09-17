package com.kotlin.chat_component.inner.modules.chat

import android.content.Context
import android.util.AttributeSet
import android.util.Log
import android.view.LayoutInflater
import android.view.MotionEvent
import android.view.View
import android.view.ViewGroup
import android.widget.FrameLayout
import android.widget.LinearLayout
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentActivity
import com.hyphenate.util.DensityUtil
import com.hyphenate.util.EMLog
import com.kotlin.android.ktx.ext.log.e
import com.kotlin.chat_component.R
import com.kotlin.chat_component.inner.domain.EaseEmojicon
import com.kotlin.chat_component.inner.modules.chat.interfaces.*
import com.kotlin.chat_component.inner.utils.EaseSmileUtils

class EaseChatInputMenu @JvmOverloads constructor(
    context: Context?,
    attrs: AttributeSet? = null,
    defStyleAttr: Int = 0
) : LinearLayout(context, attrs, defStyleAttr), IChatInputMenu, EaseChatPrimaryMenuListener,
    EaseEmojiconMenuListener, EaseChatExtendMenuItemClickListener {

    private var chatMenuContainer: LinearLayout? = null
    private var primaryMenuContainer: FrameLayout? = null
    private var extendMenuContainer: FrameLayout? = null
    override var primaryMenu: IChatPrimaryMenu? = null
        private set
    override var emojiconMenu: IChatEmojiconMenu? = null
        private set
    override var chatExtendMenu: IChatExtendMenu? = null
        private set
    private var menuListener: ChatInputMenuListener? = null
    override fun onFinishInflate() {
        super.onFinishInflate()
        chatMenuContainer = findViewById(R.id.chat_menu_container)
        primaryMenuContainer = findViewById(R.id.primary_menu_container)
        extendMenuContainer = findViewById(R.id.extend_menu_container)
        init()
    }

    private fun init() {
        showPrimaryMenu()
        if (chatExtendMenu == null) {
            chatExtendMenu = EaseChatExtendMenu(context)
            (chatExtendMenu as EaseChatExtendMenu).init()
        }
        if (emojiconMenu == null) {
            emojiconMenu = EaseEmojiconMenu(context)
            (emojiconMenu as EaseEmojiconMenu).init()
        }
    }

    override fun setCustomPrimaryMenu(menu: IChatPrimaryMenu?) {
        primaryMenu = menu
        showPrimaryMenu()
    }

    override fun setCustomEmojiconMenu(menu: IChatEmojiconMenu?) {
        emojiconMenu = menu
    }

    override fun setCustomExtendMenu(menu: IChatExtendMenu?) {
        chatExtendMenu = menu
    }

    override fun hideExtendContainer() {
        primaryMenu?.showNormalStatus()
        extendMenuContainer?.visibility = GONE
    }

    override fun showEmojiconMenu(show: Boolean) {
        if (show) {
            showEmojiconMenu()
        } else {
            extendMenuContainer?.visibility = GONE
        }
    }

    override fun showExtendMenu(show: Boolean) {
        if (show) {
            showExtendMenu()
        } else {
            extendMenuContainer?.visibility = GONE
            if (primaryMenu != null) {
                primaryMenu?.hideExtendStatus()
            }
        }
    }

    override fun hideSoftKeyboard() {
        if (primaryMenu != null) {
            primaryMenu?.hideSoftKeyboard()
        }
    }

    override fun setChatInputMenuListener(listener: ChatInputMenuListener?) {
        menuListener = listener
    }

    override fun onBackPressed(): Boolean {
        if (extendMenuContainer?.visibility == VISIBLE) {
            extendMenuContainer?.visibility = GONE
            return false
        }
        return true
    }

    private fun showPrimaryMenu() {
        if (primaryMenu == null) {
            primaryMenu = EaseChatPrimaryMenu(context)
        }
        if (primaryMenu is View) {
            primaryMenuContainer?.removeAllViews()
            primaryMenuContainer?.addView(primaryMenu as View?)
            primaryMenu?.setEaseChatPrimaryMenuListener(this)
        }
        if (primaryMenu is Fragment && context is FragmentActivity) {
            val manager = (context as FragmentActivity).supportFragmentManager
            manager.beginTransaction()
                .replace(R.id.primary_menu_container, (primaryMenu as Fragment?)!!)
                .commitAllowingStateLoss()
            primaryMenu?.setEaseChatPrimaryMenuListener(this)
        }
    }

    private fun showExtendMenu() {
        if (chatExtendMenu == null) {
            chatExtendMenu = EaseChatExtendMenu(context)
            (chatExtendMenu as EaseChatExtendMenu).init()
        }
        if (chatExtendMenu is View) {
            extendMenuContainer?.visibility = VISIBLE
            extendMenuContainer?.removeAllViews()
            extendMenuContainer?.addView(chatExtendMenu as View?)
            chatExtendMenu?.setEaseChatExtendMenuItemClickListener(this)
        }
        if (chatExtendMenu is Fragment && context is FragmentActivity) {
            extendMenuContainer?.visibility = VISIBLE
            val manager = (context as FragmentActivity).supportFragmentManager
            manager.beginTransaction()
                .replace(R.id.extend_menu_container, (chatExtendMenu as Fragment?)!!)
                .commitAllowingStateLoss()
            chatExtendMenu?.setEaseChatExtendMenuItemClickListener(this)
        }
    }

    private fun showEmojiconMenu() {
        if (emojiconMenu == null) {
            emojiconMenu = EaseEmojiconMenu(context)
            (emojiconMenu as EaseEmojiconMenu).init()
        }
        if (emojiconMenu is View) {
            extendMenuContainer?.visibility = VISIBLE
            extendMenuContainer?.removeAllViews()
            extendMenuContainer?.addView(emojiconMenu as View?)
            emojiconMenu?.setEmojiconMenuListener(this)
        }
        if (emojiconMenu is Fragment && context is FragmentActivity) {
            extendMenuContainer?.visibility = VISIBLE
            val manager = (context as FragmentActivity).supportFragmentManager
            manager.beginTransaction()
                .replace(R.id.extend_menu_container, (emojiconMenu as Fragment?)!!)
                .commitAllowingStateLoss()
            emojiconMenu?.setEmojiconMenuListener(this)
        }
    }

    override fun onSendBtnClicked(content: String?) {
        EMLog.i(TAG, "onSendBtnClicked content:$content")
        if (menuListener != null) {
            menuListener?.onSendMessage(content)
        }
    }

    override fun onTyping(s: CharSequence?, start: Int, before: Int, count: Int) {
        EMLog.i(TAG, "onTyping: s = $s")
        if (menuListener != null) {
            menuListener?.onTyping(s, start, before, count)
        }
    }

    override fun onPressToSpeakBtnTouch(v: View?, event: MotionEvent?): Boolean {
        return if (menuListener != null) {
            menuListener!!.onPressToSpeakBtnTouch(v, event)
        } else false
    }

    override fun onToggleVoiceBtnClicked() {
        Log.e("TAG", "onToggleVoiceBtnClicked")
        showExtendMenu(false)
    }

    override fun onToggleTextBtnClicked() {
        EMLog.i(TAG, "onToggleTextBtnClicked")
        showExtendMenu(false)
    }

    override fun onToggleExtendClicked(extend: Boolean) {
        EMLog.i(TAG, "onToggleExtendClicked extend:$extend")
        showExtendMenu(extend)
    }

    override fun onToggleEmojiconClicked(extend: Boolean) {
        EMLog.i(TAG, "onToggleEmojiconClicked extend:$extend")
        showEmojiconMenu(extend)
    }

    override fun onEditTextClicked() {
        EMLog.i(TAG, "onEditTextClicked")
    }

    override fun onEditTextHasFocus(hasFocus: Boolean) {
        EMLog.i(TAG, "onEditTextHasFocus: hasFocus = $hasFocus")
    }

    override fun onExpressionClicked(emojicon: Any?) {
        EMLog.i(TAG, "onExpressionClicked")
        if (emojicon is EaseEmojicon) {
            if (emojicon.type !== EaseEmojicon.Type.BIG_EXPRESSION) {
                if (emojicon.emojiText != null) {
                    primaryMenu?.onEmojiconInputEvent(
                        EaseSmileUtils.getSmiledText(
                            context,
                            emojicon.emojiText
                        )
                    )
                }
            } else {
                if (menuListener != null) {
                    menuListener?.onExpressionClicked(emojicon)
                }
            }
        } else {
            if (menuListener != null) {
                menuListener?.onExpressionClicked(emojicon)
            }
        }
    }

    override fun onDeleteImageClicked() {
        EMLog.i(TAG, "onDeleteImageClicked")
        primaryMenu?.onEmojiconDeleteEvent()
    }

    override fun onChatExtendMenuItemClick(itemId: Int, view: View?) {
        EMLog.i(TAG, "onChatExtendMenuItemClick itemId = $itemId")
        if (menuListener != null) {
            menuListener?.onChatExtendMenuItemClick(itemId, view)
        }
    }

    companion object {
        private val TAG = EaseChatInputMenu::class.java.simpleName
    }

    init {
        LayoutInflater.from(context).inflate(R.layout.ease_widget_chat_input_menu_container, this)
    }
}