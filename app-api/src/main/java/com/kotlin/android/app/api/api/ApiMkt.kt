package com.kotlin.android.app.api.api

import com.kotlin.android.api.ApiResponse
import retrofit2.http.GET
import retrofit2.http.Query

/**
 * mkt服务接口规范
 *
 * Created on 2020/5/13.
 *
 * @author o.s
 */
interface ApiMkt {

    /**
     * mkt服务接口的path集
     */
    object Path {
        const val ACTIVITY_REMINDERS = "/mkt/activity/reminders.api" // 获取适用活动提示信息列表
        const val ACTIVITY_COUPONS = "/mkt/activity/ncoupons.api" // 获取优惠券列表
        const val ACTIVITY_GOOD_COUPONS = "/mkt/activity/ngoodscouponslist.api" // 获取卖品券列表
        const val ACTIVITY_GOOD_COUPON_CHOOSE = "/mkt/activity/goodscouponchoose.api" // 卖品代金券选择确认
        const val ACTIVITY_LIST = "/mkt/activity/list.api" // 获取活动列表
        const val ACTIVITY_COUPON_USE = "/mkt/activity/conponuse.api" // 优惠券使用确认
        const val ACTIVITY_SELECT_COUPON = "/mkt/activity/selectcoupon.api" // 优惠券选择
        const val ACTIVITY_COMMEND = "/mkt/activity/commend.api" // 推荐活动列表
        const val ACTIVITY_BUY_CARD = "/mkt/activity/buy_card.api" // 购卡
    }

    /**
     * 获取适用活动提示信息列表
     * GET ("/mkt/activity/reminders.api")
     *
     * dids String  必填  mx场次ids(逗号分隔)
     */
    @GET(Path.ACTIVITY_REMINDERS)
    suspend fun getActivityReminders(
        @Query("dids") dIds: String,
        @Query("json") json: Boolean = true
    ): ApiResponse<*>

    /**
     * 获取优惠券列表
     * GET ("/mkt/activity/ncoupons.api")
     *
     * able         boolean 非必填   是否可用, true返回可用列表, false返回不可用列表,默认true
     * did	        string	必填	    场次ID
     * partition	string	必填	    分区号和座位号(格式:分区1-座位1,座位2|分区2-座位3)
     */
    @GET(Path.ACTIVITY_COUPONS)
    suspend fun getActivityCoupons(
        @Query("did") dId: String,
        @Query("partition") partition: String,
        @Query("able") able: Boolean = true,
        @Query("json") json: Boolean = true
    ): ApiResponse<*>

    /**
     * 获取卖品券列表
     * GET ("/mkt/activity/ngoodscouponslist.api")
     *
     * cinemaId	    int	    必填	    mx影院id
     * goodInfo	    string	必填	    卖品信息(售卖键1-售卖数量-补差项id1,补差项id2|售卖键2-售卖数量-补差项id3)
     * able	        bool	非必填	是否可用(不填默认true可用)
     */
    @GET(Path.ACTIVITY_GOOD_COUPONS)
    suspend fun getActivityGoodCoupons(
        @Query("cinemaId") cinemaId: String,
        @Query("goodInfo") goodInfo: String,
        @Query("able") able: Boolean = true,
        @Query("json") json: Boolean = true
    ): ApiResponse<*>

    /**
     * 卖品代金券选择确认
     * GET ("/mkt/activity/goodscouponchoose.api")
     *
     * cinemaId	    int	    必填	    mx影院id
     * goodInfo 	string	必填	    卖品信息(售卖键1-售卖数量-补差项id1,补差项id2|售卖键2-售卖数量-补差项id3)
     * codes	    string	非必填	选用券codes(逗号分隔)
     */
    @GET(Path.ACTIVITY_GOOD_COUPON_CHOOSE)
    suspend fun getActivityGoodCouponChoose(
        @Query("cinemaId") cinemaId: String,
        @Query("goodInfo") goodInfo: String,
        @Query("codes") codes: String,
        @Query("json") json: Boolean = true
    ): ApiResponse<*>

    /**
     * 获取活动列表
     * GET ("/mkt/activity/list.api")
     *
     * orderId	    long	    必填	    订单ID
     * did	        string	    必填	    场次ID
     * partition	string	    必填	    分区号和座位号(格式:分区1-座位1,座位2|分区2-座位3)\
     * able         boolean     非必填   是否可用, true返回可用列表, false返回不可用列表,默认true
     */
    @GET(Path.ACTIVITY_LIST)
    suspend fun getActivityList(
        @Query("orderId") orderId: String,
        @Query("did") dId: String,
        @Query("partition") partition: String,
        @Query("able") able: Boolean = true,
        @Query("json") json: Boolean = true
    ): ApiResponse<*>

    /**
     * 优惠券使用确认
     * GET ("/mkt/activity/conponuse.api")
     *
     * orderId	    long	    必填	    订单ID
     * did	        string	    必填	    场次ID
     * partition	string	    必填	    分区号和座位号(格式:分区1-座位1,座位2|分区2-座位3)
     * allotseat	string	    非必填	券-座位分摊信息json
     */
    @GET(Path.ACTIVITY_COUPON_USE)
    suspend fun getActivityCouponUse(
        @Query("orderId") orderId: String,
        @Query("did") dId: String,
        @Query("partition") partition: String,
        @Query("allotseat") allotSeat: String,
        @Query("json") json: Boolean = true
    ): ApiResponse<*>

    /**
     * 优惠券选择
     * GET ("/mkt/activity/selectcoupon.api")
     *
     * did	        string	    必填	    场次ID
     * partition	string	    必填	    分区号和座位号(格式:分区1-座位1,座位2|分区2-座位3)
     * coupons	    string	    非必填	用户选用券codes(券1,券2,券3)
     */
    @GET(Path.ACTIVITY_SELECT_COUPON)
    suspend fun getActivitySelectCoupon(
        @Query("did") dId: String,
        @Query("partition") partition: String,
        @Query("coupons") coupons: String,
        @Query("json") json: Boolean = true
    ): ApiResponse<*>

    /**
     * 推荐活动列表
     * GET ("/mkt/activity/commend.api")
     *
     * did	        string	    必填	    场次ID
     */
    @GET(Path.ACTIVITY_COMMEND)
    suspend fun getActivityCommend(
        @Query("did") dId: String,
        @Query("json") json: Boolean = true
    ): ApiResponse<*>

    /**
     * 购卡
     * GET ("/mkt/activity/buy_card.api")
     *
     * orderId	    long	    必填	    订单ID
     * did	        string	    必填	    场次ID
     * partition	string	    必填	    分区号和座位号(格式:分区1-座位1,座位2|分区2-座位3)
     */
    @GET(Path.ACTIVITY_BUY_CARD)
    suspend fun getActivityBuyCard(
        @Query("orderId") orderId: String,
        @Query("did") dId: String,
        @Query("partition") partition: String,
        @Query("json") json: Boolean = true
    ): ApiResponse<*>

}