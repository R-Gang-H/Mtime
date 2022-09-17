package com.kotlin.android.live.component.repository

import com.kotlin.android.api.ApiResult
import com.kotlin.android.app.api.base.BaseRepository
import com.kotlin.android.app.data.entity.common.ShareResultExtend
import com.kotlin.android.app.data.entity.live.*
import com.kotlin.android.live.component.viewbean.LiveDetailExtraBean
import com.kotlin.android.live.component.viewbean.LiveVideoExtraBean
import com.kotlin.android.mtime.ktx.GlobalDimensionExt
import com.kotlin.android.mtime.ktx.getDeviceToken

/**
 * create by lushan on 2021/3/4
 * description:直播详情repository
 */
class LiveDetailRepository : BaseRepository() {

    /**
     * 添加分享接口
     */
    suspend fun <T> getShareInfo(liveId: Long, extend: T): ApiResult<ShareResultExtend<T>> {
        return request(
            api = { apiMTimeLive.getShareInfo(liveId) },
            converter = { ShareResultExtend(result = it, extend) }
        )
    }

    /**
     * 获取直播详情数据
     */
    suspend fun getLiveDetail(liveId: Long, isFromLogin: Boolean): ApiResult<LiveDetailExtraBean> {
        return request(
            api = {
                val clientId: String = getDeviceToken()
                val locationId = GlobalDimensionExt.getDigitsCurrentCityId()
                apiMTimeLive.getLiveDetail(liveId, locationId, clientId)
            }, converter = { LiveDetailExtraBean(it, isFromLogin) }
        )
    }


    /**
     * 直播预约
     */
    suspend fun liveAppoint(liveId: Long): ApiResult<LiveAppointResult> {
        return request(
            api = {
                apiMTimeLive.getLiveAppoint(liveId)
            }, converter = { it }
        )
    }


    /**
     * 获取机位列表
     */
    suspend fun getCameraStandList(liveId: Long, isUpate: Boolean): ApiResult<LiveVideoExtraBean> {
        return request(
            api = {
                apiMTimeLive.getCameraList(liveId)
            }, converter = { LiveVideoExtraBean(it, isUpate) })
    }

    /**
     * 获取直播机位播放地址
     */
    suspend fun getCameraPlayUrl(videoId: Long): ApiResult<CameraPlayUrl> {
        return request(
            api = { apiMTimeLive.getCameraPlayUrl(videoId) },
            converter = { it }
        )
    }

    /**
     * 获取视频播放地址
     */
    suspend fun getVideoPlayUrlList(
        videoId: Long,
        source: Long,
        scheme: String,
        isReview: Boolean
    ): ApiResult<LiveVideoExtraBean> {
        return request(
            converter = {
                LiveVideoExtraBean(it, isReview)
            },
            api = { apiMTime.getPlayUrl(videoId, source, scheme) })
    }

    /**
     * 获取直播资讯列表
     */
    suspend fun getLiveNews(liveId: Long): ApiResult<LiveNewsList> {
        return request {
            apiMTimeLive.getLiveNews(liveId)
        }
    }

    /**
     * 轮询信令
     */
    suspend fun signalPolling(roomNum: String): ApiResult<MutableList<SignalPolling>> {
        return request { apiMTimeLive.getSignalPolling(roomNum) }
    }

    /**
     * 获取导播台信息
     */
    suspend fun getDirectorUnits(liveId: Long): ApiResult<DirectorUnits> {
        return request { apiMTime.getDirectorUnits(liveId) }
    }

}