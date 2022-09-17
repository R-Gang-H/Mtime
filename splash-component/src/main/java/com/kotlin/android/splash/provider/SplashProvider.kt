package com.kotlin.android.splash.provider

import com.alibaba.android.arouter.facade.annotation.Route
import com.kotlin.android.router.RouterManager
import com.kotlin.android.app.router.path.RouterActivityPath
import com.kotlin.android.app.router.path.RouterProviderPath
import com.kotlin.android.app.router.provider.splash.ISplashProvider

/**
 * @author ZhouSuQiang
 * @email zhousuqiang@126.com
 * @date 2020/11/23
 */
@Route(path = RouterProviderPath.Provider.PROVIDER_SPLASH)
class SplashProvider : ISplashProvider {
    override fun startSplashActivity() {
        RouterManager.instance.navigation(RouterActivityPath.Splash.PAGER_SPLASH_ACTIVITY)
    }
}