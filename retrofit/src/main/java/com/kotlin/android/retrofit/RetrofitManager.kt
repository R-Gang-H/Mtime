package com.kotlin.android.retrofit

import com.kotlin.android.retrofit.client.ClientConfig
import com.kotlin.android.retrofit.client.ClientManager
import retrofit2.Converter
import retrofit2.Retrofit

/**
 * Retrofit网络框架管理器：
 *
 * Created on 2020-01-10.
 *
 * @author o.s
 */
object RetrofitManager {
    private lateinit var config: ClientConfig
    private lateinit var factory: Array<out Converter.Factory>

    /**
     * 初始化Retrofit管理器
     */
    fun init(config: ClientConfig, vararg factory: Converter.Factory) {
        RetrofitManager.config = config
        RetrofitManager.factory = factory
        ClientManager.init(config)
    }

    /**
     * 构建Retrofit对象
     */
    fun retrofit(baseUrl: String): Retrofit {
        val builder = Retrofit.Builder()
                .baseUrl(baseUrl)
                .client(ClientManager.client)
        factory.forEach {
            builder.addConverterFactory(it)
        }
        return builder.build()
    }
}