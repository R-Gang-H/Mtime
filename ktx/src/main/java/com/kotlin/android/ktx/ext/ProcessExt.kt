package com.kotlin.android.ktx.ext

import android.app.ActivityManager
import android.app.Application
import android.content.Context
import android.os.Build
import android.os.Process.myPid
import android.text.TextUtils
import java.lang.reflect.Method

/**
 * 创建者: zl
 * 创建时间: 2020/11/10 8:57 上午
 * 描述:获取进程名称扩展类
 * 优先通过 Application.getProcessName() 方法获取进程名。
 * 如果获取失败，再反射ActivityThread.currentProcessName()获取进程名
 */
/**
 * @return 当前进程名
 */
fun getCurrentProcessName(): String {
    //1)通过Application的API获取当前进程名
    var currentProcessName = getCurrentProcessNameByApplication()
    if (!TextUtils.isEmpty(currentProcessName)) {
        return currentProcessName
    }

    //2)通过反射ActivityThread获取当前进程名
    currentProcessName = getCurrentProcessNameByActivityThread()
    if (!TextUtils.isEmpty(currentProcessName)) {
        return currentProcessName
    }

    return ""
}

/**
 * 通过Application新的API获取进程名，无需反射，无需IPC，效率最高。
 */
private fun getCurrentProcessNameByApplication(): String {
    return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.P) {
        Application.getProcessName()
    } else ""
}

/**
 * 通过反射ActivityThread获取进程名，避免了ipc
 */
private fun getCurrentProcessNameByActivityThread(): String {
    var processName: String? = null
    try {
        val declaredMethod: Method = Class.forName("android.app.ActivityThread",
                false, Application::class.java.classLoader)
                .getDeclaredMethod("currentProcessName", *arrayOfNulls<Class<*>?>(0))
        declaredMethod.isAccessible = true
        val invoke: Any = declaredMethod.invoke(null, arrayOfNulls<Any>(0))
        if (invoke is String) {
            processName = invoke
        }
    } catch (e: Throwable) {
        e.printStackTrace()
    }
    return processName ?: ""
}