package com.kotlin.android.card.monopoly.widget.box

import android.animation.ValueAnimator
import android.content.Context
import android.util.AttributeSet
import android.view.ViewGroup
import android.widget.FrameLayout
import androidx.core.view.children
import com.kotlin.android.card.monopoly.R
import com.kotlin.android.card.monopoly.widget.DrawableTextView
import com.kotlin.android.card.monopoly.widget.countdown.Countdown
import com.kotlin.android.app.data.entity.monopoly.Box
import com.kotlin.android.image.coil.ext.loadImage
import com.kotlin.android.ktx.ext.core.*
import com.kotlin.android.ktx.ext.dimension.dp
import com.kotlin.android.ktx.ext.dimension.dpF
import com.kotlin.android.mtime.ktx.getServerTime
import kotlinx.android.synthetic.main.item_view_box.view.*

/**
 * 宝箱展示视图：铜宝箱、银宝箱、金宝箱、铂金宝箱、钻石宝箱、限量宝箱、今日活动宝箱
 *
 * Created on 2020/8/18.
 *
 * @author o.s
 */
class BoxItemView : FrameLayout {

    private val mBoxWidth = 45.dp
    private val mBoxHeight = 45.dp
    private val mLabelPadding = 2.dp
    private val mTextLabelWidth = 12.dp
    private val mTextLabelHeight = 12.dp

    private var box: Box? = null
    private var countdown: Countdown? = null

    /**
     * 宝箱UI位置，对应1，2，3，4号宝箱
     */
    var position: Int = 0

    var action: ((event: ActionEvent) -> Unit)? = null

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
            boxView?.apply {
                cover?.apply {
                    loadImage(
                        data = this,
                        width = mBoxWidth,
                        height = mBoxHeight)
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
        val view = inflate(context, R.layout.item_view_box, null)
        addView(view)

        boxBgLayout?.setOnClickListener {
            click()
        }
        actionView?.setOnClickListener {
            click()
        }
    }

    private fun click() {
        action?.invoke(ActionEvent(state, position, box))
    }

    private fun notifyChange() {
        when (state) {
            State.DIG_BOX -> {
                cancel()
                digBoxState()
            }
            State.READY -> {
                cancel()
                readyState()
            }
            State.COUNT_DOWN -> {
                countDownState()
            }
            State.EMPTY -> {
                cancel()
                emptyState()
            }
        }
    }

    private fun digBoxState() {
        countdownView.gone()
        boxBgLayout?.setBackgroundResource(R.drawable.ic_dig_box_bg_ready)
        boxView?.setImageDrawable(null)
        boxCoverView?.apply {
            loadImage(
                data = R.drawable.gif_dig_box_anim_bg,
                defaultImgRes = R.drawable.transparent
            )
        }
        gifView?.apply {
            loadImage(
                data = R.drawable.gif_dig_box_smoke,
                defaultImgRes = R.drawable.transparent
            )
        }
        showScanBoxAnim(false)
        emptyBoxView.gone()
        actionView?.apply {
            visible()
            setBackgroundResource(R.drawable.ic_box_btn_green)
            setTextColor(getColor(R.color.color_ffffff))
            setText(R.string.dig_box)
        }
    }

    private fun readyState() {
        countdownView.gone()
        boxBgLayout?.setBackgroundResource(R.drawable.ic_dig_box_bg_full)
//        boxView?.setImageDrawable(null)
        boxCoverView?.setImageDrawable(null)
        gifView?.setImageDrawable(null)
        showScanBoxAnim(false)
        emptyBoxView.gone()
        actionView?.apply {
            visible()
            setBackgroundResource(R.drawable.ic_box_btn_orange)
            setTextColor(getColor(R.color.color_ffffff))
            setText(R.string.open)
        }
    }

    private fun countDownState() {
        countdownView?.apply {
            visible()
            setBackground(
                    colorRes = R.color.color_ffe6bb,
                    cornerRadius = 8.dpF
            )
            compoundDrawablePadding = mLabelPadding
            setDrawable(DrawableTextView.LEFT, getDrawable(R.drawable.ic_label_box_clock), mTextLabelWidth, mTextLabelHeight)
//            getDrawable(R.drawable.ic_label_box_clock)?.apply {
//                setBounds(0, 0, mTextLabelWidth, mTextLabelHeight)
//                setCompoundDrawables(this, null, null, null)
//            }
        }
        boxBgLayout?.setBackgroundResource(R.drawable.ic_dig_box_bg_loading)
//        boxView?.setImageDrawable(null)
        boxCoverView?.setImageDrawable(null)
        gifView?.setImageDrawable(null)
        showScanBoxAnim(false)
        emptyBoxView.gone()
        actionView?.apply {
            visible()
            background = null
            setTextColor(getColor(R.color.color_1d2736))
            setText(R.string.open_now)
        }
    }

    private fun emptyState() {
        box = null
        countdownView.gone()
        boxBgLayout?.setBackgroundResource(R.drawable.ic_dig_box_bg_null)
        boxView?.setImageDrawable(null)
        boxCoverView?.setImageDrawable(null)
        gifView?.setImageDrawable(null)
        showScanBoxAnim(false)
        emptyBoxView.visible()
        actionView.gone()
    }

    fun showScanBoxAnim(isShow: Boolean = true) {
        animationView?.apply {
            if (isShow) {
                visible()
                playAnimation()
//                animationView?.filename
                repeatCount = ValueAnimator.INFINITE
            } else {
                gone()
                cancelAnimation()
            }
        }
    }

    private fun launchCountdown(remainingTime: Long) {
        cancel()
        countdown = Countdown(
                remainingTime = remainingTime,
                complete = {
                    state = State.READY
                }
        ) {
            countdownView?.apply {
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

    data class ActionEvent(
            val state: State,
            val position: Int,
            val box: Box?
    )

    enum class State {

        /**
         * 挖宝箱
         */
        DIG_BOX,

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