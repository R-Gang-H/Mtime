package com.kotlin.android.comment.component.bar.item

import android.content.Context
import android.util.AttributeSet
import android.util.TypedValue
import android.view.Gravity
import android.view.View
import android.widget.FrameLayout
import android.widget.ImageView
import android.widget.TextView
import androidx.annotation.DrawableRes
import com.kotlin.android.comment.component.R
import com.kotlin.android.ktx.ext.core.getColor
import com.kotlin.android.ktx.ext.core.getDrawable
import com.kotlin.android.ktx.ext.core.setBackground
import com.kotlin.android.ktx.ext.dimension.dp
import com.kotlin.android.ktx.ext.dimension.dpF
import com.kotlin.android.ktx.ext.statelist.getDrawableStateList
import com.kotlin.android.mtime.ktx.formatCount

/**
 * 栏按钮项:
 *
 * Created on 2020/7/31.
 *
 * @author o.s
 */
class BarButtonItem : FrameLayout {
    private val mWidth = 50.dp
    private val mHeight = mWidth
    private val mIconWidth = 40.dp
    private val mIconHeight = mWidth
    private val mBubbleHeight = 16.dp
    private val mBubblePadding = 5.dp
    private val mBubbleMarginTop = 3.dp
    private val mBubbleMarginEnd = 1.dp
    private val mBubbleTextSize = 10F

    private var iconView: ImageView? = null
    private var bubbleView: TextView? = null

    var action: ((type: Type, isSelected: Boolean) -> Unit)? = null

    var type: Type = Type.COMMENT
        set(value) {
            field = value
            notifyTypeChange(value)
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

    private fun initView() {
        initIcon()
        initBubble()
    }

    private fun notifyTypeChange(type: Type) {
        iconView?.setImageDrawable(getDrawableStateList(normal = getDrawable(type.icon), selected = getDrawable(type.iconHighlight)))
    }

    private fun initIcon() {
        iconView = ImageView(context).apply {
            layoutParams = LayoutParams(mIconWidth, mIconHeight).apply {
                gravity = Gravity.CENTER
            }
            setOnClickListener {
                action?.invoke(type, isSelected)
            }
        }
        addView(iconView)
    }

    private fun initBubble() {
        bubbleView = TextView(context).apply {
            layoutParams = LayoutParams(LayoutParams.WRAP_CONTENT, mBubbleHeight).apply {
                gravity = Gravity.END
                topMargin = mBubbleMarginTop
                marginEnd = mBubbleMarginEnd
            }
            gravity = Gravity.CENTER
            setPadding(mBubblePadding, 0, mBubblePadding, 0)
            setTextColor(getColor(android.R.color.white))
            setTextSize(TypedValue.COMPLEX_UNIT_SP, mBubbleTextSize)
            setBackground(
                    colorRes = R.color.color_8798af,
                    strokeColorRes = android.R.color.white,
                    strokeWidth = 1.dp,
                    cornerRadius = 8.dpF
            )
            visibility = View.GONE
        }
        addView(bubbleView)
    }

    private var tipsDigits:Long = 0L//控件对应数字
    fun setTips(tips: Long) {
        this.tipsDigits = tips
        bubbleView?.apply {
            text = formatCount(tips)
            visibility = if (tips > 0) {
                View.VISIBLE
            } else {
                View.GONE
            }
        }
    }

    fun getTips():Long = tipsDigits
//    fun getTips(): Long {
//        val tips = bubbleView?.text.toString().orEmpty()
//        return if (TextUtils.isEmpty(tips) || tips.isDigitsOnly().not()) {
//            0L
//        } else {
//            tips.toLong()
//        }
//    }

    /**
     * BarButtonItem Type
     */
    enum class Type(@DrawableRes val icon: Int, @DrawableRes val iconHighlight: Int) {
        PHOTO(R.drawable.icon_publish_add_photo, R.drawable.icon_publish_add_photo),
        MOVIE(R.drawable.icon_publish_add_movie, R.drawable.icon_publish_add_movie),
        FAMILY(R.drawable.icon_publish_family, R.drawable.icon_publish_family),
        DELETE(R.drawable.icon_publish_reset, R.drawable.icon_publish_reset),
        KEYBOARD(R.drawable.icon_publish_keyboard, R.drawable.icon_publish_keyboard),
        EMOJI(R.drawable.ic_comment_emoji, R.drawable.ic_comment_emoji),
        COMMENT(R.drawable.ic_comment_msg, R.drawable.ic_comment_msg),
        PRAISE(R.drawable.ic_comment_praise, R.drawable.ic_comment_praise_highlight),
        DISPRAISE(R.drawable.ic_comment_dispraise, R.drawable.ic_comment_dispraise_highlight),
        FAVORITE(R.drawable.ic_comment_favorite, R.drawable.ic_comment_favorite_highlight),
        SHARE(R.drawable.ic_comment_share, R.drawable.ic_comment_share),
        SEND(R.drawable.ic_comment_send, R.drawable.ic_comment_send),
    }
}