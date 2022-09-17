package com.kotlin.android.retrofit

import retrofit2.Retrofit

/**
 * Retrofit构造工厂：
 *
 * Created on 2020/5/13.
 *
 * @author o.s
 */
class RetrofitFactory private constructor() {

    companion object {
        fun create(baseUrl: String): Retrofit {
            return RetrofitManager.retrofit(baseUrl)
        }
    }

}