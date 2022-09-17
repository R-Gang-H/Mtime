package com.kotlin.android.retrofit

import retrofit2.Retrofit
import java.util.concurrent.ConcurrentHashMap

/**
 * Retrofit资源库：
 *
 * Created on 2020/5/13.
 *
 * @author o.s
 */
class RetrofitRepository {

    /**
     * Retrofit缓存池
     * key：baseUrl
     * value：Retrofit对象
     */
    private val repo = ConcurrentHashMap<String, Retrofit>()

    companion object {
        val instance by lazy { RetrofitRepository() }
    }

    fun getRetrofit(baseUrl: String): Retrofit {
        return repo[baseUrl] ?: generateRetrofit(baseUrl)
    }

    private fun generateRetrofit(baseUrl: String): Retrofit {
        val retrofit = RetrofitFactory.create(baseUrl)
        repo[baseUrl] = retrofit
        return retrofit
    }
}