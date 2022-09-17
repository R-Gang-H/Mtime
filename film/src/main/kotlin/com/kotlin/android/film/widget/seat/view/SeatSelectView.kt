package com.kotlin.android.film.widget.seat.view

import android.content.Context
import android.graphics.Rect
import android.util.AttributeSet
import android.view.Gravity
import android.view.View
import android.widget.TextView
import androidx.gridlayout.widget.GridLayout
import com.kotlin.android.app.data.entity.film.seat.Seat
import com.kotlin.android.app.data.entity.film.seat.SeatInfo
import com.kotlin.android.film.R
import com.kotlin.android.film.widget.seat.SeatManager
import com.kotlin.android.film.widget.seat.SeatStyle
import com.kotlin.android.ktx.ext.dimension.dp
import com.kotlin.android.ktx.ext.core.getColor
import com.kotlin.android.ktx.ext.orZero
import com.kotlin.android.ktx.ext.statelist.getColorStateList

/**
 * 选中的座位列表视图（非座位图）
 *
 * Created on 2022/2/9.
 *
 * @author o.s
 */
class SeatSelectView : GridLayout {
    constructor(context: Context?, attrs: AttributeSet?, defStyle: Int) : super(
        context,
        attrs,
        defStyle
    )

    constructor(context: Context?, attrs: AttributeSet?) : super(context, attrs)
    constructor(context: Context?) : super(context)

    private var mItemWidth: Int = 0
    private var mItemHeight: Int = 50.dp
    private var mItemHeightAuto: Int = 26.dp
//    private var isDataSetChange = false
    private var mSeatStyle: SeatStyle = SeatStyle.NORMAL
    private var mData = ArrayList<Seat>()
    private var mLimit: Int = -1
    private var mAutoLimit: Int = -1

    private var itemPadding: Int = 5.dp
    var itemMargin: Rect = Rect(3.dp, 0, 3.dp, 0)

    var stateChange: ((isShowAutoView: Boolean) -> Unit)? = null
    var autoClick: ((count: Int) -> Unit)? = null
    var itemClick: ((seat: Seat) -> Unit)? = null
    var clickDelete: ((seat: Seat) -> Unit)? = null

    /**
     * 总价（分）
     */
    val totalPrice: Int
        get() {
            var tempPrice = 0
            mData.forEach {
                tempPrice += it.showPrice
            }
            return tempPrice
        }

    /**
     * 总服务费（分）
     */
    val totalServiceFee: Int
        get() {
            var tempPrice = 0
            mData.forEach {
                tempPrice += it.serviceFee
            }
            return tempPrice
        }

    /**
     * 服务费（分）
     */
    val serviceFee: Int
        get() {
            return mData.firstOrNull()?.serviceFee.orZero()
        }

    /**
     * 票数
     */
    val ticketCount: Int
        get() = mData.size

    /**
     * 座位ids
     */
    val seatIds: String
        get() {
            val sb = StringBuilder()
            mData.forEach {
                if (sb.isNotEmpty()) {
                    sb.append(",")
                }
                sb.append(it.id)
            }
            return sb.toString()
        }

    /**
     * 座位图名称列表
     */
    val seatNames: String
        get() {
            val sb = StringBuilder()
            mData.forEach {
                if (sb.isNotEmpty()) {
                    sb.append("/")
                }
                sb.append(it.name)
            }
            return sb.toString()
        }

    override fun onMeasure(widthSpec: Int, heightSpec: Int) {
        super.onMeasure(widthSpec, heightSpec)
        val width = measuredWidth
        val columnCount = columnCount
        if (columnCount > 0) {
            mItemWidth = (width - (paddingStart + paddingEnd) - columnCount * (itemMargin.left + itemMargin.right)) / columnCount
//            if (isDataSetChange) {
//                isDataSetChange = false
//                notifyDataSetChange()
//            }
        }
    }

    fun setData(seatManager: SeatManager<Seat, SeatInfo>) {
        setData(
            style = seatManager.getSeatStyle(),
            seats = seatManager.getSelectList(),
            limit = seatManager.getMaxSelectCount(),
            autoLimit = seatManager.getMaxAutoSelectCount(),
        )
    }

    fun setData(
        style: SeatStyle = SeatStyle.NORMAL,
        seats: List<Seat>? = null,
        limit: Int = -1,
        autoLimit: Int = -1
    ) {
        mSeatStyle = style
        mLimit = limit
        mAutoLimit = autoLimit

        fillSeat(seats)
//        isDataSetChange = true
        post {
            notifyDataSetChange()
        }
    }

    private fun fillSeat(seats: List<Seat>?) {
        mData.clear()
        seats?.forEachIndexed { index, seat ->
            if (index < mLimit) {
                mData.add(seat)
            }
        }
    }

    private fun notifyDataSetChange() {
        if (mData.isNotEmpty()) {
            showSeatView()
            stateChange?.invoke(false)
        } else {
            showAutoView()
            stateChange?.invoke(true)
        }
    }

    private fun showSeatView() {
        removeAllViews()
        mData.forEach {
            addView(createItemView(it))
        }
    }

    private fun showAutoView() {
        removeAllViews()
        if (mLimit <= 0 || mAutoLimit <= 0) {
            return
        }
        (1 .. mLimit).forEach {
            if (it <= mAutoLimit) {
                addView(createItemViewAuto(count = it, enable = true))
            } else {
                addView(createItemViewAuto(count = it, enable = false))
            }
        }
    }

    private fun createItemView(seat: Seat): View {
        return SeatSelectItemView(context).apply {
            layoutParams = LayoutParams().apply {
                width = mItemWidth
                height = mItemHeight
                setMargins(itemMargin.left, itemMargin.top, itemMargin.right, itemMargin.bottom)
                setGravity(Gravity.CENTER)
            }
            setData(
                data = seat,
                style = mSeatStyle,
            )
            clickDelete = this@SeatSelectView.clickDelete
        }
    }

    private fun createItemViewAuto(count: Int, enable: Boolean): View {
        return TextView(context).apply {
            layoutParams = LayoutParams().apply {
                width = mItemWidth
                height = mItemHeightAuto
                setMargins(itemMargin.left, itemMargin.top, itemMargin.right, itemMargin.bottom)
                gravity = Gravity.CENTER
            }
            gravity = Gravity.CENTER
            setPadding(itemPadding, 0, itemPadding, 0)
            setBackgroundResource(R.drawable.pic_autoseat_bg)
//            background = getStateListDrawable(
//                normal = getShapeDrawable(
//                    strokeColorRes = R.color.color_9fa4b3,
//                    strokeWidth = 1.dp,
//                    cornerRadius = 5
//                ),
//                pressed = getShapeDrawable(
//                    strokeColorRes = R.color.color_6d707b,
//                    strokeWidth = 1.dp,
//                    cornerRadius = 5
//                ),
//                disable = getShapeDrawable(
//                    colorRes = R.color.color_cfd1d8,
//                    cornerRadius = 5
//                )
//            )
            setTextColor(
                getColorStateList(
                    normalColor = getColor(R.color.color_f97d3f),
                    pressColor = getColor(R.color.color_f97d3f),
                    disableColor = getColor(R.color.color_999999),
                )
            )
            text = "${count}位"
            isEnabled = enable
            setOnClickListener {
                autoClick?.invoke(count)
            }
        }
    }
}