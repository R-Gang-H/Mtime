package com.kotlin.android.statistics.sensors

import android.content.Context
import com.kotlin.android.ktx.utils.LogUtils
import com.kotlin.android.statistics.BuildConfig
import com.sensorsdata.analytics.android.sdk.SAConfigOptions
import com.sensorsdata.analytics.android.sdk.SensorsAnalyticsAutoTrackEventType
import com.sensorsdata.analytics.android.sdk.SensorsDataAPI
import org.json.JSONException
import org.json.JSONObject


/**
 * 创建者: zl
 * 创建时间: 2021/3/26 5:01 下午
 * 描述:神策埋点管理类
 */
class SensorsManager private constructor() {
    /**
     * 为了加快本地编译速度，开发过程中关闭埋点功能，如需调试，自行打开
     */
    private var debug = BuildConfig.DEBUG

    companion object {
        val instance by lazy { SensorsManager() }

        /**
         * https://manual.sensorsdata.cn/sa/latest/tech_sdk_client_android_basic-17563956.html#id-.SDK%E9%9B%86%E6%88%90(Android%EF%BC%89v1.13-%E8%AE%BE%E7%BD%AE%E4%BA%8B%E4%BB%B6%E5%85%AC%E5%85%B1%E5%B1%9E%E6%80%A7
         */
        fun sensorsTrack(eventName: String, properties: JSONObject? = null) {
            if (instance.debug) {
                return
            }
//            LogUtils.e("sensorsTrack eventName：$eventName properties: $properties")
            try {
                if (properties == null) {
                    SensorsDataAPI.sharedInstance().track(eventName)
                } else {
                    SensorsDataAPI.sharedInstance()
                        .track(eventName, properties)
                }
            } catch (e: JSONException) {
                LogUtils.e("sensorsTrack: $e")
            }
        }

    }

    fun login(userId: String?) {
        if (debug) {
            return
        }
        SensorsDataAPI.sharedInstance().login(userId)
    }

    /**
     * 初始化神策埋点sdk
     */
    fun initSensors(context: Context, api: String, isAgreePrivacy: Boolean): SensorsManager {
        if (debug) {
            return this
        }
        val SA_SERVER_URL = when (api) {
            "prd" -> {
                "https://ma-datasink.wanda.cn:18106/sa?project=production"
            }
            else -> {//stg、qas用一个地址
                "https://ma-datasink.wanda.cn:18106/sa?project=production"
            }
        }
        // 初始化配置
        val saConfigOptions = SAConfigOptions(SA_SERVER_URL)
        // 开启全埋点
        saConfigOptions.setAutoTrackEventType(
            SensorsAnalyticsAutoTrackEventType.APP_CLICK or
                    SensorsAnalyticsAutoTrackEventType.APP_START or
                    SensorsAnalyticsAutoTrackEventType.APP_END or
                    SensorsAnalyticsAutoTrackEventType.APP_VIEW_SCREEN
        ).enableLog(false)

        if (!isAgreePrivacy) {
            // 禁用数据采集
            saConfigOptions.disableDataCollect()
        } else {
            // 开启数据采集
            SensorsDataAPI.sharedInstance().enableDataCollect()
        }
        //https://manual.sensorsdata.cn/sa/latest/app-h5-7551956.html
        // 开启 App 打通 H5
        // isSupportJellyBean：是否支持 API level 16 及以下的版本，16 及以下版本 API 中，addJavascriptInterface 有安全漏洞，需要谨慎使用。
        saConfigOptions.enableJavaScriptBridge(true)

        // 开启可视化全埋点
        saConfigOptions.enableVisualizedAutoTrack(true)
        // 需要在主线程初始化神策 SDK
        SensorsDataAPI.startWithConfigOptions(context, saConfigOptions)
        return this
    }

    fun start() {
        if (debug) {
            return
        }
        SensorsDataAPI.sharedInstance().enableDataCollect()
    }
}