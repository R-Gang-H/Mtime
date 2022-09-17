package com.kotlin.android.card.monopoly.widget

import android.content.Context
import android.util.AttributeSet
import android.util.TypedValue
import android.view.Gravity
import android.view.ViewGroup
import android.widget.FrameLayout
import android.widget.ImageView
import android.widget.TextView
import androidx.cardview.widget.CardView
import androidx.core.view.children
import com.kotlin.android.card.monopoly.R
import com.kotlin.android.card.monopoly.widget.countdown.Countdown
import com.kotlin.android.app.data.entity.monopoly.Box
import com.kotlin.android.image.coil.ext.loadImage
import com.kotlin.android.ktx.ext.core.*
import com.kotlin.android.ktx.ext.dimension.dp
import com.kotlin.android.ktx.ext.dimension.dpF
import com.kotlin.android.mtime.ktx.getServerTime

/**
 * 宝箱展示视图：铜宝箱、银宝箱、金宝箱、铂金宝箱、钻石宝箱、限量宝箱、今日活动宝箱
 *
 * Created on 2020/8/18.
 *
 * @author o.s
 */
class BoxItemView : FrameLayout {

    private val mLabelRatio = 42 / 166F
    private val mBoxRatio = 122 / 140F
    private val mLabelWidth = 80.dp
    private val mLabelHeight = (mLabelWidth * mLabelRatio).toInt()
    private val mLabelPadding = 2.dp
    private val mBoxWidth = 68.dp
    private val mBoxHeight = (mBoxWidth * mBoxRatio).toInt()
    private val mTextHeight = 18.dp
    private val mTextPadding = 8.dp
    private val mDrawablePadding = 2.dp
    private val mTextLabelWidth = 12.dp
    private val mTextLabelHeight = 15.dp
    private val mTextSize = 10F
    private val mHighlightTextSize = 12F

    private var boxView: ImageView? = null
    private var labelView: TextView? = null
    private var textView: TextView? = null

    private var box: Box? = null
    private var countdown: Countdown? = null

    var action: ((box: Box) -> Unit)? = null

    var state: State = State.READY
        set(value) {
            field = value
            notifyChange()
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

    /**
     * 回收资源（定时器）
     */
    fun cancel() {
        countdown?.cancel()
    }

    fun setCardBox(box: Box?) {
        this.box = box
        box?.apply {
            labelView?.apply {
                text = cardBoxName ?: ""
            }
            boxView?.apply {
                cover?.apply {
                    loadImage(
                        data = this,
                        width = mBoxWidth,
                        height = mBoxHeight
                    )
                }
            }
            val remainingTime = unlockTime * 1000 - getServerTime()
            if (remainingTime <= 0) {
                state = State.READY
            } else {
                state = State.COUNT_DOWN
                launchCountdown(remainingTime)
            }
        }
        if (box == null) {
            state = State.EMPTY
        }
    }

    fun getCardBox(): Box? {
        return box
    }

    override fun onAttachedToWindow() {
        super.onAttachedToWindow()
        fitsSystemWindows = (parent as ViewGroup).fitsSystemWindows
        children.forEach {
            it.fitsSystemWindows = fitsSystemWindows
        }
    }

    private fun initView() {
        labelView = TextView(context).apply {
            layoutParams = LayoutParams(mLabelWidth, mLabelHeight).apply {
                gravity = Gravity.CENTER_HORIZONTAL
                topMargin = mBoxHeight
            }
            gravity = Gravity.CENTER
            setPadding(0, 0, 0, mLabelPadding)
            setTextSize(TypedValue.COMPLEX_UNIT_SP, mTextSize)
            setTextColor(getColor(R.color.color_07916a))
            setBackgroundResource(R.drawable.ic_box_base)
            addView(this)
        }
        CardView(context).apply {
            layoutParams = LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT).apply {
                gravity = Gravity.CENTER_HORIZONTAL
            }
            radius = 3.dpF
            cardElevation = 0F
            useCompatPadding = false
            setCardBackgroundColor(getColor(R.color.transparent))
            this@BoxItemView.addView(this)
            boxView = ImageView(context).apply {
                layoutParams = LayoutParams(mBoxWidth, mBoxHeight).apply {
                    gravity = Gravity.CENTER_HORIZONTAL
                }
                scaleType = ImageView.ScaleType.CENTER_CROP
                addView(this)
            }
        }
        textView = TextView(context).apply {
            layoutParams = LayoutParams(LayoutParams.WRAP_CONTENT, mTextHeight).apply {
                gravity = Gravity.CENTER_HORIZONTAL or Gravity.BOTTOM
            }
            gravity = Gravity.CENTER
            setTextSize(TypedValue.COMPLEX_UNIT_SP, mTextSize)
            setTextColor(getColor(R.color.color_4e5e73))
            compoundDrawablePadding = mDrawablePadding
            setOnClickListener {
                if (state == State.READY) {
                    box?.apply {
                        action?.invoke(this)
                    }
                }
            }
            addView(this)
        }
    }

    private fun notifyChange() {
        when (state) {
            State.WAITING -> {
                waitingState()
            }
            State.READY -> {
                readyState()
            }
            State.COUNT_DOWN -> {
                countDownState()
            }
            State.EMPTY -> {
                emptyState()
            }
        }
    }

    private fun waitingState() {
        textView?.apply {
            visible()
            setBackground(
                    colorRes = R.color.color_b3f6dc,
                    strokeColorRes = android.R.color.transparent,
                    strokeWidth = 4.dp,
                    cornerRadius = 9.dpF
            )
            setPadding(0, 0, mTextPadding, 0)
            setTextSize(TypedValue.COMPLEX_UNIT_SP, mTextSize)
            setTextColor(getColor(R.color.color_4e5e73))
            getDrawable(R.drawable.ic_label_clock)?.apply {
                setBounds(0, 0, mTextLabelWidth, mTextLabelHeight)
                setCompoundDrawables(this, null, null, null)
            }
        }
    }

    private fun readyState() {
        textView?.apply {
            visible()
            background = getGradientDrawable(
                    color = getColor(R.color.color_ff9a3a),
                    endColor = getColor(R.color.color_ff6723),
                    cornerRadius = 9.dpF
            )
            setPadding(mTextPadding, 0, mTextPadding, 0)
            setTextSize(TypedValue.COMPLEX_UNIT_SP, mHighlightTextSize)
            setTextColor(getColor(R.color.color_ffffff))
            setCompoundDrawables(null, null, null, null)
            text = getString(R.string.open_box)
        }
    }

    private fun countDownState() {
        textView?.apply {
            visible()
            setBackground(
                    colorRes = R.color.color_b3f6dc,
                    strokeColorRes = android.R.color.transparent,
                    strokeWidth = 4.dp,
                    cornerRadius = 9.dpF
            )
            setPadding(0, 0, mTextPadding, 0)
            setTextSize(TypedValue.COMPLEX_UNIT_SP, mTextSize)
            setTextColor(getColor(R.color.color_4e5e73))
            getDrawable(R.drawable.ic_label_clock)?.apply {
                setBounds(0, 0, mTextLabelWidth, mTextLabelHeight)
                setCompoundDrawables(this, null, null, null)
            }
        }
    }

    private fun emptyState() {
        labelView?.apply {
            text = "空宝箱"
        }
        boxView?.apply {
            setImageResource(R.drawable.ic_none_box)
        }
        textView?.gone()
        box = null
    }

    private fun launchCountdown(remainingTime: Long) {
        countdown?.cancel()
        countdown = Countdown(
                remainingTime = remainingTime,
                complete = {
                    state = State.READY
                }
        ) {
            textView?.apply {
                text = if (it.totalMinute > 0L) {
                    getString(R.string.card_box_count_down_minute, it.totalMinute)
                } else {
                    getString(R.string.card_box_count_down_second, it.totalSecond)
                }
            }
        }.apply {
            start()
        }
    }

    enum class State {

        /**
         * 等待状态
         */
        WAITING,

        /**
         * 就绪状态
         */
        READY,

        /**
         * 倒计时状态
         */
        COUNT_DOWN,

        /**
         * 空宝箱状态
         */
        EMPTY
    }

}