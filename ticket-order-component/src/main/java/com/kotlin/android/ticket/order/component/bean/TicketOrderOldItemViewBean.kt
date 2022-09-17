package com.kotlin.android.ticket.order.component.bean

import com.kotlin.android.app.data.ProguardRule
import com.kotlin.android.mtime.ktx.PriceUtils
import com.kotlin.android.mtime.ktx.getColor
import com.kotlin.android.mtime.ktx.getServerTime
import com.kotlin.android.mtime.ktx.getString
import com.kotlin.android.ticket.order.component.*

/**
 * create by lushan on 2020/10/16
 * description:订单列表老需求实体
 */
data class TicketOrderOldItemViewBean(
        var orderId: Long = 0L,//订单id
        var dsPlatformLogo: String = "",//营销平台logo
        var dsPlatformName: String = "",//营销平台名称
        var orderStatus: Long = 0L,//订单状态
        var isNotPay: Boolean = false,//是否已支付
        var orderTitle: String = "",//订单内容描述
        var orderTimeInfo: String = "",//订单开场时间
        var ticketCount: Long = 0L,//张数
        var totalPrice: Long = 0L,//总价格
        var dsWithGoods: Boolean = false,//是否还有卖品
        var payEndTime: Long = 0L,//支付结束时间(多少秒)
        var refundStatus: Long = 0L,//退款状态0-未退款，1-申请退款，2-已退款
        var reSelectSeat: Boolean = false,//是否需要重新选座
        var reSelectSeatEndTime: Long = 0L//1361880105,//创建时间。 后产品使用（按单位：秒 解析）
) : ProguardRule {


    /**
     * 获取显示金额
     */
    fun getTotalPriceFormat(): String {
        return getString(R.string.ticket_order_total_price_format).format(PriceUtils.formatPriceFenToYuan(totalPrice))
    }

    /**
     * 获取总票数
     */
    fun getTotalTicketNum(): String = getString(R.string.ticket_order_ticket_num_format).format(ticketCount)


    fun getOrderStatusColor(): Int {
        var color = getColor(R.color.color_f97d3f)

        if (notPay()) {
            color = getColor(R.color.color_f97d3f)
        }

        if (orderStatus == TICKET_ORDER_COMPETE) {
            color = getColor(R.color.color_a3a3a3)//交易成功
        } else if (orderStatus == TICKET_ORDER_FAIL) {
            color = getColor(R.color.color_f15353)//出票失败
        }
        if (refundStatus == TICKET_REFUNDING) {
            color = getColor(R.color.color_f97d3f)//退款中
        } else if (refundStatus == TICKET_REFUNDED) {
            color = getColor(R.color.color_a3a3a3)//已退款
        }
        return color
    }

    /**
     * 订单状态文案
     *  按照订单状态获取文案
     */
    fun getOrderStatusContent(): String {
        var content = ""
        if (notPay()) {
            content = getString(R.string.ticket_order_no_pay)
        }

        if (orderStatus == TICKET_ORDER_COMPETE) {
            content = getString(R.string.ticket_order_complete)//交易成功
        } else if (orderStatus == TICKET_ORDER_FAIL) {
            content = getString(R.string.ticket_order_ticketing_failed)//出票失败
        }

        if (refundStatus == TICKET_REFUNDING) {
            content = getString(R.string.ticket_order_refunding)//退款中
        } else if (refundStatus == TICKET_REFUNDED) {
            content = getString(R.string.ticket_order_refunded)//已退款
        }

        return content
    }

    fun getPayBtnContent(): String {
        return if (notPay() && reSelectSeat) {
            getString(R.string.ticket_order_component_reselect_seat)
        } else {
            getString(R.string.ticket_order_to_pay)
        }
    }


    fun notPay(): Boolean {
        return orderStatus != TICKET_ORDER_COMPETE && orderStatus != TICKET_ORDER_FAIL && (getServerTime() < payEndTime  || (reSelectSeat && getServerTime() < reSelectSeatEndTime))
    }

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as TicketOrderOldItemViewBean

        if (orderId != other.orderId) return false
        if (dsPlatformLogo != other.dsPlatformLogo) return false
        if (dsPlatformName != other.dsPlatformName) return false
        if (orderStatus != other.orderStatus) return false
        if (isNotPay != other.isNotPay) return false
        if (orderTitle != other.orderTitle) return false
        if (orderTimeInfo != other.orderTimeInfo) return false
        if (ticketCount != other.ticketCount) return false
        if (totalPrice != other.totalPrice) return false
        if (dsWithGoods != other.dsWithGoods) return false
        if (payEndTime != other.payEndTime) return false
        if (refundStatus != other.refundStatus) return false
        if (reSelectSeat != other.reSelectSeat) return false
        if (reSelectSeatEndTime != other.reSelectSeatEndTime) return false

        return true
    }

    override fun hashCode(): Int {
        var result = orderId.hashCode()
        result = 31 * result + dsPlatformLogo.hashCode()
        result = 31 * result + dsPlatformName.hashCode()
        result = 31 * result + orderStatus.hashCode()
        result = 31 * result + isNotPay.hashCode()
        result = 31 * result + orderTitle.hashCode()
        result = 31 * result + orderTimeInfo.hashCode()
        result = 31 * result + ticketCount.hashCode()
        result = 31 * result + totalPrice.hashCode()
        result = 31 * result + dsWithGoods.hashCode()
        result = 31 * result + payEndTime.hashCode()
        result = 31 * result + refundStatus.hashCode()
        result = 31 * result + reSelectSeat.hashCode()
        result = 31 * result + reSelectSeatEndTime.hashCode()
        return result
    }


}