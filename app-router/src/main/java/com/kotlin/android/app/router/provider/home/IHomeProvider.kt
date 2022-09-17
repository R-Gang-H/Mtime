package com.kotlin.android.app.router.provider.home

import com.kotlin.android.app.router.path.RouterProviderPath
import com.kotlin.android.router.annotation.RouteProvider
import com.kotlin.android.router.provider.IBaseProvider

/**
 * 创建者: zl
 * 创建时间: 2020/7/3 3:32 PM
 * 描述:
 */
@RouteProvider(path = RouterProviderPath.Provider.PROVIDER_HOME)
interface IHomeProvider : IBaseProvider {

    fun startTopListActivity()

    fun startToplistDetailActivity(toplistId: Long)

    fun startToplistGameDetailActivity(rankType: Long)

    fun startToplistGameActivity()

    fun startFindMovie()
}