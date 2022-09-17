package com.kotlin.android.ktx.ext.log

import com.kotlin.android.ktx.utils.LogUtils

/**
 * 日志方法栈格式化：
 *
 * Created on 2022/1/25.
 *
 * @author o.s
 */
object LogFormat {
    private const val MAX_LEN = 2100 // fit for Chinese character

    fun logFormat(any: Any?): String {
        return String.format(getMethodNames(), any ?: "")
    }

    fun logFormatFull(any: Any?): List<String> {
        val list = ArrayList<String>()
        val msg = if (any is String) {
            any
        } else {
            any.toString()
        }
        val len = msg.length
        val pageCount = if (len % MAX_LEN == 0) len / MAX_LEN else len / MAX_LEN + 1
        for (i in 0 until pageCount) {
            val currentPage = i + 1
            val endIndex = if (currentPage == pageCount) {
                len
            } else {
                currentPage * MAX_LEN
            }
            val subMsg = msg.substring(i * MAX_LEN, endIndex)
            list.add(String.format(getMethodNames(true), currentPage, pageCount, subMsg))
        }
        return list
    }

    /**
     * 是否需要显示页签
     */
    private fun getMethodNames(isPage: Boolean = false): String {
        val sElements = Thread.currentThread().stackTrace

        var stackOffset = getStackOffset(sElements)
        stackOffset++
        val className = sElements[stackOffset].className
        LogManager.syncTag(className)
        val builder = StringBuilder()
//    builder.append(" ").append("\n").append(LogUtils.TOP_BORDER).append("\r\n")
            // 添加当前线程名
            //.append("║ " + "Thread: " + Thread.currentThread().name).append("\r\n")
            //.append(MIDDLE_BORDER).append("\r\n")
            // 添加类名、方法名、行数
//        .append(LogUtils.HORIZONTAL_DOUBLE_LINE)
            .append("Class: ")
            .append(className)
            .append(".")
            .append(sElements[stackOffset].methodName)
            .append("(")
            .append(sElements[stackOffset].fileName)
            .append(":")
            .append(sElements[stackOffset].lineNumber)
            .append(")")
            // 页码
//        .append(" Page:")
//        .append("(%d/%d)")
//            .append("\r\n")
//        .append(LogUtils.MIDDLE_BORDER)
            // 添加打印的日志信息
//        .append("\n")
//        .append(LogUtils.HORIZONTAL_DOUBLE_LINE)
//        .append(" Log: ")
//            .append("%s")
//        .append("\n")
//        .append(LogUtils.BOTTOM_BORDER)
        if (isPage) {
            // 页码
            builder.append(" ♻️ Page:︎✔︎(%d/%d)")
        }
        // 添加打印的日志信息
        builder.append(" 🌹 %s")
        return builder.toString()
    }

    private fun getStackOffset(trace: Array<StackTraceElement>): Int {
        var i = 3
        while (i < trace.size) {
            val e = trace[i]
            val name = e.className
            if (
                name != LogUtils::class.java.name
                && name != "com.kotlin.android.ktx.ext.log.LogExtKt"
                && name != "com.kotlin.android.ktx.ext.log.LogFormat"
            ) {
                return --i
            }
            i++
        }
        return -1
    }
}