package com.kotlin.android.app.data.entity

/**
 *
 * Created on 2017/8/16.
 *
 * @author o.s
 */
data class SeatBanner(var bizCode: Int = 0,
                      var bizMsg: String = "",
                      var objects: List<Objects>) {
    data class Objects(var currentTime: Long = 0L,
                       var imgUrl: String = "",
                       var identifier: String = "",
                       var showTime: Long = 0L,
                       var startTime: Long = 0L)
}