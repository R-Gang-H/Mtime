package com.kotlin.android.app.router.provider.splash

import com.kotlin.android.app.router.path.RouterProviderPath
import com.kotlin.android.router.annotation.RouteProvider
import com.kotlin.android.router.provider.IBaseProvider

/**
 * @author ZhouSuQiang
 * @email zhousuqiang@126.com
 * @date 2020/11/23
 */
@RouteProvider(path = RouterProviderPath.Provider.PROVIDER_SPLASH)
interface ISplashProvider : IBaseProvider {
    fun startSplashActivity()
}