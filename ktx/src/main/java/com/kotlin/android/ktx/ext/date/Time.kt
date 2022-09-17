package com.kotlin.android.ktx.ext.date

import kotlin.math.abs

/**
 * 时间戳（Long）、时间间隔（Long）的格式化输出对象。
 * 辅助用于倒计时、日期格式化展示（评论/回复）等业务展示。
 * [Time.timestamp] 时间戳（ms）
 * [Time.anchorTimestamp] 锚定时间（ms），如相对当前时间即：[Time.anchorTimestamp] = [nowMillis]
 * [Time.timeInterval] 时间间隔（ms）：等同于 [Time.timestamp] 基于 [Time.anchorTimestamp] 时间的差值即：[Time.timestamp] - [Time.anchorTimestamp]
 *
 * Created on 2021/3/9.
 *
 * @author o.s
 */
class Time {

    /**
     * 日期时间间隔：时间差值，通常取自于 [timestamp] - [anchorTimestamp]
     */
    constructor(timeInterval: Long) {
        this.timeInterval = timeInterval
        internalSyncTime()
    }

    /**
     * [timestamp] 时间戳（1970）
     * [anchorTimestamp] 锚点时间戳（1970）：一般取自当前时间 [nowMillis]
     */
    constructor(timestamp: Long, anchorTimestamp: Long) {
        this.timestamp = timestamp
        this.anchorTimestamp = anchorTimestamp
        internalSyncTime()
    }

    /**
     * 内部时间间隔
     */
    private var internalTimeInterval: Long = 0

    var timeInterval: Long = 0
        private set
    var timestamp: Long = 0
        private set
    var anchorTimestamp: Long = nowMillis
        private set

    /**
     * -1：过去，1：未来，0：现在
     */
    var timeState: Int = 0
        private set

    /**
     * 时间间隔总毫秒
     */
    var totalMillisSecond: Long = 0
        private set
    var totalSecond: Long = 0
        private set
    var totalMinute: Long = 0
        private set
    var totalHour: Long = 0
        private set

    /**
     * 时间间隔：毫秒余数 = totalMillisSecond % 1000L
     */
    var millisecond: Long = 0
        private set
    var second: Long = 0
        private set
    var minute: Long = 0
        private set
    var hour: Long = 0
        private set

    /**
     * 时间（日期）间隔总天数
     */
    var day: Long = 0
        private set
    var week: Long = 0
        private set
    var month: Long = 0
        private set
    var year: Long = 0
        private set

    /**
     * 时间格式化输出
     */
    var hourDesc: String = "00"
        private set
        get() {
            return if (hour < 10) {
                "0$hour"
            } else {
                hour.toString()
            }
        }
    var minuteDesc: String = "00"
        private set
        get() {
            return if (minute < 10) {
                "0$minute"
            } else {
                minute.toString()
            }
        }
    var secondDesc: String = "00"
        private set
        get() {
            return if (second < 10) {
                "0$second"
            } else {
                second.toString()
            }
        }

    /**
     * 同步时间间隔：
     * 重复利用当前对象解析时间间隔。（无需反复构造新的 [Time] 对象）
     * 应用在倒计时场景，高频次解析时间间隔
     */
    fun syncTimeInterval(
        timeInterval: Long
    ): Time {
        this.timeInterval = timeInterval
        this.timestamp = 0
        this.anchorTimestamp = 0

        internalSyncTime()
        return this
    }

    /**
     * 同步时间戳：
     * 重复利用当前对象解析时间戳。（无需反复构造新的 [Time] 对象）
     * 应用在评论/回复日期场景等，高频次解析时间间隔
     */
    fun syncTimestamp(
        timestamp: Long,
        anchorTimestamp: Long = nowMillis
    ): Time {
        this.timeInterval = 0
        this.timestamp = timestamp
        this.anchorTimestamp = anchorTimestamp

        internalSyncTime()
        return this
    }

    /**
     * 内部同步时间
     */
    private fun internalSyncTime() {
        reset()

        if (timeInterval != 0L) {
            internalTimeInterval = timeInterval
        }
        if (timestamp != 0L) {
            internalTimeInterval = timestamp - anchorTimestamp
        }

        timeState = when {
            internalTimeInterval < 0L -> -1 // 过去
            internalTimeInterval > 0L -> 1 // 未来
            else -> 0 // 现在
        }

        internalTimeInterval = abs(internalTimeInterval)

        if (internalTimeInterval > 0) {
            parserTimeInterval()
        }
        if (timestamp > 0) {
            parserTimestamp()
        }
    }

    /**
     * 解析时间间隔
     */
    private fun parserTimeInterval() {
        totalMillisSecond = internalTimeInterval
        totalSecond = internalTimeInterval / TimeUnit.SECOND.time // 总秒
        totalMinute = internalTimeInterval / TimeUnit.MINUTE.time // 总分
        totalHour = internalTimeInterval / TimeUnit.HOUR.time // 总时
        millisecond = totalMillisSecond % 1000L // 秒
        second = totalSecond % 60L // 秒
        minute = totalMinute % 60L // 分
        hour = totalHour % 24L // 时
        day = internalTimeInterval / TimeUnit.DAY.time // 总天
        week = internalTimeInterval / TimeUnit.WEEK.time // 总周（/7天）
        month = internalTimeInterval / TimeUnit.MONTH.time // 总月（/30天）
        year = internalTimeInterval / TimeUnit.YEAR.time // 总年（/365天）
    }

    /**
     * 解析时间戳
     */
    private fun parserTimestamp() {

    }

    private fun reset() {
        internalTimeInterval = 0
        totalMillisSecond = 0
        totalSecond = 0
        totalMinute = 0
        totalHour = 0
        millisecond = 0
        second = 0
        minute = 0
        hour = 0
        day = 0
        week = 0
        month = 0
        year = 0
    }
}