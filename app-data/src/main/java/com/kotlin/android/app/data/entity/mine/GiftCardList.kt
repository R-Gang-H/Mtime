package com.kotlin.android.app.data.entity.mine

import com.kotlin.android.app.data.ProguardRule

/**
 * 礼品卡
 * @date 2020.10.25
 */
data class GiftCardList(
        val msg: String? = "",
        val memberList: List<Member>?,
        val statusCode: Long = 0
) : ProguardRule {
    data class Member(
            val cId: Long = 0L,//电影卡id

            val cName: String? = "",//电影卡名称

            val cNum: String? = "",//电影卡卡号

            val cType: Long = 0L,//电影卡类型 0-次卡，1-点卡，2-会员卡次卡，3-会员卡点卡

            val balance: Long = 0L,//余额，对次卡是次数，点卡是点数

            val balancePoint: String? = "",//点数余额

            val startTime: Long = 0L,//开始日期 时间戳 单位秒

            val endTime: Long = 0L//失效日期 时间戳 单位秒
    ) : ProguardRule
}


