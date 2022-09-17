package com.kotlin.android.app.api.api

import com.kotlin.android.api.ApiResponse
import retrofit2.http.Field
import retrofit2.http.FormUrlEncoded
import retrofit2.http.POST

/**
 * user服务接口规范
 *
 * Created on 2020/5/13.
 *
 * @author o.s
 */
interface ApiUser {
    /**
     * user服务接口的path集
     */
    object Path {
        const val DEVICE_TOKEN = "/messagepush/devicetoken_active.api" // 绑定手机token
        const val POINTS = "/user/query_points.api" // 查询用户积分等级
        const val POINTS_HISTORY = "/user/query_points_history.api" // 查询用户积分历史

    }

    /**
     * 查询用户积分等级
     * POST ("/user/query_points.api")
     */
    @POST(Path.POINTS)
    @FormUrlEncoded
    suspend fun getPoints(
        @Field("json") json: Boolean = true
    ): ApiResponse<*>

    /**
     * 查询用户积分历史
     * POST ("/user/query_points_history.api")
     */
    @POST(Path.POINTS_HISTORY)
    @FormUrlEncoded
    suspend fun getPointsHistory(
        @Field("json") json: Boolean = true
    ): ApiResponse<*>

}