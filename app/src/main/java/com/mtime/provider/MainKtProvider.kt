//package com.mtime.provider
//
//import android.os.Bundle
//import com.alibaba.android.arouter.facade.annotation.Route
//import com.kotlin.android.app.router.path.RouterActivityPath
//import com.kotlin.android.app.router.path.RouterProviderPath
//import com.kotlin.android.app.router.provider.main.IMainKtProvider
//import com.kotlin.android.router.RouterManager
//import com.mtime.frame.App
//
///**
// * 老框架主工程路由跳转（Kotlin实现）
// *
// * Created on 2022/5/15.
// *
// * @author o.s
// */
//@Route(path = RouterProviderPath.Provider.PROVIDER_MAIN_KT)
//class MainKtProvider : IMainKtProvider {
//
//    override fun startOrderConfirmActivity(
//        seatIds: String,
//        seatCount: Int,
//        totalPrice: Double,
//        serviceFee: Double,
//        buyPhone: String?,
//        movieName: String?,
//        cinemaName: String?,
//        ticketDateInfo: String?,
//        seatSelectedInfo: String?,
//        orderId: String?,
//        subOrderId: String?,
//        isFromSeatActivity: Boolean,
//        dId: String?,
//        movieId: String?,
//        cinemaId: String?,
//        showtimeDate: String?,
//        ticketTime: String?,
//        ticketHallName: String?,
//        ticketVersion: String?,
//        ticketLanguage: String?
//    ) {
//        Bundle().apply {
//            putString(App.getInstance().KEY_SEATING_SEAT_ID, seatIds)
//            putInt(App.getInstance().KEY_SEATING_SELECTED_SEAT_COUNT, seatCount)
//            putDouble(App.getInstance().KEY_SEATING_TOTAL_PRICE, totalPrice)
//            putDouble(App.getInstance().KEY_SEATING_SERVICE_FEE, serviceFee)
//            putString(App.getInstance().KEY_USER_BUY_TICKET_PHONE, buyPhone)
//            putString(App.getInstance().KEY_MOVIE_NAME, movieName)
//            putString(App.getInstance().KEY_CINEMA_NAME, cinemaName)
//            putString(App.getInstance().KEY_TICKET_DATE_INFO, ticketDateInfo)
//            putString(App.getInstance().KEY_SEAT_SELECTED_INFO, seatSelectedInfo)
//            putString(App.getInstance().KEY_SEATING_SUBORDER_ID, subOrderId)
//            putBoolean(App.getInstance().IS_FROM_SEATSELECT_ACTIVITY, isFromSeatActivity)
//            putString(App.getInstance().KEY_SEATING_DID, dId)
//            putString(App.getInstance().KEY_MOVIE_ID, movieId)
//            putString(App.getInstance().KEY_CINEMA_ID, cinemaId)
//            putString(App.getInstance().KEY_SHOWTIME_DATE, showtimeDate)
//            putString(App.getInstance().KEY_TICKET_TIME_INFO, ticketTime)
//            putString(App.getInstance().KEY_TICKET_HALLNAME_INFO, ticketHallName)
//            putString(App.getInstance().KEY_TICKET_VERSIONDESC_INFO, ticketVersion)
//            putString(App.getInstance().KEY_TICKET_LANGUAGE_INFO, ticketLanguage)
//        }.also {
//            RouterManager.instance.navigation(
//                path = RouterActivityPath.MainKt.PAGER_ORDER_CONFIRM_ACTIVITY,
//                bundle = it
//            )
//        }
//    }
//
//    override fun startOrderPayActivity(
//        dId: String,
//        orderId: String?,
//        subOrderId: String?,
//        isETicket: Boolean,
//        isFromSeatActivity: Boolean,
//        totalPrice: Double,
//        serviceFee: Double,
//        payEndTime: Long,
//        buyPhone: String?,
//        seatIds: String,
//        seatCount: Int,
//        ticketDateInfo: String?,
//        seatSelectedInfo: String?,
//    ) {
//        Bundle().apply {
//            putString(App.getInstance().KEY_SEATING_DID, dId)
//            putString(App.getInstance().KEY_SEATING_ORDER_ID, orderId)
//            putString(App.getInstance().KEY_SEATING_SUBORDER_ID, subOrderId)
//            putBoolean(App.getInstance().PAY_ETICKET, isETicket)
//            putBoolean(App.getInstance().IS_FROM_SEATSELECT_ACTIVITY, isFromSeatActivity)
//            putDouble(App.getInstance().KEY_SEATING_TOTAL_PRICE, totalPrice)
//            putDouble(App.getInstance().KEY_SEATING_SERVICE_FEE, serviceFee)
//            putLong(App.getInstance().KEY_SEATING_PAY_ENDTIME, payEndTime)
//            putString(App.getInstance().KEY_USER_BUY_TICKET_PHONE, buyPhone)
//            putString(App.getInstance().KEY_SEATING_SEAT_ID, seatIds)
//            putInt(App.getInstance().KEY_SEATING_SELECTED_SEAT_COUNT, seatCount)
//            putString(App.getInstance().KEY_TICKET_DATE_INFO, ticketDateInfo)
//            putString(App.getInstance().KEY_SEAT_SELECTED_INFO, seatSelectedInfo)
//        }.also {
//            RouterManager.instance.navigation(
//                path = RouterActivityPath.MainKt.PAGER_ORDER_PAY_ACTIVITY,
//                bundle = it
//            )
//        }
//    }
//}