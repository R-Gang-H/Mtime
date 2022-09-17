package com.kotlin.android.app.api.api

import com.kotlin.android.api.ApiResponse
import com.kotlin.android.api.config.ApiConfig
import retrofit2.http.*

/**
 * ticket服务接口规范
 *
 * Created on 2020/5/13.
 *
 * @author o.s
 */
interface ApiTicket {

    /**
     * ticket服务接口的path集
     */
    object Path {
        const val SHOWTIME_BY_CINEMA_ID = "/showtime/by_cinema.api" //
        const val SHOWTIME_BY_CINEMA_ID_FILM_ID_AND_DATE = "/showtime/by_cinema_film_date.api" // Del
        const val SHOWTIME_BY_LOCATION = "/showtime/location_film_ticket_v6_4.api" //
        const val SEAT_INFO = "/order/real_time_seat.api" //
        const val AUTO_SEAT = "/order/auto_seat.api" //
        const val CREATE_ORDER = "/order/create_order.api" // POST
        const val CANCEL_ORDER = "/order/cancel.api" // POST
        const val SAVE_ORDER = "/order/save_order.api" // POST 保存订单图片
        const val CONFIRM_ORDER = "/order/confirm_order.api" // POST
        const val REFUND_ORDER = "/order/refund_order.api" // POST 退单
        const val ORDER_REFUND_INFO = "/order/query_order_info_for_refund.api" // POST
        const val ORDER_REFUND_DETAIL = "/order/refund_details.api" // POST
        const val ORDER_STATUS = "/order/order_status.api" // POST
        const val ORDER_GO_PAY = "/order/go_pay.api" // POST Del
        const val ORDER_TO_PAY = "/order/to_pay.api" // POST 支付订单-立即支付（V6.3.8）
        const val ORDER_PAY = "/order/release_pay.api" // POST 支付订单-待支付订单释放活动
        const val ORDER_IS_PAY = "/order/is_pay.api" // POST 支付订单-待支付订单是否可支付
        const val QUERY_ORDER_BY_USER_ID = "/order/query_by_userid.api" // POST
        const val QUERY_WAIT_TO_PAY_ORDER_BY_USER_ID = "/order/query_waitforpay_by_userid.api" // POST
        const val QUERY_PAY_INFO = "/order/query_pay_info.api" // POST Del
        const val QUERY_PAY_INFO_V6_3_8 = "/order/query_pay_info_upgrade.api" // POST 支付订单-查询支付信息(V6.3.8)
        const val QUERY_PAY_DEAL_RESULT = "/order/query_pay_deal_result.api" // POST 查询卡券营销活动使用状态(V6.3.8)
        const val QUERY_MOVIE_REMIND_BY_USER_ID = "/order/query_movie_remind_by_userid.api" // POST 查询用户最近购买上映影票信息
        const val QUERY_ORDER_REFUND_STATUS = "/order/query_order_refund_status.api" // POST
        const val SEND_TICKET_MSG = "/order/send_ticket_msg.api" // POST
    }

    /**
     *
     * GET ("/showtime/by_cinema.api")
     */
    @GET(Path.SHOWTIME_BY_CINEMA_ID)
    suspend fun getShowtimeByCinemaId(
        @Query("123") abc: String,
        @Query("json") json: Boolean = true
    ): ApiResponse<*>

    /**
     *
     * GET ()
     */
    @GET(Path.SHOWTIME_BY_LOCATION)
    suspend fun getShowtimeByLocation(
        @Query("123") abc: String,
        @Query("json") json: Boolean = true
    ): ApiResponse<*>

    /**
     *
     * GET ()
     */
    @GET(Path.SEAT_INFO)
    suspend fun getSeatInfo(
        @Query("123") abc: String,
        @Query("json") json: Boolean = true
    ): ApiResponse<*>

    /**
     *
     * GET ()
     */
    @GET(Path.AUTO_SEAT)
    suspend fun getAutoSeat(
        @Query("123") abc: String,
        @Query("json") json: Boolean = true
    ): ApiResponse<*>

    /**
     *
     * POST ()
     */
    @POST(Path.CREATE_ORDER)
    @FormUrlEncoded
    suspend fun getCreateOrder(
        @Field("123") abc: String,
        @Field("json") json: Boolean = true
    ): ApiResponse<*>

    /**
     *
     * POST ()
     */
    @POST(Path.CANCEL_ORDER)
    @FormUrlEncoded
    suspend fun getCancelOrder(
        @Field("123") abc: String,
        @Field("json") json: Boolean = true
    ): ApiResponse<*>

    /**
     * 保存订单图片
     * POST ("/order/save_order.api")
     *
     * orderId  String  必选  订单号
     */
    @POST(Path.SAVE_ORDER)
    @FormUrlEncoded
    suspend fun getSaveOrder(
        @Field("orderId") orderId: String,
        @Field("json") json: Boolean = true
    ): ApiResponse<*>

    /**
     *
     * POST ()
     */
    @POST(Path.CONFIRM_ORDER)
    @FormUrlEncoded
    suspend fun getConfirmOrder(
        @Field("123") abc: String,
        @Field("json") json: Boolean = true
    ): ApiResponse<*>

    /**
     *
     * POST ()
     */
    @POST(Path.REFUND_ORDER)
    @FormUrlEncoded
    suspend fun getRefundOrder(
        @Field("123") abc: String,
        @Field("json") json: Boolean = true
    ): ApiResponse<*>

    /**
     *
     * POST ()
     */
    @POST(Path.ORDER_REFUND_INFO)
    @FormUrlEncoded
    suspend fun getOrderRefund(
        @Field("123") abc: String,
        @Field("json") json: Boolean = true
    ): ApiResponse<*>

    /**
     *
     * POST ()
     */
    @POST(Path.ORDER_REFUND_DETAIL)
    @FormUrlEncoded
    suspend fun getOrderRefundDetail(
        @Field("123") abc: String,
        @Field("json") json: Boolean = true
    ): ApiResponse<*>

    /**
     *
     * POST ()
     */
    @POST(Path.ORDER_STATUS)
    @FormUrlEncoded
    suspend fun getOrderStatus(
        @Field("123") abc: String,
        @Field("json") json: Boolean = true
    ): ApiResponse<*>

    /**
     * 支付订单-立即支付（V6.3.8）
     * POST ("/order/to_pay.api")
     *
     * requestInfo	    String	必选	所有下面的字段都在这个大的json里
     * orderId	        Long	必选	主订单号
     * verifyCode	    String	可选	验证码
     * contextId	    String	可选	短信验证码发送成功后返回contextId
     * goodInfo	        String	可选	卖品信息(售卖键1-售卖数量-补差项id1,补差项id2|售卖键2-售卖数量-补差项id3)
     * activity	        String	可选 活动信息
     * ticketVoucher	String	可选	票券信息
     * snackVoucher	    String	可选	观影套餐优惠信息
     * cardPayment	    String	可选	储值卡支付信息
     * externalPayment	String	可选	第三方支付信息
     * intergral        String	可选	积分支付信息
     * snackStockType	Int	    可选	备货方式 1预约取货  2立即取货
     * snackStockTime	Long	可选	snackStockType 为1时必选  取货预约时间毫秒时间戳
     */
    @POST(Path.ORDER_TO_PAY)
    @FormUrlEncoded
    suspend fun getOrderToPay(
        @Field("requestInfo") requestInfo: String,
        @Field("orderId") orderId: String,
        @Field("verifyCode") verifyCode: String?,
        @Field("contextId") contextId: String?,
        @Field("goodInfo") goodInfo: String?,
        @Field("activity") activity: String?,
        @Field("ticketVoucher") ticketVoucher: String?,
        @Field("snackVoucher") snackVoucher: String?,
        @Field("cardPayment") cardPayment: String?,
        @Field("externalPayment") externalPayment: String?,
        @Field("intergral") integral: String?,
        @Field("snackStockType") snackStockType: Int?,
        @Field("snackStockTime") snackStockTime: Long?,
        @Field("json") json: Boolean = true
    ): ApiResponse<*>

    /**
     * 支付订单-待支付订单释放活动
     * POST ("/order/release_pay.api")
     *
     * orderId	long	必选	主订单号
     */
    @POST(Path.ORDER_PAY)
    @FormUrlEncoded
    suspend fun getOrderPay(
        @Field("orderId") orderId: String,
        @Field("json") json: Boolean = true
    ): ApiResponse<*>

    /**
     * 支付订单-待支付订单是否可支付
     * POST ("/order/is_pay.api")
     *
     * orderId	long	必选	主订单号
     */
    @POST(Path.ORDER_IS_PAY)
    @FormUrlEncoded
    suspend fun getIsPay(
        @Field("orderId") orderId: String,
        @Field("json") json: Boolean = true
    ): ApiResponse<*>

    /**
     *
     * POST ()
     */
    @POST(Path.QUERY_ORDER_BY_USER_ID)
    @FormUrlEncoded
    suspend fun getQueryOrderByUserId(
        @Field("123") abc: String,
        @Field("json") json: Boolean = true
    ): ApiResponse<*>

    /**
     *
     * POST ()
     */
    @POST(Path.QUERY_WAIT_TO_PAY_ORDER_BY_USER_ID)
    @FormUrlEncoded
    suspend fun getQueryWaitToPayOrderByUserId(
        @Field("123") abc: String,
        @Field("json") json: Boolean = true
    ): ApiResponse<*>

    /**
     * 支付订单-查询支付信息(V6.3.8)
     * POST ("/order/query_pay_info_upgrade.api")
     *
     * orderId  long    必填  主订单ID
     * tradeNo  long	必填  流水号
     */
    @POST(Path.QUERY_PAY_INFO_V6_3_8)
    @FormUrlEncoded
    suspend fun getQueryPayInfo(
        @Field("orderId") orderId: String,
        @Field("tradeNo") tradeNo: String,
        @Field("json") json: Boolean = true
    ): ApiResponse<*>

    /**
     * 查询卡券营销活动使用状态(V6.3.8)
     * POST ("/order/query_pay_deal_result.api")
     *
     * orderId  long    必填  主订单ID
     * tradeNo  long	必填  流水号
     */
    @POST(Path.QUERY_PAY_DEAL_RESULT)
    @FormUrlEncoded
    suspend fun getQueryPayDealResult(
        @Field("orderId") orderId: String,
        @Field("tradeNo") tradeNo: String,
        @Field("json") json: Boolean = true
    ): ApiResponse<*>

    /**
     * 查询用户最近购买上映影票信息
     * POST ("/order/query_movie_remind_by_userid.api")
     *
     * remindTime   Long    必选  查询最近上映时间段单位小时（72小时内则传72）
     */
    @POST(Path.QUERY_MOVIE_REMIND_BY_USER_ID)
    @FormUrlEncoded
    suspend fun getQueryMovieRemindByUserId(
        @Field("remindTime") remindTime: String = ApiConfig.REMIND_TIME,
        @Field("json") json: Boolean = true
    ): ApiResponse<*>

    /**
     *
     * POST ()
     */
    @POST(Path.QUERY_ORDER_REFUND_STATUS)
    @FormUrlEncoded
    suspend fun getQueryRefundStatus(
        @Field("123") abc: String,
        @Field("json") json: Boolean = true
    ): ApiResponse<*>

    /**
     *
     * POST ()
     */
    @POST(Path.SEND_TICKET_MSG)
    @FormUrlEncoded
    suspend fun getSendTicketMsg(
        @Field("123") abc: String,
        @Field("json") json: Boolean = true
    ): ApiResponse<*>

}