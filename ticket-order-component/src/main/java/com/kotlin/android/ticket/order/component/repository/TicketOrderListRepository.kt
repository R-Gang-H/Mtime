package com.kotlin.android.ticket.order.component.repository

import com.kotlin.android.api.ApiResult
import com.kotlin.android.app.api.base.BaseRepository
import com.kotlin.android.app.data.entity.order.BlendOrders
import com.kotlin.android.ktx.ext.time.TimeExt
import com.kotlin.android.mtime.ktx.getServerTime
import com.kotlin.android.ticket.order.component.TICKET_ORDER_COMPETE
import com.kotlin.android.ticket.order.component.TICKET_ORDER_CREATE_SUCCESS
import com.kotlin.android.ticket.order.component.TICKET_ORDER_FAIL
import com.kotlin.android.ticket.order.component.TICKET_ORDER_NEW_CREATE
import com.kotlin.android.ticket.order.component.bean.ListViewBean
import com.kotlin.android.ticket.order.component.bean.TicketOrderOldItemViewBean
import com.kotlin.android.ticket.order.component.binder.TicketOrderOldBinder
import com.kotlin.android.widget.adapter.multitype.adapter.binder.MultiTypeBinder

/**
 * create by lushan on 2020/9/16
 * description: 电影票订单列表
 */
class TicketOrderListRepository : BaseRepository() {
    private val START_INDEX = 1L
    private var pageIndex = START_INDEX

    /**
     * 获取三个月以内的订单列表
     */
    suspend fun getTicketOrderListDataInThreeMonths(): ApiResult<ListViewBean> {
        return request(
            converter = { blendOrder ->
                getListViewBean(blendOrder, true)
            },
            api = { apiMTime.getOrderInThreeMonth() })
    }

    /**
     * 获取三个月之前的订单列表
     */
    suspend fun getTicketOrderListDataOutThreeMonths(
        isMore: Boolean,
        platformId: Long,
        orderType: String
    ): ApiResult<ListViewBean> {
        if (isMore.not()) {
            pageIndex = START_INDEX
        }
//        val body = arrayMapOf<String, Any>("pageIndex" to pageIndex, "platformId" to platformId, "orderType" to orderType).toRequestBody()
        return request(
            converter = {
                pageIndex++
                getListViewBean(it, false)
            },
            api = { apiMTime.getOrdersOutThreeMonth(orderType, pageIndex, platformId) })
    }

    private fun getListViewBean(blendOrders: BlendOrders, isfilter: Boolean): ListViewBean {
        val listViewBean = ListViewBean(
            false,
            blendOrders.totalCount,
            getOrderList(blendOrders.orders, isfilter),
            blendOrders.more
        )
        return listViewBean
    }

    /**
     * 过滤三个月内订单列表
     */
    private fun filterOrderList(list: MutableList<BlendOrders.Order>?): MutableList<BlendOrders.Order> {
        list ?: return mutableListOf()
        val ticketOrderList = mutableListOf<BlendOrders.Order>()
        val otherOrderList = mutableListOf<BlendOrders.Order>()
        val serverTime = getServerTime()
        list.forEach {
            when (it.orderStatus) {
                TICKET_ORDER_COMPETE -> {//成功状态，直接加入列表中     30
                    ticketOrderList.add(it)
                }
                TICKET_ORDER_CREATE_SUCCESS -> {//10
                    if (serverTime < it.payEndTime) {
                        ticketOrderList.add(it)
                    } else if (it.deductedAmount > 0L) {//如果订单有扣除的余额，说明支付的时候使用了代金券一类的，需要加入列表，让用户退款
                        otherOrderList.add(it)
                    }
                }
                TICKET_ORDER_NEW_CREATE -> {//订单新建 0
                    if (serverTime < it.payEndTime) {//在倒计时时间以内，加入列表
                        ticketOrderList.add(it)
                    }
                }
                TICKET_ORDER_FAIL -> {//已经支付，但是订单失败，加入列表，让用户退款  40
                    otherOrderList.add(it)
                }
                else -> {
                    if (it.reSelectSeat && serverTime < it.reSelectSeatEndTime) {
                        ticketOrderList.add(it)
                    }
                }

            }
        }

        ticketOrderList.addAll(otherOrderList)
        return ticketOrderList
    }

    private fun getOrderList(
        list: MutableList<BlendOrders.Order>?,
        isfilter: Boolean
    ): MutableList<MultiTypeBinder<*>> {
        list ?: return mutableListOf()
        val orderList = if (isfilter) {
            filterOrderList(list)
        } else {
            list
        }
        return orderList.map {
            TicketOrderOldBinder(
                TicketOrderOldItemViewBean(
                    orderId = it.orderId,
                    dsPlatformLogo = it.dsPlatformLogo.orEmpty(),
                    dsPlatformName = it.dsPlatformName.orEmpty(),
                    orderStatus = it.orderStatus,
                    isNotPay = it.payStatus == 0L,
                    orderTitle = it.ticketName.orEmpty(),
                    orderTimeInfo = getShowTimeInfo(it.showTime),
                    ticketCount = it.ticketCount,
                    totalPrice = it.salesAmount + it.deductedAmount,
                    dsWithGoods = it.dsWithGoods == 1L,
                    payEndTime = it.payEndTime,
                    refundStatus = it.refundStatus,
                    reSelectSeat = it.reSelectSeat,
                    reSelectSeatEndTime = it.reSelectSeatEndTime

                )
            )
        }.toMutableList()
    }

    private fun getShowTimeInfo(showTime: Long): String {
        val chineseWeek = TimeExt.getChineseWeek(showTime)
        val dateStr = TimeExt.millis2String(showTime, "yyyy-MM-dd")
        val hourAndMinStr = TimeExt.millis2String(showTime, "HH:mm")
        return "$dateStr (${chineseWeek}) $hourAndMinStr"
    }

}