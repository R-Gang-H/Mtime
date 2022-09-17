package com.kotlin.android.app.router.provider.daily

import com.kotlin.android.app.router.path.RouterProviderPath
import com.kotlin.android.router.annotation.RouteProvider
import com.kotlin.android.router.provider.IBaseProvider

/**
 * @author ZhouSuQiang
 * @email zhousuqiang@126.com
 * @date 2020/8/4
 */
@RouteProvider(path = RouterProviderPath.Provider.PROVIDER_DAILY_RCMD)
interface IDailyProvider: IBaseProvider {
    fun startDailyRecommendActivity()
}