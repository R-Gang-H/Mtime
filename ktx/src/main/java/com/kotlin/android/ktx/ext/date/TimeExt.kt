package com.kotlin.android.ktx.ext.date

import com.kotlin.android.ktx.utils.LogUtils
import java.util.*

/**
 * TimeExt.kt
 * [Time] 扩展，日期时间格式化解析、输入输出扩展
 *
 * Created on 2021/3/12.
 *
 * @author o.s
 */

/**
 * 时间间隔解析，返回格式化时间对象 [Time]，
 * 当 [time] 不为 null 时，则更新输入的 [time]，
 * 当 [time] 为 null 时，则新建 [Time]
 *
 * 注：辅助倒计时等业务场景
 */
fun Int.timeIntervalToTime(time: Time? = null): Time {
    return toLong().timeIntervalToTime(time)
}

/**
 * [anchorTimestamp] 锚点时间戳，
 * 用于解析 [this] 相对于 [anchorTimestamp] 的时间差
 */
fun Long.timestampToTime(anchorTimestamp: Long = nowMillis): Time {
    return Time(this, anchorTimestamp)
}

/**
 * 时间间隔解析，返回格式化时间对象 [Time]，
 * 当 [time] 不为 null 时，则更新输入的 [time]，
 * 当 [time] 为 null 时，则新建 [Time]
 *
 * 注：辅助倒计时等业务场景
 */
fun Long.timeIntervalToTime(time: Time? = null): Time {
    return time?.syncTimeInterval(this) ?: Time(this)
}

fun Date.toTime(anchorDate: Date = nowDate): Time {
    return time.timestampToTime(anchorDate.time)
}

fun Date.toTime(anchorTimestamp: Long = nowMillis): Time {
    return time.timestampToTime(anchorTimestamp)
}

fun String.toTime(anchorTime: String, format: String = yyyy_MM_dd_HH_mm_ss, zone: String = TIME_ZONE): Time? {
    return anchorTime.toTimestamp(format, zone)?.let {
        toTimestamp(format, zone)?.timestampToTime(it)
    }
}

fun String.toTime(anchorTimestamp: Long = nowMillis, format: String = yyyy_MM_dd_HH_mm_ss, zone: String = TIME_ZONE): Time? {
    return toTimestamp(format, zone)?.timestampToTime(anchorTimestamp)
}

/**
 * 时光回复日期格式化输出
 */
fun Time.toStringWithMTime(): String {
    return StringBuilder().apply {
        when {
            totalMillisSecond < TimeUnit.SECOND.time ->  {
                append("刚刚")
            }
            totalMillisSecond < TimeUnit.MINUTE.time ->  {
                append(second)
                append("秒前")
            }
            totalMillisSecond < TimeUnit.HOUR.time ->  {
                append(minute)
                append("分钟前")
            }
            timestamp.isToday ->  {
                append("今天")
                append(hourDesc)
                append(":")
                append(minuteDesc)
            }
            timestamp.isYesterday ->  {
                append("昨天")
                append(hourDesc)
                append(":")
                append(minuteDesc)
            }
            else -> {
                append(timestamp.toStringWithDateFormat("yyyy-MM-dd"))
            }
        }
    }.toString()
}

/**
 * 输出格式化时间
 * 如：1天 08小时08分钟08秒、01天08小时
 *
 * 注：可扩展 [Time] 输出业务需要的时间格式
 */
fun Time.toStringByTimeUnit(timeUnit: TimeUnit): String {
    return StringBuilder().apply {
        if (day > 0) {
            append(day)
            append("天 ")
        }
        when (timeUnit) {
            TimeUnit.MINUTE -> {
                append(hourDesc)
                append("小时")
                append(minuteDesc)
                append("分钟")
            }
            TimeUnit.HOUR -> {
                append(hourDesc)
                append("小时")
            }
            TimeUnit.DAY -> {

            }
            else -> {
                append(hourDesc)
                append("小时")
                append(minuteDesc)
                append("分钟")
                append(secondDesc)
                append("秒")
            }
        }
    }.toString()
}