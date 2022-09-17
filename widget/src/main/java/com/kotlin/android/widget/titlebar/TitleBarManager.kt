package com.kotlin.android.widget.titlebar

import android.app.Activity
import androidx.fragment.app.Fragment
import java.util.concurrent.ConcurrentHashMap

/**
 * TitleBar管理器
 *
 * Created on 2021/12/10.
 *
 * @author o.s
 */
class TitleBarManager private constructor() {
    private val cacheMap by lazy { ConcurrentHashMap<Class<*>, TitleBar>() }

    companion object {
        val instance by lazy { TitleBarManager() }

        fun with(activity: Activity, themeStyle: ThemeStyle = ThemeStyle.STANDARD) = TitleBar(activity).of(themeStyle)
        fun with(fragment: Fragment, themeStyle: ThemeStyle = ThemeStyle.STANDARD) = TitleBar(fragment.requireContext()).of(themeStyle, fragment)
    }

    fun get(activity: Activity): TitleBar {
        val clazz = activity.javaClass
        var titleBar = cacheMap[clazz]
        if (titleBar == null) {
            titleBar = TitleBar(activity)
        }
        return titleBar
    }

    fun get(fragment: Fragment): TitleBar {
        val clazz = fragment.javaClass
        var titleBar = cacheMap[clazz]
        if (titleBar == null) {
            titleBar = TitleBar(fragment.requireContext())
        }
        return titleBar
    }
}