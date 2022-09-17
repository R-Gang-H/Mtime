package com.kotlin.android.ktx.ext.log

import android.util.Log
import com.kotlin.android.ktx.utils.LogUtils

/**
 * 日志输出类型
 */
enum class LogType {
    /**
     * 使用 [LogUtils] 输出日志
     */
    LOG_UTILS,

    /**
     * 使用 [Log] 输出日志（第一页，截断）
     */
    LOG,

    /**
     * 使用 [Log] 输出全部日志（即：分页）
     */
    LOG_FULL,
}

/**
 * 日志管理器
 *
 * Created on 2022/1/25.
 *
 * @author o.s
 */
object LogManager {
    private var globalTag = "APP_LOG"
    private var logTag: String? = null
    private var logType = LogType.LOG
    private val tagMap by lazy { HashMap<String, String>() }

    val LOG_TAG: String
        get() = logTag ?: globalTag

    val LOG_TYPE: LogType
        get() = logType

    /**
     * 初始化全局日志TAG
     */
    fun initGlobal(tag: String, logType: LogType) {
        globalTag = tag
        this.logType = logType
    }

    /**
     * 同步专属模块TAG
     */
    fun syncTag(className: String) {
        logTag = tagMap[className]
    }

    /**
     * 注册独立功能、独立模块、模块、子系统的专属日志TAG
     */
    fun register(className: String, tag: String) {
        tagMap[className] = tag
    }

    /**
     * 反注册专属日志TAG
     */
    fun unregister(className: String) {
        tagMap.remove(className)
    }

}
