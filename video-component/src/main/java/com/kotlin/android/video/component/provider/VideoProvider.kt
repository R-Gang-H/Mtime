package com.kotlin.android.video.component.provider

import android.content.Context
import android.os.Bundle
import com.alibaba.android.arouter.facade.annotation.Route
import com.kotlin.android.router.RouterManager
import com.kotlin.android.router.ext.put
import com.kotlin.android.app.router.path.RouterActivityPath
import com.kotlin.android.app.router.path.RouterProviderPath
import com.kotlin.android.app.router.provider.video.IVideoProvider
import com.kotlin.android.video.component.ui.detail.VideoDetailActivity

/**
 * create by lushan on 2020/9/2
 * description:视频组件provider
 */
@Route(path = RouterProviderPath.Provider.PROVIDER_VIDEO)
class VideoProvider :IVideoProvider{
    /**
     * 跳转到影片详情
     * @param videoId 视频id
     */
    override fun startPreVideoActivity(videoId:Long) {
        Bundle().apply {
            put(VideoDetailActivity.KEY_PRE_VIDEO_ID,videoId)
        }.also {
            RouterManager.instance.navigation(RouterActivityPath.Video.PAGE_VIDEO_PRE_VIDEO_ACTIVITY,it)
        }
    }

    override fun init(context: Context?) {
    }
}