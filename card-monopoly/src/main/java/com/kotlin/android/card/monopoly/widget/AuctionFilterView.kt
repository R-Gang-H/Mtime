package com.kotlin.android.card.monopoly.widget

import android.content.Context
import android.util.AttributeSet
import android.view.LayoutInflater
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import androidx.annotation.Nullable
import androidx.core.content.ContextCompat
import com.kotlin.android.card.monopoly.R
import com.kotlin.android.card.monopoly.constants.Constants
import com.kotlin.android.ktx.ext.click.onClick

/**
 * @desc 拍卖行购买页面筛选tab的view
 * @author zhangjian
 * @date 2020/9/16 10:38
 */
class AuctionFilterView : LinearLayout {

    //剩余时间
    private var llTime: LinearLayout? = null
    private var tvTime: TextView? = null

    //一口价
    private var llFixPrice: LinearLayout? = null
    private var tvFixPrice: TextView? = null


    //当前价
    private var llCurrentPrice: LinearLayout? = null
    private var tvCurrentOrder: TextView? = null

    //剩余时间
    private var ivTime: ImageView? = null

    //一口价
    private var ivFixPrice: ImageView? = null

    //当前价升降图片
    private var ivCurrentPrice: ImageView? = null

    private var currentType = 0
    var onClickFilter: OnClickFilterListener? = null

    var onClick: ((type: Int) -> Unit)? = null

    constructor(context: Context) : this(context, null)

    constructor(context: Context, @Nullable attr: AttributeSet?) : this(context, attr, 0)

    constructor(context: Context, @Nullable attr: AttributeSet?, def: Int) : super(
        context,
        attr,
        def
    ) {
        initView(context)
    }

    private fun initView(context: Context) {
        val view = LayoutInflater.from(context).inflate(R.layout.view_auction_filter, null)
        orientation = HORIZONTAL
        llTime = view.findViewById(R.id.llTime)
        ivTime = view.findViewById(R.id.ivTimeOrder)
        tvTime = view.findViewById(R.id.tvTime)

        llFixPrice = view.findViewById(R.id.llFixPrice)
        ivFixPrice = view.findViewById(R.id.ivFixOrder)
        tvFixPrice = view.findViewById(R.id.tvFixPrice)

        llCurrentPrice = view.findViewById(R.id.llCurrentPrice)
        ivCurrentPrice = view.findViewById(R.id.ivCurrentOrder)
        tvCurrentOrder = view.findViewById(R.id.tvCurrentOrder)
        val param = LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT)
        view.layoutParams = param
        addView(view)

        showState(Constants.ORDER_TIME_ASE)

        llTime?.onClick {
            if (currentType == Constants.ORDER_TIME_ASE) {
                onClickFilter?.clickView(Constants.ORDER_TIME_DES)
                showState(Constants.ORDER_TIME_DES)
            } else {
                onClickFilter?.clickView(Constants.ORDER_TIME_ASE)
                showState(Constants.ORDER_TIME_ASE)
            }
        }

        llFixPrice?.onClick {
            if (currentType == Constants.ORDER_PRICE_ASE) {
                onClickFilter?.clickView(Constants.ORDER_PRICE_DES)
                showState(Constants.ORDER_PRICE_DES)
            } else {
                onClickFilter?.clickView(Constants.ORDER_PRICE_ASE)
                showState(Constants.ORDER_PRICE_ASE)
            }
        }

        llCurrentPrice?.onClick {
            if (currentType == Constants.ORDER_CURRENT_ASE) {
                onClickFilter?.clickView(Constants.ORDER_CURRENT_DES)
                showState(Constants.ORDER_CURRENT_DES)
            } else {
                onClickFilter?.clickView(Constants.ORDER_CURRENT_ASE)
                showState(Constants.ORDER_CURRENT_ASE)
            }
        }

    }

    //展示状态值
    private fun showState(type: Int) {
        currentType = type
        when (type) {
            Constants.ORDER_TIME_DES -> {
                ivTime?.setImageResource(R.drawable.ic_order_des)
                ivFixPrice?.setImageResource(R.drawable.ic_order_dis)
                ivCurrentPrice?.setImageResource(R.drawable.ic_order_dis)
                setTextColors(tvTime, tvFixPrice, tvCurrentOrder)
            }
            Constants.ORDER_TIME_ASE -> {
                ivTime?.setImageResource(R.drawable.ic_order_asc)
                ivFixPrice?.setImageResource(R.drawable.ic_order_dis)
                ivCurrentPrice?.setImageResource(R.drawable.ic_order_dis)
                setTextColors(tvTime, tvFixPrice, tvCurrentOrder)
            }
            Constants.ORDER_PRICE_ASE -> {
                ivTime?.setImageResource(R.drawable.ic_order_dis)
                ivFixPrice?.setImageResource(R.drawable.ic_order_asc)
                ivCurrentPrice?.setImageResource(R.drawable.ic_order_dis)
                setTextColors(tvFixPrice, tvTime, tvCurrentOrder)
            }
            Constants.ORDER_PRICE_DES -> {
                ivTime?.setImageResource(R.drawable.ic_order_dis)
                ivFixPrice?.setImageResource(R.drawable.ic_order_des)
                ivCurrentPrice?.setImageResource(R.drawable.ic_order_dis)
                setTextColors(tvFixPrice, tvTime, tvCurrentOrder)
            }
            Constants.ORDER_CURRENT_ASE -> {
                ivTime?.setImageResource(R.drawable.ic_order_dis)
                ivFixPrice?.setImageResource(R.drawable.ic_order_dis)
                ivCurrentPrice?.setImageResource(R.drawable.ic_order_asc)
                setTextColors(tvCurrentOrder, tvFixPrice, tvTime)
            }
            Constants.ORDER_CURRENT_DES -> {
                ivTime?.setImageResource(R.drawable.ic_order_dis)
                ivFixPrice?.setImageResource(R.drawable.ic_order_dis)
                ivCurrentPrice?.setImageResource(R.drawable.ic_order_des)
                setTextColors(tvCurrentOrder, tvFixPrice, tvTime)
            }
        }
    }


    private fun setTextColors(tv1: TextView?, tv2: TextView?, tv3: TextView?) {
        tv1?.setTextColor(ContextCompat.getColor(context, R.color.color_feb12a))
        tv2?.setTextColor(ContextCompat.getColor(context, R.color.color_4e5e73))
        tv3?.setTextColor(ContextCompat.getColor(context, R.color.color_4e5e73))
    }


    /**
     * 给3个textview赋值
     */
    fun setTextValue(start: String, middle: String, end: String) {
        tvTime?.text = start
        tvFixPrice?.text = middle
        tvCurrentOrder?.text = end
    }

    interface OnClickFilterListener {
        fun clickView(orderType: Int)
    }


}