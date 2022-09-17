package com.kotlin.android.retrofit.client

import androidx.annotation.IntRange
import com.kotlin.android.ktx.ext.log.i
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import java.util.concurrent.TimeUnit
import com.kotlin.android.retrofit.BuildConfig
import com.kotlin.android.retrofit.ssl.SSL
import com.kotlin.android.retrofit.ssl.UnSafeHostnameVerifier
import okhttp3.Interceptor
import okhttp3.Response
import okhttp3.internal.closeQuietly
import java.lang.IllegalStateException
import java.net.Proxy

/**
 *
 * Created on 2020/8/10.
 *
 * @author o.s
 */
object ClientManager {

    private var config: ClientConfig = ClientConfig.Builder().build()

    fun init(config: ClientConfig) {
        ClientManager.config = config
    }

    /**
     * 根据 [config] 配置构建 [OkHttpClient] 单例
     */
    val client by lazy {
        val builder = getClientBuilder()
        builder.build()
    }

    fun getDownloadClient(interceptor: Interceptor? = null): OkHttpClient? {
        val builder = getClientBuilder(interceptor)
        return builder.build()
    }

    private fun getClientBuilder(interceptor: Interceptor? = null): OkHttpClient.Builder {
        val builder = OkHttpClient.Builder()
            .connectTimeout(config.connectTimeout, TimeUnit.MILLISECONDS)
            .readTimeout(config.readTimeout, TimeUnit.MILLISECONDS)
            .writeTimeout(config.writeTimeout, TimeUnit.MILLISECONDS)
            .followRedirects(false)
            .followSslRedirects(false)
            .retryOnConnectionFailure(false) //不自动重连
        config.cache?.apply {
            builder.cache(this)
        }
        config.cookie?.apply {
            builder.cookieJar(this)
        }
        config.interceptors.forEach {
            builder.addInterceptor(it)
        }
        if (config.retryCount > 0) {
            builder.addInterceptor(RetryInterceptor(config.retryCount))
        }
        if (config.isForbid) {
            builder.proxy(Proxy.NO_PROXY)
        }

        // 设置SSL配置
        if (config.ssl) {
            builder.hostnameVerifier(UnSafeHostnameVerifier())
            SSL.instance.ignoreSSL?.apply {
                builder.sslSocketFactory(sslSocketFactory, trustManager)
            }
        }

        // Debug模式下打印日志
        if (BuildConfig.DEBUG) {
            builder.addInterceptor(logInterceptor)
            // Facebook/Stetho——Android开发调试神器
//            builder.addNetworkInterceptor(StethoInterceptor())
        }
        interceptor?.apply {
            builder.addInterceptor(this)
        }
        return builder
    }

    /**
     * 日志拦截器
     */
    private val logInterceptor = HttpLoggingInterceptor(
        object : HttpLoggingInterceptor.Logger {
            val sb = StringBuilder()
            override fun log(message: String) {
                if (message.contains("-->") && !message.contains("--> END")) {
                    if (sb.isNotEmpty()) {
                        "HttpLoggingInterceptor $sb".i()
                        sb.clear()
                    }
                }
                sb.append(message)
                sb.append("\n")
                if (message.contains("<-- END HTTP")) {
                    "HttpLoggingInterceptor $sb".i()
                    sb.clear()
                }
            }
        }
    ).apply {
        level = HttpLoggingInterceptor.Level.BODY
    }

}

/**
 * 重试拦截器
 */
class RetryInterceptor(@IntRange(from = 1) val retryCount: Int = 3) : Interceptor {

    override fun intercept(chain: Interceptor.Chain): Response {
        var retryCount = 0
        val request = chain.request()
        var response = chain.proceed(request)
        while (!response.isSuccessful && retryCount < this.retryCount) {
            retryCount++
            response.closeQuietly()
            response = chain.proceed(request)
        }
        return response
    }
}