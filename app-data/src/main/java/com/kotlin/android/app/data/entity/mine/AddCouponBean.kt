package com.kotlin.android.app.data.entity.mine

data class AddCoupoBean(
    val error: String,
    val status: Int,
    val success: Boolean,
    val vcodeId: String,
    val vcodeUrl: String,
    val voucherId: String
)