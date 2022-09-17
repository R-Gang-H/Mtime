package com.kotlin.android.app.router.provider.search

import android.app.Activity
import android.content.Context
import com.kotlin.android.app.router.path.RouterProviderPath
import com.kotlin.android.router.annotation.RouteProvider
import com.kotlin.android.router.provider.IBaseProvider

/**
 * 搜索组件：
 *
 * Created on 2020/9/15.
 *
 * @author o.s
 */
@RouteProvider(path = RouterProviderPath.Provider.PROVIDER_SEARCH)
interface ISearchProvider : IBaseProvider {

    fun startSearchActivity(context: Context?)

    fun startPublishSearchActivity(activity: Activity?, searchType: Long, from: Long)

    fun startSearchGroupActivity(activity: Activity?, groupId:Long? = 0L)

}