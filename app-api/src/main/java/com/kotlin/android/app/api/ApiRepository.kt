package com.kotlin.android.app.api

import com.kotlin.android.api.config.Env
import com.kotlin.android.app.api.api.ApiMTime
import com.kotlin.android.app.api.api.ApiMTimeLive
import com.kotlin.android.retrofit.RetrofitRepository


/**
 *
 * Created on 2020/5/13.
 *
 * @author o.s
 */
class ApiRepository private constructor() {

    /**
     * Api缓存池
     * key：Api类型
     * value：Api对象
     */
    private val repo = HashMap<Class<*>, Any>()

    companion object {
        val instance by lazy { ApiRepository() }
    }

    fun <T : Any> getApi(service: Class<T>): T {
        val api = repo[service]
        return if (api != null) {
            api as? T ?: generateApi(service)
        } else {
            generateApi(service)
        }
    }

    private fun <T : Any> generateApi(service: Class<T>): T {
        val api = RetrofitRepository.instance.getRetrofit(ApiType.obtain(service).baseUrl).create(service)
        repo[service] = api
        return api
    }

    enum class ApiType(val service: Class<*>, var baseUrl: String) {
        MTime(ApiMTime::class.java, Env.instance.hostMTime),
        MTimeLive(ApiMTimeLive::class.java, Env.instance.hostMTimeLive);

        companion object {
            fun obtain(service: Class<*>): ApiType {
                return when (service) {
                    ApiMTimeLive::class.java -> MTimeLive
                    else -> MTime
                }
            }
        }
    }
}