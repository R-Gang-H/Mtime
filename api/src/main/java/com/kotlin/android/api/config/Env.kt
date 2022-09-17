package com.kotlin.android.api.config

import com.kotlin.android.api.key.ApiKey


/**
 * 环境：App构建环境适配:
 * 包括网络请求host、加密、图片代理、埋点、批量请求、等环境参数的匹配分发。
 * 不限于此！
 *
 * Created on 2020/5/7.
 *
 * @author o.s
 */
class Env private constructor() {
    val gatewayUrl: String = ""
    val batchUrl: String = ""
    val logUrl: String = ""

    /**
     * dev使用http，其他都使用https
     */
    var hostMTime: String = when (AppConfig.instance.api) {
        ApiKey.DEV -> {
            ApiConfig.HOST_ARRAY_DEV[11]
        }
        else -> {
            ApiConfig.HOST_ARRAY_PRD[11]
        }
    }

    /**
     * dev使用http，其他都使用https
     */
    var hostMTimeLive: String = when (AppConfig.instance.api) {
        ApiKey.DEV -> {
            ApiConfig.HOST_ARRAY_DEV[13]
        }
        else -> {
            ApiConfig.HOST_ARRAY_PRD[13]
        }
    }

    /**
     * 定位成功后赋值经纬度，写架构自测用的
     */
//    var longitude: Long = 0L
//    var latitude: Long = 0L

    /**
     * 位置，写架构自测用的
     */
//    val location: String
//        get() {
//            return "$longitude,$latitude"
//        }

    companion object {
        val instance by lazy { Env() }

        fun isRelease(): Boolean {
            return ApiKey.PRD == AppConfig.instance.api || ApiKey.STG == AppConfig.instance.api
        }
    }

}