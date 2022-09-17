package com.kotlin.android.app.api.api

import com.kotlin.android.api.ApiResponse
import com.kotlin.android.api.config.ApiConfig
import retrofit2.http.GET
import retrofit2.http.Query

/**
 * payment服务接口规范
 *
 * Created on 2020/5/13.
 *
 * @author o.s
 */
interface ApiPayment {

    /**
     * payment服务接口的path集
     */
    object Path {
        const val PAY_METHOD_LIST = "/movie/movie_detail.api" // 获取当前影院当前业务支持的支付方式
    }

    /**
     * 获取当前影院当前业务支持的支付方式
     * GET ("/order/pay_method_list.api")
     *
     * cinemaId     int     必选  影院id
     * businessId   int     必选  业务范围：1购票、2小卖、3会员卡充值 4会员卡销售、5券销售，6拼团活动
     * channelType  int     必填  渠道类型: 1APP,2PC,3H5 4 KIOSK
     */
    @GET(Path.PAY_METHOD_LIST)
    suspend fun getPayMethodList(
        @Query("cinemaId") cinemaId: String,
        @Query("businessId") businessId: Int,
        @Query("channelType") channelType: Int = ApiConfig.CHANNEL_TYPE,
        @Query("json") json: Boolean = true
    ): ApiResponse<*>
}