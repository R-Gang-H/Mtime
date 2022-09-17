package com.kotlin.android.film.widget.seat.view

import android.content.Context
import android.util.AttributeSet
import android.view.Gravity
import android.view.ViewGroup
import android.widget.FrameLayout
import android.widget.TextView
import androidx.core.view.isVisible
import com.kotlin.android.app.data.entity.film.seat.Seat
import com.kotlin.android.film.R
import com.kotlin.android.film.widget.seat.SeatStyle
import com.kotlin.android.ktx.ext.core.getColor
import com.kotlin.android.ktx.ext.core.marginTop
import com.kotlin.android.ktx.ext.core.setBackground
import com.kotlin.android.ktx.ext.dimension.dp
import com.kotlin.android.ktx.ext.dimension.dpF
import com.kotlin.android.mtime.ktx.PriceUtils

/**
 * 待选/选中座位展示的 item view
 *
 * Created on 2022/2/9.
 *
 * @author o.s
 */
class SeatSelectItemView : FrameLayout {

    companion object {
        const val SHOW_MASK = 0x0000
        const val SHOW_DELETE_TOP = 0x0001
        const val SHOW_DELETE_CENTER = 0x0002
        const val SHOW_TAG = 0x0004
    }

    constructor(context: Context) : super(context)
    constructor(context: Context, attrs: AttributeSet?) : super(context, attrs)
    constructor(context: Context, attrs: AttributeSet?, defStyleAttr: Int) : super(
        context,
        attrs,
        defStyleAttr
    )

    constructor(
        context: Context,
        attrs: AttributeSet?,
        defStyleAttr: Int,
        defStyleRes: Int
    ) : super(context, attrs, defStyleAttr, defStyleRes)

    var clickDelete: ((seat: Seat) -> Unit)? = null

    private var mSeatStyle: SeatStyle? = null
    private var mData: Seat? = null
    private var mShow: Int = 0
    private var mDeleteWidth = 9.dp
    private var mDeleteHeight = mDeleteWidth
    private var mTitleSize = 12F
    private var mDelSize = 10F
    private var mTagSize = 10F
    private var mTitlePaddingStart = 1.dp
    private var mTitlePaddingEnd = 10.dp
    private var mDelMarginEnd = 4.dp
    private var mDelMarginTop = 7.dp
    private var mTag = "惠"

    private val isShowDeleteTopView: Boolean
        get() = mShow and SHOW_DELETE_TOP == SHOW_DELETE_TOP

    private val isShowDeleteCenterView: Boolean
        get() = mShow and SHOW_DELETE_CENTER == SHOW_DELETE_CENTER

    private val isShowTagView: Boolean
        get() = mShow and SHOW_TAG == SHOW_TAG

    private val mTitleView by lazy {
        TextView(context).apply {
            layoutParams = LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT)
            gravity = Gravity.CENTER
            setPadding(mTitlePaddingStart, 0, mTitlePaddingEnd, 0)
            textSize = mTitleSize
            setTextColor(getColor(R.color.color_1d2736))
            setBackground(
                colorRes = R.color.color_f6f6f6,
                cornerRadius = 5.dpF
            )

            setOnClickListener {
                mData?.apply {
                    clickDelete?.invoke(this)
                }
            }
        }
    }
    private val mDeleteView by lazy {
        TextView(context).apply {
            layoutParams = LayoutParams(mDeleteWidth, mDeleteHeight).apply {
                setMargins(0, 0, mDelMarginEnd, 0)
                gravity = Gravity.CENTER_VERTICAL or Gravity.END
            }
            gravity = Gravity.CENTER
            textSize = mDelSize
            setBackgroundResource(R.drawable.ic_label_9_delete_x)

            isVisible = false
        }
    }
    private val mTagView by lazy {
        TextView(context).apply {
            layoutParams = LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT).apply {
                gravity = Gravity.BOTTOM or Gravity.END
            }
            gravity = Gravity.CENTER
            textSize = mTagSize
            text = mTag

            isVisible = false
        }
    }

    init {
        addView(mTitleView)
        addView(mDeleteView)
//        setBackgroundResource(R.drawable.pic_ticket_back)
    }

    fun setData(
        data: Seat,
        style: SeatStyle = SeatStyle.NORMAL,
    ) {
        this.mData = data
        this.mSeatStyle = style
        this.mShow = SHOW_DELETE_CENTER
//        var show = if (data.showDelete) if (data.hasActivity) SHOW_DELETE_TOP else SHOW_DELETE_CENTER else SHOW_MASK
//        show = if (data.showTag) show or SeatItemView.SHOW_TAG else show
        notifyShowChange()
        notifyDataChange()
    }

    private fun notifyShowChange() {
//        mDeleteView.isVisible = isShowTagView
        mDeleteView.apply {
            isVisible = isShowDeleteTopView || isShowDeleteCenterView
            if (isShowDeleteTopView) {
                gravity = Gravity.TOP or Gravity.END
                marginTop = mDelMarginTop
            } else if (isShowDeleteCenterView) {
                gravity = Gravity.CENTER_VERTICAL or Gravity.END
                marginTop = 0
            }
        }
    }

    private fun notifyDataChange() {
        val seat = mData
        mTitleView.text = if (seat != null) {
            "${seat.name}\n¥${PriceUtils.formatPrice(seat.showPrice.toDouble())}元"
        } else {
            ""
        }

        if (SeatStyle.AREA === mSeatStyle) {
            areaLevel()
        } else {
            defLevel()
        }
    }

    private fun areaLevel() {

    }

    private fun defLevel() {

    }
}