package com.kotlin.android.app.api.api

import com.kotlin.android.api.ApiResponse
import retrofit2.http.GET
import retrofit2.http.Query

/**
 * snack服务接口规范
 *
 * Created on 2020/5/13.
 *
 * @author o.s
 */
interface ApiSnack {

    /**
     * snack服务接口的path集
     */
    object Path {
        const val SNACK_BY_CINEMA = "/snack/by_cinema.api" // 某影院推荐卖品
        const val SNACK_BY_CINEMA_CATEGORY = "/snack/by_cinema_category.api" // 获取某影院售卖分类下的卖品
        const val CATEGORY_BY_CINEMA = "/category/by_cinema.api" // 获取某影院所有售卖分类
        const val SNACK_BY_IDS = "/snack/query_snack_by_ids.api" // 根据卖品id集合查询卖品集合
    }

    /**
     * 某影院推荐卖品
     * GET ("/snack/by_cinema.api")
     *
     * cinemaId     string  必选  影院ID
     */
    @GET(Path.SNACK_BY_CINEMA)
    suspend fun getSnackByCinema(
        @Query("cinemaId") cinemaId: String,
        @Query("json") json: Boolean = true
    ): ApiResponse<*>

    /**
     * 获取某影院售卖分类下的卖品
     * GET ("/snack/by_cinema_category.api")
     *
     * cinemaId         string  必选  影院ID
     * saleCategoryId   int     必选  售卖分类ID
     * pageIndex        String  必选  第几页，从1开始
     * pageSize         String  必选  每页显示条数
     */
    @GET(Path.SNACK_BY_CINEMA_CATEGORY)
    suspend fun getSnackByCinemaCategory(
        @Query("cinemaId") cinemaId: String,
        @Query("saleCategoryId") saleCategoryId: Int,
        @Query("pageIndex") pageIndex: Int,
        @Query("pageSize") pageSize: Int,
        @Query("json") json: Boolean = true
    ): ApiResponse<*>

    /**
     * 获取某影院所有售卖分类
     * GET ("/category/by_cinema.api")
     *
     * cinemaId         string  必选  影院ID
     */
    @GET(Path.CATEGORY_BY_CINEMA)
    suspend fun getCategoryByCinema(
        @Query("cinemaId") cinemaId: String,
        @Query("json") json: Boolean = true
    ): ApiResponse<*>

    /**
     * 根据卖品id集合查询卖品集合
     * GET ("/snack/query_snack_by_ids.api")
     *
     * ids         string  必选  (卖品ID集合，以逗号分隔，必选)
     */
    @GET(Path.SNACK_BY_IDS)
    suspend fun getSnackByIds(
        @Query("ids") ids: String,
        @Query("json") json: Boolean = true
    ): ApiResponse<*>
}