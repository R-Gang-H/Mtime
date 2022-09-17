package com.kotlin.android.qb

import android.content.Context
import com.kotlin.android.ktx.ext.log.d
import com.tencent.smtt.export.external.TbsCoreSettings
import com.tencent.smtt.sdk.QbSdk

/**
 * 创建者: zl
 * 创建时间: 2020/8/3 10:24 AM
 * 描述:
 */
object JsManager {
    private var isInitializer: Boolean = true
    fun initQbSdk(context: Context) {
        if (isInitializer) {
            isInitializer = !isInitializer
        }
        //https://x5.tencent.com/docs/access.html
        // 在调用TBS初始化、创建WebView之前进行如下配置
        val map = HashMap<String, Any>()
        map[TbsCoreSettings.TBS_SETTINGS_USE_SPEEDY_CLASSLOADER] = true
        map[TbsCoreSettings.TBS_SETTINGS_USE_DEXLOADER_SERVICE] = true
        QbSdk.initTbsSettings(map)


        //搜集本地tbs内核信息并上报服务器，服务器返回结果决定使用哪个内核。
        val cb: QbSdk.PreInitCallback = object : QbSdk.PreInitCallback {
            override fun onViewInitFinished(arg0: Boolean) {
                //x5內核初始化完成的回调，为true表示x5内核加载成功，否则表示x5内核加载失败，会自动切换到系统内核。
                " onViewInitFinished is $arg0".d()
            }

            override fun onCoreInitFinished() {
            }
        }
        //x5内核初始化接口
        QbSdk.initX5Environment(context, cb)
    }
}