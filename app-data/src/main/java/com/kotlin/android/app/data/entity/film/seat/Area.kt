package com.kotlin.android.app.data.entity.film.seat

import com.kotlin.android.app.data.ProguardRule

/**
 * 分区信息
 *
 * Created on 2022/2/8.
 *
 * @author o.s
 */
data class Area(
    var areaCode: String? = null, // 座区编码
    var areaName: String? = null, // 座区名称
    var areaPrice: AreaPrice? = null, // 分区价格信息
    var seatList: List<Seat>? = null, // 分区座位列表
) : ProguardRule