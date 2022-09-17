package com.kotlin.android.ktx.ext.permission

import java.util.concurrent.atomic.AtomicInteger

/**
 * 权限授权回调缓存池
 *
 * Created on 2020/6/8.
 *
 * @author o.s
 */
object PermissionsCallbackCache {
    private val atomicInteger = AtomicInteger(1000)
    private val callbacks = mutableMapOf<Int, PermissionsCallback>()

    /**
     * 根据 [requestCode] 获取缓存池中的callback
     */
    fun get(requestCode: Int): PermissionsCallback? {
        return callbacks[requestCode].also {
            callbacks.remove(requestCode)
        }
    }

    /**
     * [atomicInteger] 原子操作获取requestCode，并把 [callback] 缓存到缓存池中
     * return requestCode
     */
    fun put(callback: PermissionsCallback): Int {
        return atomicInteger.getAndIncrement().also {
            callbacks[it] = callback
        }
    }

}