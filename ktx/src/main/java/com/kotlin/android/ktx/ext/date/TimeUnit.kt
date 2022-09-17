package com.kotlin.android.ktx.ext.date

/**
 * 日期时间单位：毫秒、秒钟、分钟、小时、天、周、月（30天）、年（365天）
 *
 * Created on 2021/3/9.
 *
 * @author o.s
 */
enum class TimeUnit(val time: Long) {
    MILLISECOND(1L), // 毫秒
    SECOND(1_000L), // 秒钟
    MINUTE(60_000L), // 分钟
    HOUR(3_600_000L), // 小时
    DAY(86_400_000L), // 天
    WEEK(604_800_000L), // 周
    MONTH(2_592_000_000L), // 月（30天）
    YEAR(31_536_000_000L), // 年（365天）
}