package com.kotlin.android.app.api.api

/**
 * coupon服务接口规范
 *
 * Created on 2020/5/13.
 *
 * @author o.s
 */
interface ApiCoupon {

    /**
     * coupon服务接口的path集
     */
    object Path {
        const val COUPON_EXPIRE_SOON = "/coupon/expiresoon.api" //三日内过期券数量查询
        const val COUPON_BIND_COUNT = "/coupon/backendbindcount.api" //后台新绑定券数量查询
    }

//    /**
//     *
//     * GET ()
//     */
//    @GET(Path.)
//    suspend fun get(
//        @Query("123") abc: String,
//        @Query("json") json: Boolean = true
//    ): ApiResponse<*>
}