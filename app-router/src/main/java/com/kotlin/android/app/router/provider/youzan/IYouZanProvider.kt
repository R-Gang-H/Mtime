package com.kotlin.android.app.router.provider.youzan

import com.kotlin.android.app.router.path.RouterProviderPath
import com.kotlin.android.router.annotation.RouteProvider
import com.kotlin.android.router.provider.IBaseProvider

/**
 * @author zhangjian
 * @date 2021/10/26 09:09
 */
@RouteProvider(path = RouterProviderPath.Provider.PROVIDER_YOUZAN_WEB)
interface IYouZanProvider:IBaseProvider {
    fun startYouZanWebView(url:String)
}