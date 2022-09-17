package com.kotlin.android.app.data.entity.film.seat

import com.kotlin.android.app.data.ProguardRule

/**
 * 分区价格
 *
 * Created on 2022/2/8.
 *
 * @author o.s
 */
data class AreaPrice(
    var salesPrice: Int? = null, // 销售价：票款 + 手续费
    var ticketPrice: Int? = null, // 票款： APP展示的价格，对应的后台价格是：合同结算价+外部服务费+外部手续费
    var totalFee: Int? = null, // 手续费：APP 展示的价格，对应的后台价格是：终端服务费+时光手续费
) : ProguardRule