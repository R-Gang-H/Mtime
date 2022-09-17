package com.kotlin.android.retrofit.client

import okhttp3.Cache
import okhttp3.CookieJar
import okhttp3.Interceptor

/**
 * OkHttpClient相关配置：
 *
 * Created on 2020/8/10.
 *
 * @author o.s
 */
class ClientConfig private constructor(
    val connectTimeout: Long,
    val readTimeout: Long,
    val writeTimeout: Long,
    val retryCount: Int,
    val ssl: Boolean, // SSL配置开关
    val cache: Cache? = null, // 缓存
    val cookie: CookieJar? = null, // cookie
    val interceptors: MutableList<Interceptor>, // 拦截器列表
    val isForbid: Boolean = false
) {
    class Builder {
        private var connectTimeout: Long = 0
        private var readTimeout: Long = 0
        private var writeTimeout: Long = 0
        private var retryCount: Int = 3
        private var ssl: Boolean = false
        private var cache: Cache? = null
        private var cookie: CookieJar? = null
        private val interceptors: MutableList<Interceptor> = mutableListOf()
        private var isForbid: Boolean = false

        init {
            getDefault()
        }

        fun setConnectTimeout(timeout: Long) = apply {
            connectTimeout = timeout
        }

        fun setReadTimeout(timeout: Long) = apply {
            readTimeout = timeout
        }

        fun setWriteTimeout(timeout: Long) = apply {
            writeTimeout = timeout
        }

        fun setRetryCount(retryCount: Int) = apply {
            this.retryCount = retryCount
        }

        fun setSSL(ssl: Boolean) = apply {
            this.ssl = ssl
        }

        fun setCache(cache: Cache) = apply {
            this.cache = cache
        }

        fun setCookie(cookie: CookieJar) = apply {
            this.cookie = cookie
        }

        fun addInterceptor(interceptor: Interceptor) = apply {
            interceptors += interceptor
        }

        fun setForbid(isForbid: Boolean) = apply {
            this.isForbid = isForbid
        }

        private fun getDefault() {
            connectTimeout = 15_000L
            readTimeout = 15_000L
            writeTimeout = 30_000L
            ssl = true
        }

        fun build(): ClientConfig = ClientConfig(
            connectTimeout,
            readTimeout,
            writeTimeout,
            retryCount,
            ssl,
            cache,
            cookie,
            interceptors,
            isForbid
        )
    }
}