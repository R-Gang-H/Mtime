package com.kotlin.android.app.api.api

import com.kotlin.android.api.ApiResponse
import retrofit2.http.GET
import retrofit2.http.Query

/**
 * count服务接口规范
 *
 * Created on 2020/5/13.
 *
 * @author o.s
 */
interface ApiCount {

    /**
     * count服务接口的path集
     */
    object Path {
        const val APP_COUNT = "/app/app_count.api" // App分享数据统计接口
    }

    /**
     * App分享数据统计接口
     * GET ("/app/app_count.api")
     *
     * shareType        String	非必选   (1.影评分享、2.APP截图分享、3.APP分享邀请好友、4.购票红包)
     * channelCode      String	非必选   (1:微信，2.朋友圈，3.QQ，4.微博）
     * sharePageId      String	非必选   分享页面标识
     * relationFilmId   String	非必选	影片ID
     * relationCinemaId String	非必选	影院ID
     * relationOrderId  String	非必选	订单ID
     * shareContent     String	非必选	分享内容（除以上ID外的内容）
     * visitId	        String	非必选	访客ID(目前后台使用的是IP）
     */
    @GET(Path.APP_COUNT)
    suspend fun getAppCount(
        @Query("shareType") shareType: String,
        @Query("channelCode") channelCode: String,
        @Query("sharePageId") sharePageId: String,
        @Query("relationFilmId") relationFilmId: String,
        @Query("relationCinemaId") relationCinemaId: String,
        @Query("relationOrderId") relationOrderId: String,
        @Query("shareContent") shareContent: String,
        @Query("visitId") visitId: String,
        @Query("json") json: Boolean = true
    ): ApiResponse<*>
}