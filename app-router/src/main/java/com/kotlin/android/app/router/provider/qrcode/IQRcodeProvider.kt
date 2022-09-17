package com.kotlin.android.app.router.provider.qrcode

import android.content.Context
import com.kotlin.android.app.router.path.RouterProviderPath
import com.kotlin.android.router.annotation.RouteProvider
import com.kotlin.android.router.provider.IBaseProvider
/**
 * create by lushan on 2020/12/21
 * description:
 */
@RouteProvider(path = RouterProviderPath.Provider.PROVIDER_QRCODE)
interface IQRcodeProvider :IBaseProvider{

    /**
     * 二维码扫描页面
     */
    fun startQrScanActivity()


    /**
     * 二维码网页登录确认
     */
    fun startQrcodeLoginActivity(uuid: String, context: Context)
}

