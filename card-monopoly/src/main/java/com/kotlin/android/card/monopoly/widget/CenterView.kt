package com.kotlin.android.card.monopoly.widget

import android.content.Context
import android.util.AttributeSet
import android.util.TypedValue
import android.view.Gravity
import android.view.View
import android.view.ViewGroup
import android.widget.FrameLayout
import android.widget.ImageView
import android.widget.TextView
import androidx.annotation.DrawableRes
import androidx.annotation.StringRes
import androidx.core.view.children
import com.kotlin.android.card.monopoly.R
import com.kotlin.android.card.monopoly.widget.suit.gallery.SuitGalleryView
import com.kotlin.android.app.data.entity.monopoly.Suit
import com.kotlin.android.ktx.ext.core.getColor
import com.kotlin.android.ktx.ext.core.gone
import com.kotlin.android.ktx.ext.core.visible
import com.kotlin.android.ktx.ext.dimension.dp

/**
 * 大富翁中间套装展示：
 *
 * Created on 2020/8/18.
 *
 * @author o.s
 */
class CenterView : FrameLayout {

    private val mWidth = 204.dp
    private val mHeight = 185.dp
    private val mEmptyWidth = 135.dp
    private val mEmptyHeight = 142.dp
    private val mSuitGalleryWidth = 204.dp
    private val mSuitGalleryHeight = 170.dp
    private val mIconEmptyWidth = 135.dp
    private val mIconEmptyHeight = 107.dp
//    private val mIconCenterWidth = 105.dp
//    private val mIconCenterHeight = 160.dp
//    private val mIconWidth = 58.dp
//    private val mIconHeight = 90.dp
//    private val mIconMarginTop = 40.dp
    private val mIconEmptyMarginTop = 10.dp
    private val mTextSize = 15F

    private val suitGalleryView: SuitGalleryView by lazy {
        SuitGalleryView(context).apply {
            layoutParams = LayoutParams(mSuitGalleryWidth, mSuitGalleryHeight)
            itemListener = object : SuitGalleryView.ItemListener {
                override fun onItemChanged(position: Int) {
                }

                override fun onItemClick(view: View, position: Int) {
                }

                override fun onHighlightItemClick(view: View, position: Int) {
                }
            }
        }
    }
    private var textView: TextView? = null
    private var emptyView: ImageView? = null
    private var emptyTextView: TextView? = null

    /**
     * 外部设置的（滚动到）中心位置
     */
    var position: Int = 2
        set(value) {
            field = value
            suitGalleryView.position = value
        }

    var action: ((v: View) -> Unit)? = null

    var state: State = State.NULL
        set(value) {
            field = value
            notifyChangeState()
        }

    fun setData(data: List<Suit>?) {
        when {
            data == null -> {
                state = State.NULL
            }
            data.isEmpty() -> {
                state = State.EMPTY
                suitGalleryView.setData(data)
            }
            else -> {
                state = State.NORMAL
                suitGalleryView.setData(data)
            }
        }
    }

    /**
     * 获取当前选中的数据
     */
    fun getCurrentData(): Suit? {
        return suitGalleryView.getCurrentData()
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
        initNormalView()
        initEmptyView()
        state = State.NULL
    }

    private fun initNormalView() {
//        leftView = initImageView(R.drawable.ic_card_center_placeholder, mIconWidth, mIconHeight, Gravity.START, mIconMarginTop)
//        rightView = initImageView(R.drawable.ic_card_center_placeholder, mIconWidth, mIconHeight, Gravity.END, mIconMarginTop)
//        centerView = initImageView(R.drawable.ic_card_center_placeholder, mIconCenterWidth, mIconCenterHeight, Gravity.CENTER_HORIZONTAL)
        addView(suitGalleryView)
        textView = initTextView(R.string.suit_detail_).apply {
            setOnClickListener {
                action?.invoke(it)
            }
        }
    }

    private fun initEmptyView() {
        emptyView = initImageView(R.mipmap.ic_empty, mIconEmptyWidth, mIconEmptyHeight, Gravity.CENTER_HORIZONTAL, mIconEmptyMarginTop)
        emptyTextView = initTextView(R.string.you_do_not_have_a_suit_yet)
    }

    private fun initImageView(@DrawableRes iconRes: Int, width: Int, height: Int, gravity: Int, margin: Int = 0): ImageView {
        return ImageView(context).apply {
            LayoutParams(width, height).apply {
                this.gravity = gravity
                topMargin = margin
                layoutParams = this
            }
            setImageResource(iconRes)
            gone()
            addView(this)
        }
    }

    private fun initTextView(@StringRes resId: Int): TextView {
        return TextView(context).apply {
            LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT).apply {
                gravity = Gravity.CENTER_HORIZONTAL or Gravity.BOTTOM
                layoutParams = this
            }
            setTextColor(getColor(R.color.color_ffffff))
            setTextSize(TypedValue.COMPLEX_UNIT_SP, mTextSize)
            setText(resId)
            gone()
            addView(this)
        }
    }

    private fun notifyChangeState() {
        when (state) {
            State.NORMAL -> {
                normalState()
            }
            State.EMPTY -> {
                emptyState()
            }
            State.NULL -> {
                nullState()
            }
        }
    }

    private fun normalState() {
        post {
            val params = layoutParams
            params.width = mWidth
            params.height = mHeight
            layoutParams = params
        }

//        leftView?.visible()
//        rightView?.visible()
//        centerView?.visible()
        suitGalleryView.visible()
        textView?.visible()

        emptyView?.gone()
        emptyTextView?.gone()
    }

    private fun emptyState() {
        post {
            val params = layoutParams
            params.width = mWidth
            params.height = mHeight
            layoutParams = params
        }

//        leftView?.visible()
//        rightView?.visible()
//        centerView?.visible()
        suitGalleryView.visible()
        textView?.visible()

        emptyView?.gone()
        emptyTextView?.gone()
    }

    private fun nullState() {
        post {
            val params = layoutParams
            params.width = mEmptyWidth
            params.height = mEmptyHeight
            layoutParams = params
        }

//        leftView?.gone()
//        rightView?.gone()
//        centerView?.gone()
        suitGalleryView.gone()
        textView?.gone()

        emptyView?.visible()
        emptyTextView?.visible()
    }

    fun recycle() {
        suitGalleryView.recycle()
    }

    enum class State {

        /**
         * 正常状态
         */
        NORMAL,

        /**
         * 空状态
         */
        EMPTY,

        /**
         * NULL状态
         */
        NULL
    }
}