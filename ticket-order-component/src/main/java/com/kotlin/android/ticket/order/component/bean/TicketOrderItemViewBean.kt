package com.kotlin.android.ticket.order.component.bean

import android.text.TextUtils
import com.kotlin.android.app.data.ProguardRule
import com.kotlin.android.mtime.ktx.PriceUtils
import com.kotlin.android.mtime.ktx.getString
import com.kotlin.android.ticket.order.component.*

/**
 * create by lushan on 2020/9/16
 * description: 影票订单列表viewBean
 */
data class TicketOrderItemViewBean(
//订单相关
        var orderId: Long = 0L,//订单id
        var orderStatus: Long = 0L,//订单状态
        var totalPrice: Long = 0L,//金额
        var showTime: String = "",//开场时间
        var hall: String = "",//影厅名称
        var seatList: MutableList<String> = mutableListOf(),//影厅座位
        var orderEndTime: Long = 0L,//待支付订单使用订单支付结束时间，用于计算剩余时间
//        影片相关
        var movieId: Long = 0L,//影片id
        var movieName: String = "",//影片名称
        var moviePic: String = "",//影片海报
        var rating: String = "",//我的评分

//  影院相关
        var cinemaId: Long = 0L,
        var cinemaName: String = ""//影院名称
) : ProguardRule {


    /**
     * 是否显示订单评价按钮
     * 订单完成且没有评价过才显示评分按钮
     */
    fun isShowGotoRatingBtn(): Boolean {
        return orderStatus == TICKET_ORDER_COMPLETE && TextUtils.isEmpty(rating)
    }

    /**
     * 获取座位信息
     */
    fun getSeatContent(): String {
        return buildString {
            seatList.forEachIndexed { index, s ->
                append("$hall  $s |${if (index % 2 != 0) "" else " "}")
                if (index % 2 != 0 && index != seatList.size - 1) {
                    append("\n")
                }
            }

        }
    }

    /**
     * 获取订单列表中显示的评分文案
     */
    fun getRatingFormat(): String {
        return if (TextUtils.isEmpty(rating)) {
            ""
        } else {
            getString(R.string.ticket_order_rating_format).format(rating)
        }
    }

    /**
     * 获取订单状态文案
     */
    fun getOrderStatusContent(): String {
        return when (orderStatus) {
            TICKET_ORDER_COMPLETE -> getString(R.string.ticket_order_complete)//已完成
            TICKET_ORDER_CANCEL -> getString(R.string.ticket_order_cancel)//已取消
            TICKET_ORDER_NO_PAY -> getString(R.string.ticket_order_no_pay)//待支付
            TICKET_ORDER_REFUNDED -> getString(R.string.ticket_order_refunded)//已退款
            TICKET_ORDER_REFUNDING -> getString(R.string.ticket_order_refunding)//退款中
            else -> ""
        }
    }

    /**
     * 订单是否是未支付
     */
    fun isNoPay() = orderStatus == TICKET_ORDER_NO_PAY

    /**
     * 获取显示金额
     */
    fun getTotalPriceFormat():String{
        return getString(R.string.ticket_order_total_price_format).format(PriceUtils.formatPriceFenToYuan(totalPrice))
    }
    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as TicketOrderItemViewBean

        if (orderId != other.orderId) return false
        if (orderStatus != other.orderStatus) return false
        if (totalPrice != other.totalPrice) return false
        if (showTime != other.showTime) return false
        if (hall != other.hall) return false
        if (seatList != other.seatList) return false
        if (orderEndTime != other.orderEndTime) return false
        if (movieId != other.movieId) return false
        if (movieName != other.movieName) return false
        if (moviePic != other.moviePic) return false
        if (rating != other.rating) return false
        if (cinemaId != other.cinemaId) return false
        if (cinemaName != other.cinemaName) return false

        return true
    }

    override fun hashCode(): Int {
        var result = orderId.hashCode()
        result = 31 * result + orderStatus.hashCode()
        result = 31 * result + totalPrice.hashCode()
        result = 31 * result + showTime.hashCode()
        result = 31 * result + hall.hashCode()
        result = 31 * result + seatList.hashCode()
        result = 31 * result + orderEndTime.hashCode()
        result = 31 * result + movieId.hashCode()
        result = 31 * result + movieName.hashCode()
        result = 31 * result + moviePic.hashCode()
        result = 31 * result + rating.hashCode()
        result = 31 * result + cinemaId.hashCode()
        result = 31 * result + cinemaName.hashCode()
        return result
    }
}