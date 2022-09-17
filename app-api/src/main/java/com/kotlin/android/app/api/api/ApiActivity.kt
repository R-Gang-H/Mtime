package com.kotlin.android.app.api.api

import com.kotlin.android.api.ApiResponse
import com.kotlin.android.api.config.ApiConfig
import retrofit2.http.*

/**
 * activity服务接口规范
 *
 * Created on 2020/5/13.
 *
 * @author o.s
 */
interface ApiActivity {

    /**
     * activity服务接口的path集
     */
    object Path {
        const val ACTIVITY_INFO = "/activitylottery/get_by_id.api" // 根据活动ID查询活动信息
    }

    /**
     * 根据活动ID查询活动信息
     * GET (/activitylottery/get_by_id.api)
     *
     * params:
     * activityId    Long    必选    活动id
     */
    @GET(Path.ACTIVITY_INFO)
    suspend fun getActivityInfo(
        @Query("activityId") activityId: String,
        @Query("json") json: Boolean = true
    ): ApiResponse<*>

    /**
     * 活动评论
     * GET (/activity/comment.api)
     *
     * params:
     * activityId	long	必选  活动ID
     * content      String  必选  评论内容
     */
    @GET("/activity/comment.api")
    suspend fun getActivityComment(
        @Query("activityId") activityId: String,
        @Query("content") content: String,
        @Query("json") json: Boolean = true
    ): ApiResponse<*>

    /**
     * 活动评论列表
     * GET (/activity/comment_list.api)
     *
     * params:
     * activityId	long	必选  活动ID
     * pageIndex    int     必选  页码，从1开始
     * pageSize     int     必选  每页大小
     */
    @GET("/activity/comment_list.api")
    suspend fun getActivityComments(
        @Query("activityId") activityId: String,
        @Query("pageIndex") pageIndex: Int,
        @Query("pageSize") pageSize: Int,
        @Query("json") json: Boolean = true
    ): ApiResponse<*>

    /**
     * 评论点赞接口
     * GET (/activity/comment_like.api)
     *
     * params:
     * commentId	long	必选	 微评ID
     * isLike       int     必选  0：取消点赞  1：点赞
     */
    @GET("/activity/comment_like.api")
    suspend fun getActivityCommentLike(
        @Query("commentId") commentId: String,
        @Query("isLike") isLike: Int,
        @Query("json") json: Boolean = true
    ): ApiResponse<*>

    /**
     * 回复活动评论
     * GET (/activity/comment_reply.api)
     *
     * params:
     * commentId	long	必选	 微评ID
     * replyContent String  必选  回复微评内容
     */
    @GET("/activity/comment_reply.api")
    suspend fun getActivityCommentReply(
        @Query("commentId") commentId: String,
        @Query("replyContent") replyContent: String,
        @Query("json") json: Boolean = true
    ): ApiResponse<*>

    /**
     * 回复评论列表
     * GET (/activity/comment_reply_list.api)
     *
     * params:
     * commentIds   String      必选  评论ID，以”,“分割
     * pageIndex    int         必选  页码，从1开始
     * pageSize     int         必选  每页大小
     */
    @GET("/activity/comment_reply_list.api")
    suspend fun getActivityCommentReplies(
        @Query("commentIds") commentIds: String,
        @Query("pageIndex") pageIndex: Int,
        @Query("pageSize") pageSize: Int,
        @Query("json") json: Boolean = true
    ): ApiResponse<*>

    /**
     * 获取智能客服H5地址接口
     * GET (/system/get_customer_url.api)
     */
    @GET("/system/get_customer_url.api")
    suspend fun getCustomerService(
        @Query("json") json: Boolean = true
    ): ApiResponse<*>

    /**
     * 智能客服，机器人回复
     * GET (/system/robot_reply.api)
     *
     * params:
     * question     必选 String 问题
     * source       必选 String 来源
     * psessionid   必选 String 保持会话id(首次访问可为空，下次访问时将输出参数 'conversationid'赋值这个参数即可)
     */
    @GET("/system/robot_reply.api")
    suspend fun getRobot(
        @Query("question") question: String,
        @Query("source") source: String,
        @Query("conversationid") conversationid: String,
        @Query("json") json: Boolean = true
    ): ApiResponse<*>

    /**
     * 我的奖品列表
     * GET (/activity/get_user_prize.api)
     */
    @GET("/activity/get_user_prize.api")
    suspend fun getUserPrize(
        @Query("json") json: Boolean = true
    ): ApiResponse<*>

    /**
     * 保存收货地址
     * POST ("/activity/save_address.api")
     *
     * sendMobile	String	必选	收货手机号
     * sendAddress	String	必选	收货地址
     * sendName	    String	必选	收货人姓名
     * activityId	Long	必选	活动ID
     * lotteryId    Long	必选	抽奖记录ID（从我的奖品接口获得）
     */
    @POST("/activity/save_address.api")
    @FormUrlEncoded
    suspend fun getSaveAddress(
        @Field("sendMobile") mobile: String,
        @Field("sendAddress") address: String,
        @Field("sendName") name: String,
        @Field("activityId") activityId: String,
        @Field("lotteryId") lotteryId: String,
        @Field("json") json: Boolean = true
    ): ApiResponse<*>

    /**
     * 影片详情中推荐相关活动
     * GET ("/activity/film_detail.api")
     *
     * filmId	String	必选	影片ID
     */
    @GET("/activity/film_detail.api")
    suspend fun getFilmDetailActivity(
        @Query("filmId") filmId: String,
        @Query("json") json: Boolean = true
    ): ApiResponse<*>

    /**
     * 活动列表
     * GET ("/activity/activity_list.api")
     *
     * cityId	String	必选	    城市ID
     * cinemaId	String  可选 	影城ID
     */
    @GET("/activity/activity_list.api")
    suspend fun getActivityList(
        @Query("cityId") cityId: String,
//        @Query("cinemaId") cinemaId: String,
        @Query("json") json: Boolean = true
    ): ApiResponse<*>

    /**
     * 根据用户ID获取嵌入兑吧的免登陆url
     * GET ("/user/get_duiba_url.api")
     *
     * cityId	String	必选	    城市ID
     * cityName	String	否	    城市名称
     */
    @GET("/user/get_duiba_url.api")
    suspend fun getDuiBa(
        @Query("cityId") cityId: String,
        @Query("cityName") cityName: String,
        @Query("json") json: Boolean = true
    ): ApiResponse<*>

    /**
     * 查询拼团活动订单详情接口
     * GET ("/gather/order_detail.api")
     *
     * orderId	String	必选	订单号
     */
    @GET("/gather/order_detail.api")
    suspend fun getGatherOrderDetail(
        @Query("orderId") orderId: String,
        @Query("json") json: Boolean = true
    ): ApiResponse<*>

    /**
     * 拼团活动订单立即支付接口
     * POST ("/gather/to_pay.api")
     *
     * orderId	    String	必选	订单号
     * paymentType	int	    必选	第三方支付类型ID
     * paymentPrice	int	    必选	支付金额
     *
     * 自定义WIKI中没有定义
     * returnUrl    String
     */
    @POST("/gather/to_pay.api")
    @FormUrlEncoded
    suspend fun getGatherToPay(
        @Field("orderId") orderId: String,
        @Field("paymentType") paymentType: Int,
        @Field("paymentPrice") paymentPrice: Int,
        @Field("returnUrl") returnUrl: String = ApiConfig.PAY_SUCCESS_URL,
        @Field("json") json: Boolean = true
    ): ApiResponse<*>

    /**
     * 查询拼团订单支付信息
     * POST ("/gather/payment_status.api")
     *
     * orderId      String  必填  主订单ID
     * paymentId	long	必填  支付ID
     */
    @POST("/gather/payment_status.api")
    @FormUrlEncoded
    suspend fun getGatherPaymentStatus(
        @Field("orderId") orderId: String,
        @Field("paymentId") paymentId: Long,
        @Field("json") json: Boolean = true
    ): ApiResponse<*>

    /**
     * 查询拼团订单状态接口
     * POST ("/gather/order_status.api")
     *
     * orderId      String  必填  主订单ID
     */
    @POST("/gather/order_status.api")
    @FormUrlEncoded
    suspend fun getGatherOrderStatus(
        @Field("orderId") orderId: String,
        @Field("json") json: Boolean = true
    ): ApiResponse<*>

    /**
     * 查询拼团活动订单退款详情
     * POST ("/gather/refund_details.api")
     *
     * orderId      String  必填  主订单ID
     */
    @POST("/gather/refund_details.api")
    @FormUrlEncoded
    suspend fun getGatherRefundDetail(
        @Field("orderId") orderId: String,
        @Field("json") json: Boolean = true
    ): ApiResponse<*>

    /**
     * 拼团活动订单列表接口
     * GET ("/gather/order_list.api")
     *
     * pageIndex    int         必选  页码，从1开始
     * pageSize     int         必选  每页大小
     */
    @GET("/gather/order_list.api")
    suspend fun getGatherOrderList(
        @Query("pageIndex") pageIndex: Int,
        @Query("pageSize") pageSize: Int,
        @Query("json") json: Boolean = true
    ): ApiResponse<*>

    /**
     * 签到接口
     * GET ("/gather/gather_sign_in.api")
     *
     * orderId	String	必选	订单号
     * signCode	String	必选	签到码
     */
    @GET("/gather/gather_sign_in.api")
    suspend fun getGatherSignIn(
        @Query("orderId") orderId: String,
        @Query("signCode") signCode: String,
        @Query("json") json: Boolean = true
    ): ApiResponse<*>

}