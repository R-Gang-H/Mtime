package com.kotlin.android.app.router.provider.sdk

import com.kotlin.android.app.data.entity.js.sdk.BrowserEntity
import com.kotlin.android.app.router.path.RouterProviderPath
import com.kotlin.android.router.annotation.RouteProvider
import com.kotlin.android.router.provider.IBaseProvider

/**
 *
 * Created on 2020/11/3.
 *
 * @author o.s
 */
@RouteProvider(path = RouterProviderPath.Provider.PROVIDER_JS_SDK)
interface IJsSDKProvider : IBaseProvider {

    fun startH5Activity(data: BrowserEntity?)
}