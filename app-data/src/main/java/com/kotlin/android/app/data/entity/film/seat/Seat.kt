package com.kotlin.android.app.data.entity.film.seat

import com.kotlin.android.app.data.ProguardRule

/**
 * 座位
 *
 * Created on 2022/2/8.
 *
 * @author o.s
 */
data class Seat(
    var id: String = "",
    var x: Int = 0, // 座位横坐标
    var y: Int = 0, // 座位纵坐标
    var type: Int? = null, // 0普通座;1残疾人座;2情侣左座;3情侣右座
    var name: String? = null, // 座位名称
    var seatNumber: String? = null, // 座位号
    var rowName: String? = null, // 行号
    var status: Boolean = false, // 座位状态（可选和不可选）

    var showPrice: Int = 0, // 本地座区票价
    var serviceFee: Int = 0, // 本地座区服务费

    var areaId: String,
    var areaLevel: Int = 1,
    var selected: Boolean = false, // 自己选中

    // 自动选座返回
    var seatId: String = "", // 座位号
    var enable: Boolean = false, // 是否可选，true表示可选
) : ProguardRule {

    /**
     * 本地状态
     */
    val localStatus: Int
        get() = if (status) {
            if (selected) {
                3 // 自己选中（3）
            } else {
                1 // 可选（1）
            }
        } else {
            2 // 不可选（2）已售
        }

    fun isSame(seat: Seat): Boolean {
        return seat.x == x && seat.y == y
    }
}