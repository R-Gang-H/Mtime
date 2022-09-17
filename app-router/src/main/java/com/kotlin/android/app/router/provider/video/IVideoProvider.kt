package com.kotlin.android.app.router.provider.video

import com.kotlin.android.app.router.path.RouterProviderPath
import com.kotlin.android.router.annotation.RouteProvider
import com.kotlin.android.router.provider.IBaseProvider

/**
 * create by lushan on 2020/9/2
 * description:视频组件provider
 */
@RouteProvider(path = RouterProviderPath.Provider.PROVIDER_VIDEO)
interface IVideoProvider :IBaseProvider{
    /**
     * 跳转到预告片详情
     */
    fun startPreVideoActivity(videoId:Long)
}