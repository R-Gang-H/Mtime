package com.kotlin.android.api.config

import com.kotlin.android.core.CoreApp
import com.kotlin.android.ktx.ext.log.e
import com.kotlin.android.ktx.ext.io.safeClose
import java.io.*
import java.util.*

/**
 * app构建配置
 *
 * Created on 2020/5/6.
 *
 * @author o.s
 */
class AppConfig private constructor() {

    companion object {
        val instance by lazy { AppConfig() }
        const val CHANNEL: String = "channel"
        const val API: String = "api"
        const val FORBID: String = "forbid"
    }

    var api: String = ""
    var channel: String = ""
    var isForbid: Boolean = false//是否禁止代理

    init {
        var fis: InputStream? = null
        try {
            fis = CoreApp.instance.assets.open(ApiConfig.CONFIG_FILE_NAME)
            val props = Properties()
            props.load(fis)
            api = props.getProperty(API)
            channel = props.getProperty(CHANNEL)
            isForbid = props.getProperty(FORBID).toBoolean()
        } catch (e: Throwable) {
            e.e()
        } finally {
            fis.safeClose()
        }
    }
}