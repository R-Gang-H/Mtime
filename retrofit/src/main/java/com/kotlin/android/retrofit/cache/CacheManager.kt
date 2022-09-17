package com.kotlin.android.retrofit.cache

import android.content.Context
import com.kotlin.android.core.CoreApp
import okhttp3.Cache

/**
 * 缓存管理器：
 *
 * Created on 2020/8/14.
 *
 * @author o.s
 */
class CacheManager private constructor(context: Context) {

    private val cacheSize = 10 * 1024 * 1024L
    val cache: Cache = Cache(context.cacheDir, cacheSize)

    companion object {
        val instance by lazy { CacheManager(CoreApp.instance) }
    }

    fun clear() {
        cache.evictAll()
    }
}