package com.kotlin.android.home.provider

import android.content.Context
import android.os.Bundle
import com.alibaba.android.arouter.facade.annotation.Route
import com.kotlin.android.home.ui.toplist.constant.TopListConstant
import com.kotlin.android.router.RouterManager
import com.kotlin.android.router.ext.put
import com.kotlin.android.app.router.path.RouterActivityPath
import com.kotlin.android.app.router.path.RouterProviderPath
import com.kotlin.android.app.router.provider.home.IHomeProvider

/**
 * 创建者: zl
 * 创建时间: 2020/6/5 10:14 AM
 * 描述:
 */
@Route(path = RouterProviderPath.Provider.PROVIDER_HOME)
class HomeProvider : IHomeProvider {
    override fun init(context: Context?) {

    }

    /**
     * 榜单页
     */
    override fun startTopListActivity() {
        RouterManager.instance.navigation(RouterActivityPath.Home.PAGER_TOPLIST_ACTIVITY)
    }

    override fun startToplistDetailActivity(toplistId: Long) {
        val bundle = Bundle().put(TopListConstant.KEY_TOPLIST_ID, toplistId)
        RouterManager.instance.navigation(RouterActivityPath.Home.PAGER_TOPLIST_DETAIL_ACTIVITY, bundle)
    }

    override fun startToplistGameDetailActivity(rankType: Long) {
        val bundle = Bundle().put(TopListConstant.KEY_TOPLIST_ID, rankType)
        RouterManager.instance.navigation(
            RouterActivityPath.Home.PAGER_TOPLIST_GAME_DETAIL_ACTIVITY,
                bundle)
    }

    override fun startToplistGameActivity() {
        RouterManager.instance.navigation(RouterActivityPath.Home.PAGER_TOPLIST_GAME_ACTIVITY)
    }

    override fun startFindMovie() {
        RouterManager.instance.navigation(RouterActivityPath.Home.PAGER_FIND_MOVIE_ACTIVITY)
    }
}