package com.kotlin.android.app.data.entity.mine

import com.kotlin.android.app.data.ProguardRule

data class CouponList(
        val msg: String,
        val status: Long,
        val success: Boolean,
        val voucherList: List<Voucher>?
) : ProguardRule {
    data class Voucher(
            val description: String = "",
            val endTime: Long = 0L,
            val id: Long = 0L,
            val movieId: Long = 0L,
            val movieImg: String = "",
            val name: String = "",
            val orderId: Long = 0L,
            val startTime: Long = 0L,
            val status: String = "",
            val useTime: Long = 0L
    )
}

