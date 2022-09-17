package com.kotlin.android.app.data.entity.member

import com.kotlin.android.app.data.ProguardRule

/**
 * create by lushan on 2020/12/24
 * description:会员中心-获取抽奖配置列表
 * http://apidocs.mt-dev.com/user-front-api/index.html#api-MemberController-exchangeList
 */
data class MemberExchangeList(
    var list: MutableList<ExchangeBean>? = mutableListOf()
): ProguardRule {
    data class ExchangeBean(
            var appImage: String? = "",//app图
            var configId: Long = 0L,//兑换商品编码
            var exchangeName: String? = "",//兑换商品名称
            var explain: String? = "",//使用说明
            var mtimebPrice: Long = 0L,//时光币兑换单价
    ): ProguardRule
}

