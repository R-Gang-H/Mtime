package com.kotlin.android.card.monopoly.widget.nickname

import android.content.Context
import android.graphics.Color
import android.graphics.Typeface
import android.text.TextUtils
import android.util.AttributeSet
import android.util.TypedValue
import android.view.ViewGroup
import androidx.appcompat.widget.AppCompatTextView
import com.kotlin.android.card.monopoly.R
import com.kotlin.android.ktx.ext.core.getColor
import com.kotlin.android.ktx.ext.dimension.dp
import com.kotlin.android.ktx.ext.statelist.getDrawableStateList

/**
 * 用户昵称视图：登录态展示 [State]
 *
 * Created on 2020/9/11.
 *
 * @author o.s
 */
class NickNameView : AppCompatTextView {

    private var mHeight = 16.dp
    private var mTextSize = 12F

    var state: State = State.UNKNOWN
        set(value) {
            field = value
            notifyChange()
        }

    var isOnline: Boolean = false
        set(value) {
            field = value
            state = if (value) {
                State.ONLINE
            } else {
                State.OFFLINE
            }
        }

    private val mStateDrawable by lazy {
        getDrawableStateList(
                normalRes = R.drawable.ic_user_state_offline,
                activatedRes = R.drawable.ic_user_state_online
        ).apply {
            setBounds(0, 0, mHeight, mHeight)
        }
    }

    constructor(context: Context) : super(context) {
        initView()
    }

    constructor(context: Context, attrs: AttributeSet) : super(context, attrs) {
        initView()
    }

    constructor(context: Context, attrs: AttributeSet, defStyleAttr: Int) : super(context, attrs, defStyleAttr) {
        initView()
    }

    override fun onAttachedToWindow() {
        super.onAttachedToWindow()
        fitsSystemWindows = (parent as ViewGroup).fitsSystemWindows
    }

    private fun initView() {
//        setTextColor(getColor(R.color.color_4e5e73))
//        setTextSize(TypedValue.COMPLEX_UNIT_SP, mTextSize)
//        typeface = Typeface.defaultFromStyle(Typeface.BOLD)
        setSingleLine()
        ellipsize = TextUtils.TruncateAt.END
    }

    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec)
        mHeight = measuredHeight
    }

    private fun notifyChange() {
        when (state) {
            State.OFFLINE -> {
                offlineState()
            }
            State.ONLINE -> {
                onlineState()
            }
            State.UNKNOWN -> {
                unknownState()
            }
        }
    }

    private fun offlineState() {
        setCompoundDrawables(null, null, mStateDrawable, null)
        isActivated = false
    }

    private fun onlineState() {
        setCompoundDrawables(null, null, mStateDrawable, null)
        isActivated = true

    }

    private fun unknownState() {
        setCompoundDrawables(null, null, null, null)
        isActivated = false
    }

    /**
     * 用户状态
     */
    enum class State {

        /**
         * 未知
         */
        UNKNOWN,

        /**
         * 离线
         */
        OFFLINE,

        /**
         * 在线
         */
        ONLINE;

        companion object {
            fun obtain(isOnline: Boolean): State {
                return if (isOnline) {
                    ONLINE
                } else {
                    OFFLINE
                }
            }
        }
    }
}