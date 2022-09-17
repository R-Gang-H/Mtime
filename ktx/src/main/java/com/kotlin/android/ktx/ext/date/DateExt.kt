package com.kotlin.android.ktx.ext.date

import android.content.Context
import com.kotlin.android.ktx.R
import java.text.SimpleDateFormat
import java.util.*

/**
 * 时间戳[Long]、日期[Date]、格式化日期[String]、时间间隔[timeInterval]等相关转换及格式输入输出等扩展（含时区设置）:
 * [TimeUnit] 时间单位枚举，毫秒，秒，分，时，天
 * [Time] "时间间隔"格式化类，辅助倒计时等场景处理时间显示问题。
 *
 * Created on 2021/3/9.
 *
 * @author o.s
 */

//enum class TimeUnit(val time: Long) {
//    MILLISECOND(1L),
//    SECOND(1_000L),
//    MINUTE(60_000L),
//    HOUR(3_600_000L),
//    DAY(86_400_000L), // 1天
//    WEEK(604_800_000L), // 7天
//    MONTH(2_592_000_000L), // 30天
//    YEAR(31_536_000_000L), // 365天
//}

//const val SS = 1L // ms
//const val ss = 1_000L // second
//const val mm = 60_000L // min 60*1000
//const val HH = 3600_000L // hour 60*60*1000
//const val dd = 86400_000L // day 24*60*60*1000
//
//@LongDef(SS, ss, mm, HH, dd)
//@Retention(AnnotationRetention.SOURCE)
//annotation class TimeUnit
//
const val TIME_ZONE = "GMT+8" // 默认东八区
const val yyyy_MM_dd_HH_mm_ss = "yyy-MM-dd HH:mm:ss" // 默认日期格式 "yyy-MM-dd HH:mm:ss"
const val HH_mm_ss = "HH:mm:ss"
const val yyyy_MM_dd = "yyyy-MM-dd"

const val M = 1000//one s ,unit:ms
const val ONE_HOUR = 3600//one hour ,unit:second
const val ONE_DAY = 24 * 3600//one day ,unit:second

//
//@StringDef(yyyy_MM_dd_HH_mm_ss, yyyy_MM_dd, HH_mm_ss)
//@Retention(AnnotationRetention.SOURCE)
//annotation class TimeFormat

/**
 * 东八区 Calendar
 */
val calendar: Calendar
    get() = run {
        val timeZone = TimeZone.getTimeZone(TIME_ZONE)
        TimeZone.setDefault(timeZone)
        Calendar.getInstance(timeZone)
    }

/**
 * 获取当天时间的小时
 * Field number for get and set indicating the hour of the day. HOUR_OF_DAY is used for the 24-hour clock. E.g., at 10:04:15.250 PM the HOUR_OF_DAY is 22
 */
val hour: Int
    get() {
        return calendar.get(Calendar.HOUR_OF_DAY)
    }

val year:Int
    get() {
        return calendar.get(Calendar.YEAR)
    }

/**
 * 今天凌晨00:00
 */
val weeOfToday: Long
    get() {
        return calendar.let {
            it[Calendar.HOUR_OF_DAY] = 0
            it[Calendar.MINUTE] = 0
            it[Calendar.SECOND] = 0
            it[Calendar.MILLISECOND] = 0
            it.timeInMillis
        }
    }

/**
 * 明天凌晨00:00
 */
val weeOfTomorrow: Long
    get() {
        return calendar.let {
            it[Calendar.HOUR_OF_DAY] = 24
            it[Calendar.MINUTE] = 0
            it[Calendar.SECOND] = 0
            it[Calendar.MILLISECOND] = 0
            it.timeInMillis
        }
    }

/**
 * 后天天凌晨00:00
 */
val weeOfAfterTomorrow: Long
    get() {
        return calendar.let {
            it[Calendar.HOUR_OF_DAY] = 48
            it[Calendar.MINUTE] = 0
            it[Calendar.SECOND] = 0
            it[Calendar.MILLISECOND] = 0
            it.timeInMillis
        }
    }

/**
 * 昨天凌晨00:00
 */
val weeOfYesterday: Long
    get() {
        return calendar.let {
            it[Calendar.HOUR_OF_DAY] = -24
            it[Calendar.MINUTE] = 0
            it[Calendar.SECOND] = 0
            it[Calendar.MILLISECOND] = 0
            it.timeInMillis
        }
    }

/**
 * 是否今天
 */
val Long.isToday: Boolean
    get() = this in weeOfToday until weeOfTomorrow

val Date.isToday: Boolean
    get() = time.isToday

fun String.isToday(
    format: String = yyyy_MM_dd_HH_mm_ss,
    zone: String = TIME_ZONE
): Boolean? {
    return toTimestamp(format, zone)?.isToday
}

/**
 * 是否明天
 */
val Long.isTomorrow: Boolean
    get() = this in weeOfTomorrow until weeOfTomorrow + TimeUnit.DAY.time

val Date.isTomorrow: Boolean
    get() = time.isTomorrow

fun String.isTomorrow(
    format: String = yyyy_MM_dd_HH_mm_ss,
    zone: String = TIME_ZONE
): Boolean? {
    return toTimestamp(format, zone)?.isTomorrow
}

/**
 * 是否后天
 */
val Long.isAfterTomorrow: Boolean
    get() = this in weeOfAfterTomorrow until weeOfAfterTomorrow + TimeUnit.DAY.time

val Date.isAfterTomorrow: Boolean
    get() = time.isAfterTomorrow

fun String.isAfterTomorrow(
    format: String = yyyy_MM_dd_HH_mm_ss,
    zone: String = TIME_ZONE
): Boolean? {
    return toTimestamp(format, zone)?.isAfterTomorrow
}

/**
 * 是否昨天
 */
val Long.isYesterday: Boolean
    get() = this in weeOfYesterday until weeOfToday

val Date.isYesterday: Boolean
    get() = time.isYesterday

fun String.isYesterday(
    format: String = yyyy_MM_dd_HH_mm_ss,
    zone: String = TIME_ZONE
): Boolean? {
    return toTimestamp(format, zone)?.isYesterday
}

val nowMillis
    get() = System.currentTimeMillis()

val nowDate
    get() = Date()

fun nowString(
    format: String = yyyy_MM_dd_HH_mm_ss,
    zone: String = TIME_ZONE
): String {
    return nowMillis.toStringWithDateFormat(format, zone)
}

/**
 * 闰年 year
 */
fun Int.isLeapYear(): Boolean {
    return this % 4 == 0 && this % 100 != 0 || this % 400 == 0
}

/**
 * 闰年 timestamp
 */
fun Long.isLeapYear(): Boolean {
    return date.isLeapYear()
}

/**
 * 闰年 Date
 */
fun Date.isLeapYear(): Boolean {
    return calendar.let {
        it.time = this
        it[Calendar.YEAR].isLeapYear()
    }
}

/**
 * 闰年 String 日期
 */
fun String.isLeapYear(
    format: String,
    zone: String = TIME_ZONE
): Boolean {
    return toDate(format, zone)?.isLeapYear() ?: false
}

/**
 * 获取指定时区，日期格式的日期格式化对象
 * [format] "yyy-MM-dd HH:mm:ss"
 * [zone] "GMT+8"
 */
fun getDateFormat(
    format: String,
    zone: String = TIME_ZONE
): SimpleDateFormat {
    return SimpleDateFormat(format, Locale.CHINA).apply {
        timeZone = TimeZone.getTimeZone(zone)
    }
}

/**
 * 时间戳Long 转 日期Date
 */
val Long.date
    get() = Date(this)

/**
 * 日期字符串String（指定格式化参数） 转 日期Date
 */
fun String.toDate(
    format: String,
    zone: String = TIME_ZONE
): Date? {
    return try {
        getDateFormat(format, zone).parse(this)
    } catch (e: Throwable) {
        null
    }
}

/**
 * 日期字符串String（指定格式化参数） 转 时间戳Long
 */
fun String.toTimestamp(
    format: String,
    zone: String = TIME_ZONE
): Long? {
    return toDate(format, zone)?.time
}

/**
 * 时间戳格式化日期输出
 */
fun Long.toStringWithDateFormat(
    format: String,
    zone: String = TIME_ZONE
): String {
    return date.toStringWithDateFormat(format, zone) // getDateFormat(format, zone).format(date)
}
/**
 * 时间戳格式化日期输出
 */
fun timeToString(
    time:Long,
    format: String
): String {
    return Date(time).toStringWithDateFormat(format, TIME_ZONE) // getDateFormat(format, zone).format(date)
}

/**
 * Date 格式化日期输出
 */
fun Date.toStringWithDateFormat(
    format: String,
    zone: String = TIME_ZONE
): String {
    return getDateFormat(format, zone).format(this)
}

/**
 * 将 ms 转为指定单位时间
 */
fun Long.toTimeUnit(timeUnit: TimeUnit = TimeUnit.SECOND): Long {
    return this / timeUnit.time
}

/**
 * 将指定单位时间转为 ms
 */
fun Long.toMillis(timeUnit: TimeUnit = TimeUnit.SECOND): Long {
    return this * timeUnit.time
}

/**
 * 将 ms 转为指定单位时间
 */
fun Int.toTimeUnit(timeUnit: TimeUnit = TimeUnit.SECOND): Long {
    return this / timeUnit.time
}

/**
 * 将指定单位时间转为 ms
 */
fun Int.toMillis(timeUnit: TimeUnit = TimeUnit.SECOND): Long {
    return this * timeUnit.time
}

/**
 * Date -> Date
 * 增加指定的 [timeInterval] 时间间隔 [TimeUnit]（可负数），
 * 为负数时表示日期之前
 */
fun Date.addTimeInterval(
    timeInterval: Long,
    timeUnit: TimeUnit = TimeUnit.MILLISECOND
): Date {
    return (time + timeInterval * timeUnit.time).date
}

/**
 * Long -> Long
 * 增加指定的 [timeInterval] 时间间隔 [TimeUnit]（可负数），
 * 为负数时表示日期之前
 */
fun Long.addTimeInterval(
    timeInterval: Long,
    timeUnit: TimeUnit = TimeUnit.MILLISECOND
): Long {
    return this + timeInterval * timeUnit.time
}

/**
 * String -> String
 * 增加指定的 [timeInterval] 时间间隔 [TimeUnit]（可负数），
 * 为负数时表示日期之前
 */
fun String.addTimeInterval(
    timeInterval: Long,
    timeUnit: TimeUnit = TimeUnit.MILLISECOND,
    format: String = yyyy_MM_dd_HH_mm_ss,
    zone: String = TIME_ZONE
): String? {
    return this.toDate(format, zone)?.time?.let {
        (it + timeInterval * timeUnit.time).toStringWithDateFormat(format, zone)
    }
}

/**
 * 时间间隔：两个（目标）日期差值
 */
fun Date.timeInterval(
    anchorDate: Date,
    timeUnit: TimeUnit = TimeUnit.MILLISECOND
): Long {
    return (time - anchorDate.time).toTimeUnit(timeUnit)
}

/**
 * 时间间隔：两个（目标）时间戳差值
 */
fun Long.timeInterval(
    anchorTimestamp: Long,
    timeUnit: TimeUnit = TimeUnit.MILLISECOND
): Long {
    return (this - anchorTimestamp).toTimeUnit(timeUnit)
}

/**
 * 时间间隔：两个（目标）日期格式时间差值
 */
fun String.timeInterval(
    anchorTime: String,
    timeUnit: TimeUnit = TimeUnit.MILLISECOND,
    format: String = yyyy_MM_dd_HH_mm_ss,
    zone: String = TIME_ZONE
): Long? {
    val anchorDate = anchorTime.toDate(format, zone)
    val date = toDate(format, zone)
    return if (anchorDate != null && date != null) {
        date.timeInterval(anchorDate, timeUnit)
    } else {
        null
    }
}

fun Date.timeIntervalByNow(
    timeUnit: TimeUnit = TimeUnit.MILLISECOND
): Long {
    return timeInterval(nowDate, timeUnit)
}

fun Long.timeIntervalByNow(
    timeUnit: TimeUnit = TimeUnit.MILLISECOND
): Long {
    return timeInterval(nowMillis, timeUnit)
}

fun String.timeIntervalByNow(
    timeUnit: TimeUnit = TimeUnit.MILLISECOND,
    format: String = yyyy_MM_dd_HH_mm_ss,
    zone: String = TIME_ZONE
): Long? {
    return toDate(format, zone)?.timeInterval(nowDate, timeUnit)
}

fun Date.week(locale: Locale = Locale.CHINA): String {
    return SimpleDateFormat("EEEE", locale).apply {
        timeZone = TimeZone.getTimeZone(TIME_ZONE)
    }.format(this)
}

fun Long.week(locale: Locale = Locale.CHINA): String {
    return date.week(locale)
}

fun String.week(
    locale: Locale = Locale.CHINA,
    format: String = yyyy_MM_dd_HH_mm_ss,
    zone: String = TIME_ZONE
): String? {
    return toDate(format, zone)?.week(locale)
}

fun Date.isAM(): Boolean {
    return calendar.let {
        it.time = this
        it[Calendar.AM_PM] == 0
    }
}

fun Date.isPM(): Boolean {
    return calendar.let {
        it.time = this
        it[Calendar.AM_PM] != 0
    }
}

fun Long.isAM(): Boolean {
    return date.isAM()
}

fun Long.isPM(): Boolean {
    return date.isPM()
}

fun String.isAM(
    format: String = yyyy_MM_dd_HH_mm_ss,
    zone: String = TIME_ZONE
): Boolean {
    return toDate(format, zone)?.isAM() ?: false
}

fun String.isPM(
    format: String = yyyy_MM_dd_HH_mm_ss,
    zone: String = TIME_ZONE
): Boolean {
    return toDate(format, zone)?.isPM() ?: false
}

private val WEEKS_CN = arrayOf(
    "周六", "周日", "周一", "周二", "周三", "周四", "周五"
)

private val MONTHS_EN = arrayOf(
    "JAN", "FEB", "MAR", "APR", "MAY", "JUN",
    "JUL", "AUG", "SEPT", "OCT", "NOV", "DEC"
)

private val MONTHS_En = arrayOf(
    "Jan", "Feb", "Mar", "Apr", "May", "Jun",
    "Jul", "Aug", "Sept", "Oct", "Nov", "Dec"
)
private val MONTHS_BIG = arrayOf(
    "一", "二", "三", "四", "五", "六",
    "七", "八", "九", "十", "十一", "十二"
)
private val CHINESE_ZODIAC = arrayOf("猴", "鸡", "狗", "猪", "鼠", "牛", "虎", "兔", "龙", "蛇", "马", "羊")
private val ZODIAC_FLAGS = intArrayOf(20, 19, 21, 21, 21, 22, 23, 23, 23, 24, 23, 22)
private val ZODIAC = arrayOf(
    "水瓶座", "双鱼座", "白羊座", "金牛座", "双子座", "巨蟹座",
    "狮子座", "处女座", "天秤座", "天蝎座", "射手座", "魔羯座"
)

/**
 * 周中的第几天
 */
val Date.dayOfWeek: Int
    get() = calendar.let {
        it.time = this
        it[Calendar.DAY_OF_WEEK]
    }

/**
 * 月中的第几天
 */
val Date.dayOfMonth: Int
    get() = calendar.let {
        it.time = this
        it[Calendar.DAY_OF_MONTH]
    }

/**
 * 周几CN
 */
val Date.weekCN: String
    get() = calendar.let {
        it.time = this
        WEEKS_CN[it[Calendar.DAY_OF_WEEK]]
    }

/**
 * 周几CN（今天，明天，周几）//，后天
 */
val Date.weekCN2: String
    get() = calendar.let {
        it.time = this
        when {
            isToday -> "今天"
            isTomorrow -> "明天"
//            isAfterTomorrow -> "后天"
            else -> WEEKS_CN[it[Calendar.DAY_OF_WEEK]]
        }
    }

/**
 * 周几CN（今天，明天
 */
val Date.weekCN3: String
    get() = calendar.let {
        it.time = this
        when {
            isToday -> "今天"
            isTomorrow -> "明天"
            else -> ""
        }
    }

/**
 * 月份EN
 */
val Date.monthEN: String
    get() = calendar.let {
        it.time = this
        MONTHS_EN[it[Calendar.MONTH]]
    }

/**
 * 月份En
 */
val Date.monthEn: String
    get() = calendar.let {
        it.time = this
        MONTHS_En[it[Calendar.MONTH]]
    }

/**
 * 生肖
 */
val Date.chineseZodiac: String
    get() = calendar.let {
        it.time = this
        CHINESE_ZODIAC[it[Calendar.YEAR] % 12]
    }

/**
 * 星座
 */
fun zodiac(month: Int, day: Int): String {
    return ZODIAC[if (day >= ZODIAC_FLAGS[month - 1]) month - 1 else (month + 10) % 12]
}

/**
 * 当前年
 */
fun getCurrentYear():Int{
    return year
}

/**
 * 月份 一、二
 */
val Date.monthBig: String
    get() = calendar.let {
        it.time = this
        MONTHS_BIG[it[Calendar.MONTH]]
    }
