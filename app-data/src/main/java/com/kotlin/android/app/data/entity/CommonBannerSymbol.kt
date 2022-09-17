package com.kotlin.android.app.data.entity

/**
 *
 * Created on 2020/05/18.
 *
 * @author o.s
 */

/**
 * pageSymbol 页面标志位
 * areaSymbol 区域标志位
 */
enum class CommonBannerSymbol(val pageSymbol: String, val areaSymbol: String) {
    MOVIE("MovieOnsaleCinemaList", "MovieOnsaleCinema_Banner"),
    MY_TAB("MyTab", "MyTab_Banner"),
    MY_TAB_RIGHT("MyTab", "MyTab_VipEquity"),
    SATISFACTION_SURVEY("CustomerServiceCenter", "CustomerServiceCenter_Survey"),
    MALL_GOODS_ORDER_TIP("GoodsSelectPay", "Goods_Notice"),
    SEAT_WARNING_TIP("SelectSeat", "FilmTips"),
}