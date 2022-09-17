package com.kotlin.android.app.router.provider.live

import com.kotlin.android.app.router.path.RouterProviderPath
import com.kotlin.android.router.annotation.RouteProvider
import com.kotlin.android.router.provider.IBaseProvider

/**
 * create by lushan on 2021/2/25
 * description: 直播
 */
@RouteProvider(path = RouterProviderPath.Provider.PROVIDER_LIVE)
interface ILiveProvider : IBaseProvider {
    /**
     * 跳转到直播详情
     */
    fun launchLiveDetail(liveId:Long)

    /**
     * 跳转直播详情生成海报分享页
     */
    fun launchLiveSharePoster(liveId: Long)

    fun launchLiveTestEntrance()

    fun launchLiveList()
}