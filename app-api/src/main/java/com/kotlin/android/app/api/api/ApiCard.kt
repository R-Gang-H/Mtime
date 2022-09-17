package com.kotlin.android.app.api.api

/**
 * card服务接口规范
 *
 * Created on 2020/5/13.
 *
 * @author o.s
 */
interface ApiCard {

    /**
     * card服务接口的path集
     */
    object Path {
        const val USER_CARD_EXPIRE_SOON = "/card/user_card_to_expiry.api"//三日内过期卡数量查询
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