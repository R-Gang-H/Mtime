package com.kotlin.android.app.api

import com.kotlin.android.api.config.AppConfig
import com.kotlin.android.api.header.MTimeApiHeader
import com.kotlin.android.api.header.storeToken
import com.kotlin.android.retrofit.CancelManager
import com.kotlin.android.retrofit.RetrofitManager
import com.kotlin.android.retrofit.cache.CacheManager
import com.kotlin.android.retrofit.client.ClientConfig
import com.kotlin.android.api.converter.ApiConverterFactory
import com.kotlin.android.api.header.storeServerTime
import com.kotlin.android.retrofit.cookie.CookieManager
import okhttp3.Interceptor
import okhttp3.Request

/**
 * API管理器：
 *
 * Created on 2020/8/10.
 *
 * @author o.s
 */
object ApiManager {

    init {
        init()
    }

    fun init() {
        /**
         * 初始化网络框架
         */
        RetrofitManager.init(
            ClientConfig.Builder()
                .setSSL(true)
                .setConnectTimeout(5_000L) // 5_000L
                .setReadTimeout(20_000L) // 10_000L
                .setWriteTimeout(30_000L) // 30_000L
                .setRetryCount(0)
                .setCache(CacheManager.instance.cache)
                .setCookie(CookieManager.instance.cookieJar)
                .addInterceptor(interceptor)
                .setForbid(AppConfig.instance.isForbid)
                .build(),
            ApiConverterFactory.create()
//                GsonConverterFactory.create()
        )
    }

    /**
     * 获取API服务
     */
    fun <T : Any> api(service: Class<T>): T {
        return ApiRepository.instance.getApi(service)
    }

    /**
     * API请求拦截器
     */
    private val interceptor
        get() = Interceptor {
            var request: Request
            it.request().apply {
                // handle cancel
                header(CancelManager.HEADER_CANCEL_TAG)?.apply {
                    if (CancelManager.isCancel(this)) {
                        it.call().cancel()
                        CancelManager.remove(this)
                    }
                }
                // add headers
                request = newBuilder().headers(MTimeApiHeader.getHeaders(url, method, body))
                    .removeHeader(CancelManager.HEADER_CANCEL_TAG).build()
            }

            val response = it.proceed(request)
            storeToken(response)
            storeServerTime(response)
            response
        }
}