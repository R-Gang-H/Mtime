package com.kotlin.android.app.data.entity.film.seat

import com.kotlin.android.app.data.ProguardRule

/**
 * 座位图信息
 *
 * Created on 2022/2/8.
 *
 * @author o.s
 */
data class SeatInfo(
    var isSale: Boolean = false, // 是否可售
    var error: String? = null, // 错误提示信息
    var mobile: String? = null, // 手机号
    var isAutoSelected: Boolean = false, // 是否支持自动选座
    var maxAutoSelectCount: Int = 4, // 最大自动选座数，默认为4
    var maxOrderTicketCount: Int = 4, // 最大订单座位数，默认为4
    var cinemaId: Long? = null,
    var cinemaName: String? = null,
    var hallName: String? = null, // 影厅名称
    var hallSpecialDes: String? = null, // 影厅特殊说明
    var movieId: Long? = null,
    var movieName: String? = null,
    var realTime: Long? = null, // 放映时间，标准Unix时间戳，单位毫秒
    var movieLength: Long? = null, // 影片时长 ，单位分
    var versionDesc: String? = null, // 版本
    var language: String? = null, // 语言
    var dateMessage: String? = null, // 给用户的时间提示
    var seatColumnCount: Int? = null, // 影院列数
    var seatRowCount: Int? = null, // 影院排数
    var centerX: Int? = null, // 列中线
    var centerY: Int? = null, // 排中线
    var remainSeat: Int? = null, // 剩余可选座位数
    var orderId: Long? = null, // 未支付订单Id
    var subOrderId: Long? = null, // 未支付子订单Id
    var orderMsg: String? = null, // 未支付提示信息
    var areaList: List<Area>? = null, // 座位图分区列表信息
    var serviceFee: Int? = null,
//    var orderExplains: String? = null, // 订单提示
) : ProguardRule
