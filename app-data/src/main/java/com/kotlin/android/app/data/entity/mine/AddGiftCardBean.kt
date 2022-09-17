package com.kotlin.android.app.data.entity.mine

data class AddGiftCardBean(
    val cardInfo: CardInfo,
    val codeId: String,
    val codeUrl: String,
    val msg: String,
    val status: Int,
    val success: Boolean,
    val token: String,
    val usedOrder: Boolean
)

data class CardInfo(
    val balance: Int,
    val balancePoint: String,
    val cId: Int,
    val cName: String,
    val cNum: String,
    val cType: Int,
    val endTime: Int,
    val startTime: Int
)