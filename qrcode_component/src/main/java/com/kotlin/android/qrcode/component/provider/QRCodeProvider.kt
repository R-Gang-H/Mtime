package com.kotlin.android.qrcode.component.provider

import android.content.Context
import android.os.Bundle
import com.alibaba.android.arouter.facade.annotation.Route
import com.kotlin.android.qrcode.component.ui.login.QrcodeLoginActivity
import com.kotlin.android.router.RouterManager
import com.kotlin.android.router.ext.put
import com.kotlin.android.app.router.path.RouterActivityPath
import com.kotlin.android.app.router.path.RouterProviderPath
import com.kotlin.android.app.router.provider.qrcode.IQRcodeProvider

/**
 * create by lushan on 2020/12/21
 * description:
 */
@Route(path = RouterProviderPath.Provider.PROVIDER_QRCODE)
class QRCodeProvider:IQRcodeProvider {
    override fun startQrScanActivity() {
        RouterManager.instance.navigation(RouterActivityPath.QRCode.PAGE_QRCODE_ACTIVITY)
    }
    //二维码网页登录确认
    override fun startQrcodeLoginActivity(uuid: String, context: Context) {
        Bundle().apply {
            put(QrcodeLoginActivity.KEY_UUID, uuid)
        }.run {
            RouterManager.instance.navigation(
                    path = RouterActivityPath.QRCode.PAGE_QRCODE_LOGIN_ACTIVITY,
                    bundle = this,
                    context = context
            )
        }
    }
}