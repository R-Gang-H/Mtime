package com.kotlin.android.retrofit

import java.util.concurrent.ConcurrentHashMap

/**
 * Retrofit网络请求取消管理器：
 *
 * Created on 2020/8/11.
 *
 * @author o.s
 */
object CancelManager {

    /**
     * 网络请求头取消请求的Key，Value为页面唯一标识字符串：如 Activity、Fragment 等类名
     */
    const val HEADER_CANCEL_TAG = "CancelTag"

    /**
     * 待取消的请求标识缓存队列
     */
    private val map = ConcurrentHashMap<String, Boolean>()

    /**
     * 获取标识的请求是否可取消
     */
    fun isCancel(tag: String): Boolean {
        return map.contains(tag) && map[tag] ?: false
    }

    /**
     * 取消标识的请求
     */
    fun cancel(tag: String) {
        if (map.contains(tag)) {
            map[tag] = true
        }
    }

    /**
     * 请求取消后会调用该方法移除
     */
    fun remove(tag: String) {
        map.remove(tag)
    }
}