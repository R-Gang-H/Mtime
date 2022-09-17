package com.kotlin.android.card.monopoly.widget

import android.content.Context
import android.text.TextUtils
import android.util.AttributeSet
import android.util.TypedValue
import android.view.Gravity
import android.view.View
import android.view.ViewGroup
import android.widget.FrameLayout
import android.widget.TextView
import androidx.core.view.children
import com.kotlin.android.card.monopoly.R
import com.kotlin.android.widget.marquee.MarqueeTextView
import com.kotlin.android.ktx.ext.core.getColor
import com.kotlin.android.ktx.ext.core.getDrawable
import com.kotlin.android.ktx.ext.core.gone
import com.kotlin.android.ktx.ext.core.setBackground
import com.kotlin.android.ktx.ext.core.visible
import com.kotlin.android.ktx.ext.dimension.dp
import com.kotlin.android.ktx.ext.dimension.dpF

/**
 * 用户互动气泡：对TA使用道具、您已成为某某某的奴隶...等
 *
 * Created on 2020/8/18.
 *
 * @author o.s
 */
class TipsBubble : FrameLayout {

    private val mHeight = 35.dp
    private val mIconWidth = 20.dp
    private val mIconHeight = 20.dp
    private val mLabelWidth = 12.dp
    private val mLabelHeight = 12.dp
    private val mTextHeight = 29.dp
    private val mTextPadding = 7.dp
    private var mTextSize = 12F

    private var textView: TextView? = null
    private var labelView: View? = null

    var action: ((style: Style) -> Unit)? = null

    var style: Style = Style.NORMAL
        set(value) {
            field = value
            notifyChange()
        }

    fun setTips(text: String? = null) {
        if (TextUtils.isEmpty(text)) {
            gone()
        } else {
            visible()
            val len = text?.length ?: 0
            when {
                len < 22 -> {
                    setBackgroundResource(R.drawable.ic_bubble_normal_bg_9)
                }
                else -> {
                    setBackgroundResource(R.drawable.ic_bubble_normal_bg)
                }
            }
            textView?.text = text
            style = Style.NORMAL
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
        children.forEach {
            it.fitsSystemWindows = fitsSystemWindows
        }
    }

    private fun initView() {
        textView = MarqueeTextView(context).apply {
            LayoutParams(LayoutParams.WRAP_CONTENT, mTextHeight).apply {
                gravity = Gravity.BOTTOM
                layoutParams = this
            }
            gravity = Gravity.CENTER_VERTICAL
            setTextSize(TypedValue.COMPLEX_UNIT_SP, mTextSize)
            setPadding(mTextPadding, 0, mTextPadding, 0)
        }
        labelView = View(context).apply {
            LayoutParams(mLabelWidth, mLabelHeight).apply {
                layoutParams = this
            }
            setBackgroundResource(R.drawable.ic_triangle_end)
            gone()
        }

        addView(textView)
        addView(labelView)

        style = Style.ACTION
    }

    private fun notifyChange() {
        when (style) {
            Style.ACTION -> {
                actionStyle()
            }
            Style.NORMAL -> {
                normalStyle()
            }
        }
    }

    private fun actionStyle() {
        background = null
        labelView?.visible()
        textView?.apply {
            setBackground(colorRes = R.color.color_ffffff, cornerRadius = 6.dpF)
            setTextColor(getColor(R.color.color_1fc4ca))
            getDrawable(R.drawable.ic_magic_wand)?.apply {
                setBounds(0, 0, mIconWidth, mIconHeight)
                setCompoundDrawables(this, null, null, null)
            }
            setText(R.string.use_props_on_ta)
            setOnClickListener {
                action?.invoke(style)
            }
        }
    }

    private fun normalStyle() {
//        setBackgroundResource(R.drawable.kuang)
        labelView?.gone()
        textView?.apply {
            background = null
            setTextColor(getColor(R.color.color_20a0da))
            setCompoundDrawables(null, null, null, null)
            setOnClickListener {
                action?.invoke(style)
            }
        }
    }

    enum class Style {
        NORMAL,
        ACTION
    }
}