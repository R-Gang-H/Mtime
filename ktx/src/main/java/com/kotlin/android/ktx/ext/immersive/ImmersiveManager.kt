package com.kotlin.android.ktx.ext.immersive

import android.app.Activity
import android.app.Dialog
import androidx.fragment.app.DialogFragment
import androidx.fragment.app.Fragment
import java.util.concurrent.ConcurrentHashMap

/**
 * 沉浸式管理器
 *
 * Created on 2021/12/8.
 *
 * @author o.s
 */
class ImmersiveManager private constructor() {

    companion object {
        val instance by lazy { ImmersiveManager() }
    }

    private val cacheMap by lazy { ConcurrentHashMap<Class<*>, Immersive>() }

    /**
     * 获取基于 [Activity] [Fragment] [DialogFragment] 的沉浸式对象。
     * 其类型时抛出异常 [ClassCastException]
     */
    fun get(any: Any): Immersive {
        val clazz = any::class.java
        var immersive = cacheMap[clazz]
        if (immersive == null || !immersive.isOwner(any)) {
            immersive = generateImmersive(any)
            cacheMap[clazz] = immersive
        }
        return immersive
    }

    /**
     * 获取基于 [Dialog] 的沉浸式对象。
     */
    fun get(activity: Activity, dialog: Dialog): Immersive {
        val clazz = dialog::class.java
        var immersive = cacheMap[clazz]
        if (immersive == null || !immersive.isOwner(dialog)) {
            immersive = Immersive.with(activity, dialog)
            cacheMap[clazz] = immersive
        }
        return immersive
    }

    fun remove(any: Any) {
        val clazz = any::class.java
        cacheMap.remove(clazz)
    }

    fun clear() {
        cacheMap.clear()
    }

    /**
     * 创建基于 [Activity] [Fragment] [DialogFragment] 的沉浸式对象。
     * 其类型时抛出异常 [ClassCastException]
     */
    private fun generateImmersive(any: Any): Immersive {
        return when (any) {
            is Activity -> {
                Immersive.with(any)
            }
            is DialogFragment -> {
                Immersive.with(any)
            }
            is Fragment -> {
                Immersive.with(any)
            }
            else -> {
                throw ClassCastException("${any::class.java} is not Activity, Fragment, DialogFragment")
            }
        }
    }

}