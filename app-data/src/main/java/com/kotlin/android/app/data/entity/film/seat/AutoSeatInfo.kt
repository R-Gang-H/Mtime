package com.kotlin.android.app.data.entity.film.seat

/**
 * 自动选座接口数据
 *
 * Created on 2022/5/16.
 *
 * @author o.s
 */
data class AutoSeatInfo(
    var bizCode: Long = 0L,
    var msg: String? = null,
    var seatColl: ArrayList<Seat>? = null,
    var autoSeatIds: ArrayList<String>? = null,
)
