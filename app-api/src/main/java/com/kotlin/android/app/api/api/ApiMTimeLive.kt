package com.kotlin.android.app.api.api

import com.kotlin.android.api.ApiResponse
import com.kotlin.android.app.data.entity.common.CommonShare
import com.kotlin.android.app.data.entity.live.*

import retrofit2.http.GET
import retrofit2.http.Query

/**
 * @author ZhouSuQiang
 * @email zhousuqiang@126.com
 * @date 2021/3/4
 */
interface ApiMTimeLive : ApiChatRoom {

    companion object {
        /**
         * 直播
         */
        const val LIVE_LIST = "/live_room/getLiveList" //直播列表页总接口
        const val LIVE_WONDER_VOD_LIST = "/live_room/getWonderVodList" //直播列表页中，获取回放列表
        const val LIVE_APPOINT = "/live_room/appoint" //直播前预约接口
        const val LIVE_DETAIL = "/live/detail"//直播详情页
        const val LIVE_SHARE = "/live_room/getLiveShareInfo"//获取直播分享信息
        const val LIVE_CAMERA_LIST = "/live_room/cameraList"//刷新机位列表
        const val LIVE_CAMERA_PLAY = "/live_room/play"//机位直播播放地址
        const val LIVE_NEWS_LIST = "/live_room/news" // 直播相关联的资讯内容列表
        const val LIVE_SIGNAL_POLLING = "/signal/polling"//信令轮询
    }

    /**
     * 直播列表页总接口
     */
    @GET(LIVE_LIST)
    suspend fun getLiveList(): ApiResponse<LiveInfoList>

    /**
     * 直播列表页中，获取回放列表
     */
    @GET(LIVE_WONDER_VOD_LIST)
    suspend fun getLiveWonderVodList(
            @Query("pageNo") pageNo: Long,
            @Query("pageSize") pageSize: Long
    ): ApiResponse<LiveInfoList>


    /**
     * 直播间-新详情（/live/detail)
     * GET(/live/detail)
     * @param liveId    Number      直播id
     * @param  locationId   Number  城市id默认290
     * @param   clientId    String  客户端唯一码
     * @param   version     String  版本号(默认1)
     */
    @GET(LIVE_DETAIL)
    suspend fun getLiveDetail(@Query("liveId") liveId: Long, @Query("locationId") locationId: Long,
                              @Query("clientId") clientId: String): ApiResponse<LiveDetail>


    /**
     * 获取直播分享信息（/live_room/getLiveShareInfo）
     * GET(/live_room/getLiveShareInfo)
     * @param  id   Number  直播id
     */
    @GET(LIVE_SHARE)
    suspend fun getShareInfo(@Query("id") id: Long): ApiResponse<CommonShare>

    /**
     * 直播预约（/live_room/appoint）
     */
    @GET(LIVE_APPOINT)
    suspend fun getLiveAppoint(@Query("liveId") liveId: Long): ApiResponse<LiveAppointResult>

    /**
     * 刷新机位列表
     * GET(/live_room/cameraList)
     * @param   liveId  Number  直播id
     */
    @GET(LIVE_CAMERA_LIST)
    suspend fun getCameraList(@Query("liveId") liveId: Long):ApiResponse<CameraStandList>

    /**
     * 直播机位播放地址
     * GET(/live_room/play)
     * @param   videoId     Number  机位id
     */
    @GET(LIVE_CAMERA_PLAY)
    suspend fun getCameraPlayUrl(@Query("videoId")videoId:Long):ApiResponse<CameraPlayUrl>

    /**
     * 直播资讯列表
     * GET(/live_room/news)
     * @param   liveId     Number  直播ID
     */
    @GET(LIVE_NEWS_LIST)
    suspend fun getLiveNews(@Query("liveId") liveId: Long):ApiResponse<LiveNewsList>

    /**
     * 信令轮询
     * GET(/signal/polling)
     */
    @GET(LIVE_SIGNAL_POLLING)
    suspend fun getSignalPolling(@Query("roomNum") roomNum:String):ApiResponse<ArrayList<SignalPolling>>
}