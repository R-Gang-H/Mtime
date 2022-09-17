package com.kotlin.android.ktx.lifecycle

import android.app.Activity
import android.os.Build
import com.kotlin.android.ktx.ext.log.v
import java.lang.ref.WeakReference
import java.util.*
import kotlin.system.exitProcess

/**
 *
 * Created on 2020/6/4.
 *
 * @author o.s
 */
object KtxActivityManager {

    /**
     * activity任务栈增删操作相对频繁
     */
    private val activities = Stack<WeakReference<Activity>>()

    // 栈顶Activity?（多任务栈问题待解决）
    val topActivity: Activity?
        get() = if (activities.isEmpty()) {
            null
        } else {
            activities.last().get()
        }

    fun push(activity: Activity) {
        activities.push(WeakReference(activity))
        "push :: ${activity.javaClass.simpleName} :: <Stack> -> ${activities.map { it.get()?.javaClass?.simpleName }}".v()
    }

    fun pop(activity: Activity) {
        activities.removeWeak(activity)
        "pop :: ${activity.javaClass.simpleName} :: <Stack> -> ${activities.map { it.get()?.javaClass?.simpleName }}".v()
    }

    fun containsByName(clazzName: String): Boolean {
        return activities.find { it.get()?.javaClass?.name == clazzName } != null
    }

    fun finish(activity: Activity) {
        pop(activity)
        activity.finish()
    }

    fun finishAll() {
        activities.indices.reversed()
            .map { activities[it] }
            .forEach {
                it.get()?.run {
                    if (!isFinishing) finish()
                }
            }
        activities.clear()
    }

    fun finishTopActivity() {
        topActivity?.apply {
            pop(this)
            finish()
        }
    }

    fun exitApp() {
        try {
            finishAll()
            // app退出时，在前台，杀死后台进程不可取。使用killBackgroundProcesses()强制关闭与该包有关联的一切执行，该方法只能他杀；
//            activityManager?.killBackgroundProcesses(packageName)
            // 使用android.os.Process.myPid()方法获取当前进程的ID，然后使用 android.os.Process.killProcess()杀死该进程，该方法只能自杀；
            android.os.Process.killProcess(android.os.Process.myPid())
            // 使用System.exit()方法终止当前正在运 行的Java虚拟机，实现程序终止；
            exitProcess(0)
        } catch (e: Throwable) {
            exitProcess(1)
        } finally {
            System.gc()
        }
    }
}

fun <T> Stack<WeakReference<T>>.removeWeak(element: T) {
    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
        removeIf { it.get() == element }
    } else {
        var index = -1
        forEachIndexed { i, weak ->
            if (weak.get() == element) {
                index = i
            }
        }
        if (index >= 0) {
            removeAt(index)
        }
    }
}