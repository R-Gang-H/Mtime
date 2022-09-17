package com.kotlin.android.ticket.order.component

/**
 * create by lushan on 2020/9/16
 * description: 订单状态
 */
//本地使用，暂时可以舍弃
const val TICKET_ORDER_COMPLETE = 1L//订单完成
const val TICKET_ORDER_CANCEL = 2L//订单取消
const val TICKET_ORDER_NO_PAY = 3L//订单未支付
const val TICKET_ORDER_REFUNDED = 4L//已退款
const val TICKET_ORDER_REFUNDING = 5L//退款中


const val TICKET_ORDER_NEW_CREATE = 0L//新建，此订单对用户不可见
const val TICKET_ORDER_CREATE_SUCCESS = 10L   //10 创建成功(调用FinishOrder时所有子订单创建成功，此后主订单再不允许添加子订单)
const val TICKET_ORDER_CREATE_FAILED = 20L   //20 创建失败(调用了FinishOrder时部分或全部子订单创建失败，此后主订单再不允许添加子订单)
const val TICKET_ORDER_COMPETE = 30L//成功(已支付，且所有子订单都成功)
const val TICKET_ORDER_FAIL = 40L//失败(已支付，但部分或全部子订单失败)
const val TICKET_ORDER_USER_CANCEL =100L//已取消(用户在支付前主动取消)

const val TICKET_REFUND_UNDO = 0L//未退款
const val TICKET_REFUNDING = 1L//申请退款
const val TICKET_REFUNDED = 2L//已退款

