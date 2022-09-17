package com.kotlin.android.qb.provider

import android.content.Context
import android.os.Bundle
import com.alibaba.android.arouter.facade.annotation.Route
import com.kotlin.android.app.data.entity.js.sdk.BrowserEntity
import com.kotlin.android.ktx.ext.KEY_DATA
import com.kotlin.android.router.RouterManager
import com.kotlin.android.app.router.path.RouterActivityPath
import com.kotlin.android.app.router.path.RouterProviderPath
import com.kotlin.android.app.router.provider.sdk.IJsSDKProvider

/**
 *
 * Created on 2020/11/3.
 *
 * @author o.s
 */
@Route(path = RouterProviderPath.Provider.PROVIDER_JS_SDK)
class JsSDKProvider : IJsSDKProvider {

    override fun startH5Activity(data: BrowserEntity?) {
        Bundle().apply {
            putSerializable(KEY_DATA, data)
        }.also {
            RouterManager.instance.navigation(
                    path = RouterActivityPath.JsSDK.PAGE_H5_ACTIVITY,
                    bundle = it
            )
        }
    }

    override fun init(context: Context?) {
    }
}