package com.kotlin.android.youzan.provider

import android.os.Bundle
import com.alibaba.android.arouter.facade.annotation.Route
import com.kotlin.android.router.RouterManager
import com.kotlin.android.app.router.path.RouterActivityPath
import com.kotlin.android.app.router.path.RouterProviderPath
import com.kotlin.android.app.router.provider.youzan.IYouZanProvider
import com.kotlin.android.youzan.YouzanActivity

/**
 * @author zhangjian
 * @date 2021/10/26 09:15
 */
@Route(path = RouterProviderPath.Provider.PROVIDER_YOUZAN_WEB)
class YouZanProvider : IYouZanProvider {
    override fun startYouZanWebView(url: String) {
        val bundle = Bundle()
        bundle.putString(YouzanActivity.KEY_URL, url)
        RouterManager.instance.navigation(RouterActivityPath.YOUZANWEB.PAGE_YOUZAN_WEBVIEW, bundle)
    }
}