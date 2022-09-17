//package com.kotlin.android.app.router.provider.main
//
//import com.kotlin.android.app.router.path.RouterProviderPath
//import com.kotlin.android.router.annotation.RouteProvider
//import com.kotlin.android.router.provider.IBaseProvider
//
//
///**
// * 老框架主工程路由跳转（Kotlin实现）
// *
// * Created on 2022/5/15.
// *
// * @author o.s
// */
//@RouteProvider(path = RouterProviderPath.Provider.PROVIDER_MAIN_KT)
//interface IMainKtProvider : IBaseProvider {
//
//    /**
//     * 订单确认页
//     */
//    fun startOrderConfirmActivity(
//        seatIds: String,
//        seatCount: Int,
//        totalPrice: Double,
//        serviceFee: Double,
//        buyPhone: String? = null,
//        movieName: String? = null,
//        cinemaName: String? = null,
//        ticketDateInfo: String? = null,
//        seatSelectedInfo: String? = null,
//        orderId: String? = null,
//        subOrderId: String? = null,
//        isFromSeatActivity: Boolean = false,
//        // 以下4个数据需要带到支付页，当需要从支付页重新返回选座页时要带回来（更换场次要用到）
//        dId: String? = null,
//        movieId: String? = null,
//        cinemaId: String? = null,
//        showtimeDate: String? = null,
//        // 在确认订单页面显示 4个
//        ticketTime: String? = null,
//        ticketHallName: String? = null,
//        ticketVersion: String? = null,
//        ticketLanguage: String? = null,
//        // noVip?
//    )
//
//    /**
//     * 订单支付页
//     */
//    fun startOrderPayActivity(
//        dId: String,
//        orderId: String? = null,
//        subOrderId: String? = null,
//        isETicket: Boolean = false,
//        isFromSeatActivity: Boolean = false,
//        totalPrice: Double,
//        serviceFee: Double,
//        payEndTime: Long,
//        buyPhone: String? = null,
//        seatIds: String,
//        seatCount: Int,
//        ticketDateInfo: String? = null,
//        seatSelectedInfo: String? = null,
//    )
//}