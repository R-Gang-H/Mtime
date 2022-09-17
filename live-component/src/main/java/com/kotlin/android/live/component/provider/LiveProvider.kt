package com.kotlin.android.live.component.provider

import android.os.Bundle
import com.alibaba.android.arouter.facade.annotation.Route
import com.kotlin.android.live.component.ui.detail.LiveDetailActivity
import com.kotlin.android.router.RouterManager
import com.kotlin.android.router.ext.put
import com.kotlin.android.app.router.path.RouterActivityPath
import com.kotlin.android.app.router.path.RouterProviderPath
import com.kotlin.android.app.router.provider.live.ILiveProvider

/**
 * create by lushan on 2021/2/25
 * description:直播provider实现类
 */
@Route(path = RouterProviderPath.Provider.PROVIDER_LIVE)
class LiveProvider : ILiveProvider {
    /**
     * 跳转到直播详情
     */
    override fun launchLiveDetail(liveId: Long) {
        Bundle().apply {
            put(LiveDetailActivity.KEY_LIVE_ID, liveId)
        }.also {
            RouterManager.instance.navigation(
                path = RouterActivityPath.Live.PAGE_LIVE_DETAIL_ACTIVITY,
                bundle = it
            )
        }
    }

    /**
     * 跳转直播详情生成海报分享页
     */
    override fun launchLiveSharePoster(liveId: Long) {
        Bundle().apply {
            put(LiveDetailActivity.KEY_LIVE_ID, liveId)
        }.also {
            RouterManager.instance.navigation(RouterActivityPath.Live.PAGE_LIVE_SHARE_POSTER_ACTIVITY, it)
        }
    }

    override fun launchLiveTestEntrance() {
        RouterManager.instance.navigation(RouterActivityPath.Live.PAGE_LIVE_TEST_ENTRANCE_ACTIVITY)
    }

    override fun launchLiveList() {
        RouterManager.instance.navigation(RouterActivityPath.Live.PAGE_LIVE_LIST_ACTIVITY)
    }
}