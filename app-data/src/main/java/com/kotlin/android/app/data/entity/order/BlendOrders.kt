package com.kotlin.android.app.data.entity.order

import com.kotlin.android.app.data.ProguardRule

/**
 * create by lushan on 2020/10/13
 * description: 根据token查询用户三个月内的订单列表
 */
data class BlendOrders(
    var more: Boolean = false,//是否有三个月前的订单
    var orders: MutableList<Order>? = mutableListOf(),//三个月内的订单
    var totalCount: Long = 0L//总数量
): ProguardRule {
    data class Order(
            var commentStatus: Long = 0L,////0不显示评论，1未评，2已评
            var createTime: Long = 0L,//1361880105,//创建时间。 后产品使用（按单位：秒 解析）
            var deductedAmount: Long = 0L,//0,// 抵扣金额 后产品使用
            var description: String? = "",//"电影票：少年派的奇幻漂流少年派的奇幻漂流少年派的奇幻漂流，2张",//订单描述 。后产品使用 商品标题
            var directSalesFlag: Long = 0L,///是否是第三方直销订单 0：时光 1：第三方
            var dsOrderNo: String? = "",//第三方直销平台的订单号。原有的OrderId字段是数字型，为了兼容性，采用String型
            var dsPlatformId: String? = "",//第三方直销平台的ID
            var dsPlatformLogo: String? = "",//第三方直销平台的LOGO！（应该是一个图片链接，这里只显示万达logo标识，时光网的标识是客户端取自本地 ）
            var dsPlatformName: String? = "",//"万达直销", //第三方直销平台的name
            var dsWithGoods: Long = 0L,//第三方订单中是否含有卖品 0：没有 1：有
            var goodsImageUrl: String? = "",//商品图片
            var mobile: String? = "",//手机号
            var orderId: Long = 0L,//20088470, //订单Id，后产品使用
            var orderStatus: Long = 0L,//订单状态
            var orderType: Long = 0L,//1,// 1表示在线选座、2表示电子券、94表示后产品订单 ,使用枚举RelatedObjectType.ECommerceGoodsOrder
            var payEndTime: Long = 0L,//1361880970,// 支付结束时间（多少秒）。 后产品使用
            var payStatus: Long = 0L,//支付状态，0未支付 1已支付
            var rating: Float = 0.0f,//评分
            var reSelectSeat: Boolean = false,//是否需要重新选座
            var reSelectSeatEndTime: Long = 0L,////重新选座截止时间（单位：秒）
            var refundStatus: Long = 0L,//退款状态0-未退款，1-申请退款，2-已退款
            var salesAmount: Long = 0L,//11400,// 销售总价，单位：分。后产品使用
            var serialNo: String? = "",//"11111111" //第三方订单流水号 。订单详情页面需要传入此流水号字段
            var showTime: Long = 0L,//放映时间，Unix时间戳，秒单位 （展示该片上映的日期，周几，具体时间；如果需要加展示用字段，请APP同学单独联系）。非直销订单也增加此字段 2018/6/6特别注释：由于历史原因，时光订单的时候，返回的时间戳不是真正的Unix时间戳，而是已经把时区影响的8小时考虑进去；这个时间戳，直接扔到https://unixtime.51240.com/转换是差8小时的； 第三方订单，也将错就错，采用原有的时间戳生成函数。
            var ticketCount: Long = 0L,//数量
            var ticketName: String? = ""//电影票：少年派的奇幻漂流少年派的奇幻漂流少年派的奇幻漂流",//订单描述名称 new!!!!!（update:去掉"电影票："几个字符串）
    ): ProguardRule
}

