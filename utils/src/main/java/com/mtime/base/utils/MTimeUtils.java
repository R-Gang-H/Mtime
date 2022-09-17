package com.mtime.base.utils;

import android.text.TextUtils;

import java.sql.Timestamp;
import java.text.ParseException;
import java.text.ParsePosition;
import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.concurrent.TimeUnit;

public class MTimeUtils {
    private static final long MSEC_OF_ONE_DAY = 86400000L;
    private static final long nMSEC_OF_ONE_HOUR = 3600000L;
    private static final long MSEC_OF_ONE_MINUTE = 60000L;
    private static final long MSEC_OF_ONE_SEC = 1000L;
    public static final Date START = new Date(0L);
    public static final Date END = new Date(9223372036854775807L);
    public static final Date NEXT_CENTURY = parse("2100-01-01 00:00:00");
    public static final int START_YEAR = 1970;
    public static final int FIRST_LEAP_YEAR = 1972;
    public static final String YMD_HMSS = "yyyy-MM-dd HH:mm:ss.SSS";
    public static final String YMD_HMSS2 = "yyyyMMddHHmmssSSS";
    public static final String YMD_ZH = "yyyy年MM月dd日";
    public static final String YM_ZH = "yyyy年MM月";
    public static final String YMD_HMS = "yyyy-MM-dd HH:mm:ss";
    public static final String YMDHMS_URL = "yyyy-MM-dd+HH:mm:ss";
    public static final String Y2MD_HMS = "yy-MM-dd HH:mm:ss";
    public static final String YMD_HM = "yyyy-MM-dd HH:mm";
    public static final String Y2MD_HM = "yy-MM-dd HH:mm";
    public static final String YMD = "yyyy-MM-dd";
    public static final String YMD_DOT = "yyyy.MM.dd";
    public static final String YMD_H = "yyyy-MM-dd-HH";
    public static final String MD = "MM-dd";
    public static final String MD3 = "MM月dd日";
    public static final String YM = "yyyy-MM";
    public static final String HM = "HH:mm";
    public static final String MD2 = "M.d";
    public static final String YMD2 = "yyyyMMdd";
    public static final String HM2 = "HHmm";
    private static final String defaultType = "yyyy-MM-dd HH:mm:ss";
    public static final long SECOND;
    public static final long MINUTE;
    public static final long HOUR;
    public static final long HOUR8;
    public static final long HOUR16;
    public static final long DATE;
    public static final long DAY;
    public static final long WEEK;
    public static final long DAY7;
    public static final long DAY28;
    public static final long DAY29;
    public static final long DAY30;
    public static final long DAY31;
    public static final long DAY365;
    public static final long DAY366;
    public static final long YEAR_COMMON;
    public static final long YEAR2;
    public static final long YEAR_LEAP;
    public static final long YEAR4;
    public static final long LEAP_DAY_START_IN_4Y;
    public static final long LEAP_DAY_END_IN_4Y;

    public MTimeUtils() {
    }

    private static SimpleDateFormat getFmt(String type) {
        return new SimpleDateFormat(type);
    }

    public static String format(String type, Date date) {
        return getFmt(type).format(date);
    }

    public static String format(Date date) {
        return format("yyyy-MM-dd HH:mm:ss", date);
    }

    public static String format(String type, long date) {
        return format(type, new Date(date));
    }

    public static String format(long date) {
        return format("yyyy-MM-dd HH:mm:ss", new Date(date));
    }

    public static String formatSecond(int seconds) {
        StringBuffer sb = new StringBuffer();
        int hours = seconds / 3600;
        sb.append(String.format("%02d:", Integer.valueOf(hours)));
        seconds -= hours * 3600;
        int minutes = seconds / 60;
        sb.append(String.format("%02d:", Integer.valueOf(minutes)));
        seconds -= minutes * 60;
        sb.append(String.format("%02d", Integer.valueOf(seconds)));
        return sb.toString();
    }

    public static Date parse(String s) {
        s = s.trim();
        SimpleDateFormat format = new SimpleDateFormat();
        TimeFormatter[] arr$ = TimeFormatter.values();
        int len$ = arr$.length;

        for (int i$ = 0; i$ < len$; ++i$) {
            TimeFormatter f = arr$[i$];
            format.applyPattern(f.pattern);
            Date d = format.parse(s, new ParsePosition(0));
            if (d != null) {
                return d;
            }
        }

        return null;
    }

    public static Date getNow() {
        return new Date();
    }

    public static boolean afterNow(Date d) {
        return d.after(new Date());
    }

    public static boolean afterNow(Timestamp d) {
        return d.after(new Date());
    }

    public static boolean beforeNow(Date d) {
        return d.before(new Date());
    }

    public static boolean beforeNow(Timestamp d) {
        return d.before(new Date());
    }

    public static Date getDateTimeAfter(Date _from, int _time_type, int _count) {
        Calendar c = Calendar.getInstance();
        if (_from != null) {
            c.setTime(_from);
        }

        c.add(_time_type, _count);
        return c.getTime();
    }

    public static int getDay(Date date) {
        Calendar c = Calendar.getInstance();
        c.setTime(date);
        return c.get(Calendar.DAY_OF_MONTH);
    }

    public static int getHour(Date date) {
        Calendar c = Calendar.getInstance();
        c.setTime(date);
        return c.get(Calendar.HOUR_OF_DAY);
    }

    public static int getMinute(Date date) {
        Calendar c = Calendar.getInstance();
        c.setTime(date);
        return c.get(Calendar.MINUTE);
    }

    public static int getSecond(Date date) {
        Calendar c = Calendar.getInstance();
        c.setTime(date);
        return c.get(Calendar.SECOND);
    }

    public static Date getDateAfter(Date _from, int _days) {
        return getDateTimeAfter(_from, 5, _days);
    }

    public static Date getMonthAfter(Date _from, int _mons) {
        return getDateTimeAfter(_from, 2, _mons);
    }

    public static Date getHourAfter(Date _from, int _hours) {
        return getDateTimeAfter(_from, 11, _hours);
    }

    public static Date getMillisecondAfter(Date _from, int _milliseconds) {
        return getDateTimeAfter(_from, 14, _milliseconds);
    }

    public static boolean timeIsIn(Date time, Date start, Date end) {
        return time.after(start) && time.before(end);
    }

    public static boolean nowIsIn(Date start, Date end) {
        return timeIsIn(new Date(), start, end);
    }

    public static boolean isTheSameDay(Date day1, Date day2, boolean escapeYear) {
        return isTheSameDay(day1, day2, escapeYear, true);
    }

    public static boolean isTheSameDay(Date day1, Date day2, boolean escapeYear, boolean checkSummerTime) {
        return checkSummerTime ? isTheSameDayCheckSummerTime(day1, day2, escapeYear)
            : isTheSameDayNoSummerTimeCheck(day1, day2, escapeYear);
    }

    private static boolean isTheSameDayCheckSummerTime(Date day1, Date day2, boolean escapeYear) {
        Calendar c = Calendar.getInstance();
        c.setTime(day1);
        int y = c.get(1);
        int m = c.get(2);
        int d = c.get(5);
        c.setTime(day2);
        return (escapeYear || y == c.get(1)) && m == c.get(2) && d == c.get(5);
    }

    private static boolean isTheSameDayNoSummerTimeCheck(Date day1, Date day2, boolean escapeYear) {
        long t1 = day1.getTime();
        long t2 = day2.getTime();
        long t81;
        long t82;
        if (!escapeYear) {
            t81 = t1 / HOUR8;
            t82 = t2 / HOUR8;
            return t81 == t82 || (t81 + 1L) / 3L == (t82 + 1L) / 3L;
        } else {
            t81 = (t1 + HOUR8) % YEAR4;
            t82 = (t2 + HOUR8) % YEAR4;
            return t81 < LEAP_DAY_START_IN_4Y
                ? (t82 < LEAP_DAY_START_IN_4Y ? t81 % YEAR_COMMON / DAY == t82 % YEAR_COMMON / DAY
                    : (t82 >= LEAP_DAY_END_IN_4Y && t81 % YEAR_COMMON / DAY == (t82 - DAY) % YEAR_COMMON / DAY))
                : (t81 >= LEAP_DAY_END_IN_4Y
                    ? (t82 < LEAP_DAY_START_IN_4Y ? (t81 - DAY) % YEAR_COMMON / DAY == t82 % YEAR_COMMON / DAY
                        : (t82 >= LEAP_DAY_END_IN_4Y && t81 % YEAR_COMMON / DAY == t82 % YEAR_COMMON / DAY))
                    : t82 >= LEAP_DAY_START_IN_4Y && t82 < LEAP_DAY_END_IN_4Y);
        }
    }

    public static boolean isToday(Date thatDay) {
        return isTheSameDay(new Date(), thatDay, false);
    }

    public static boolean isTheSameDay(Date day1, Date day2) {
        return format("yyyy-MM-dd", day1).equals(format("yyyy-MM-dd", day2));
    }

    public static boolean isTheSameYear(Date date1, Date date2) {
        Calendar c = Calendar.getInstance();
        c.setTime(date1);
        int year1 = c.get(1);
        c.setTime(date2);
        int year2 = c.get(1);
        return year1 == year2;
    }

    public static String latelyDaysToString(Date d) {
        return latelyDaysToString(d, false);
    }

    public static String latelyDaysToString(Date d, boolean escapeYear) {
        Calendar c = Calendar.getInstance();
        if (isTheSameDay(new Date(), d, escapeYear)) {
            return "今天";
        } else {
            c.add(5, -2);
            if (isTheSameDay(d, c.getTime(), escapeYear)) {
                return "前天";
            } else {
                c.add(5, 1);
                if (isTheSameDay(d, c.getTime(), escapeYear)) {
                    return "昨天";
                } else {
                    c.add(5, 2);
                    if (isTheSameDay(d, c.getTime(), escapeYear)) {
                        return "明天";
                    } else {
                        c.add(5, 1);
                        return isTheSameDay(d, c.getTime(), escapeYear) ? "后天"
                            : (escapeYear ? format("MM-dd", d) : format("yyyy-MM-dd", d));
                    }
                }
            }
        }
    }

    public static boolean isGregorianLeapYear(int year) {
        return (year & 3) == 0 && (year <= 1582 ? year < 1582 : year % 25 != 0 || year % 16 == 0);
    }

    public static int dateOf366(Date d) {
        Calendar c = Calendar.getInstance();
        c.setTime(d);
        int rt = c.get(6);
        return isGregorianLeapYear(c.get(1)) ? rt : (c.get(2) < 2 ? rt : rt + 1);
    }

    public static int dateToInt(Date d) {
        Calendar c = Calendar.getInstance();
        c.setTime(d);
        return (c.get(2) + 1) * 100 + c.get(5);
    }

    public static boolean isSameMonth(Date date1, Date date2) {
        Calendar cal1 = Calendar.getInstance();
        cal1.setTime(date1);
        Calendar cal2 = Calendar.getInstance();
        cal2.setTime(date2);
        return cal1.get(1) == cal2.get(1) && cal1.get(2) == cal2.get(2);
    }

    public static boolean near(Calendar basic, Calendar compare, int beforeDays, int afterDays) {
        long sub = basic.getTimeInMillis() - compare.getTimeInMillis();
        long before = DAY * (long) beforeDays;
        long after = DAY * (long) afterDays;
        if (sub < 0L) {
            if (-sub <= after) {
                return true;
            } else {
                compare.add(1, -1);
                sub = basic.getTimeInMillis() - compare.getTimeInMillis();
                return sub <= before;
            }
        } else if (sub <= before) {
            return true;
        } else {
            compare.add(1, 1);
            sub = compare.getTimeInMillis() - basic.getTimeInMillis();
            return sub <= after;
        }
    }

    public static boolean near(Calendar basic, int compareMonth, int compareDay, int beforeDays, int afterDays) {
        Calendar compare = Calendar.getInstance();
        compare.set(2, compareMonth - 1);
        compare.set(5, compareDay);
        compare.set(11, 0);
        compare.set(12, 0);
        compare.set(13, 0);
        compare.set(14, 0);
        return near(basic, compare, beforeDays, afterDays);
    }

    public static boolean near(int basicMonth, int basicDay, int compareMonth, int compareDay, int beforeDays,
        int afterDays) {
        Calendar basic = Calendar.getInstance();
        basic.set(2, basicMonth - 1);
        basic.set(5, basicDay);
        basic.set(11, 0);
        basic.set(12, 0);
        basic.set(13, 0);
        basic.set(14, 0);
        Calendar compare = Calendar.getInstance();
        compare.set(2, compareMonth - 1);
        compare.set(5, compareDay);
        compare.set(11, 0);
        compare.set(12, 0);
        compare.set(13, 0);
        compare.set(14, 0);
        return near(basic, compare, beforeDays, afterDays);
    }

    public static Date getDateWithoutTime(Calendar calendar) {
        calendar.set(11, 0);
        calendar.set(12, 0);
        calendar.set(13, 0);
        calendar.set(14, 0);
        return calendar.getTime();
    }

    public static long todayStart() {
        Calendar calendar = Calendar.getInstance();
        calendar.set(11, 0);
        calendar.set(12, 0);
        calendar.set(13, 0);
        calendar.set(14, 0);
        return calendar.getTimeInMillis();
    }

    public static Date getTodayStart() {
        return new Date(todayStart());
    }

    public static int leapYearCount(int year) {
        if (year < 1970) {
            throw new IllegalArgumentException("year < 1970:" + year);
        } else {
            return year < 1972 ? 0 : (year - 1972) / 4 + 1;
        }
    }

    public static boolean isLeapYear(int year) {
        return (year & 3) == 0 && (year % 25 != 0 || (year & 15) == 0);
    }

    public static int getHours(long milliseconds) {
        return (int) (milliseconds % DATE / HOUR);
    }

    public static int getMinutes(long milliseconds) {
        return (int) (milliseconds % HOUR / MINUTE);
    }

    public static int getSeconds(long milliseconds) {
        return (int) (milliseconds % MINUTE / SECOND);
    }

    public static int getMilliseconds(long milliseconds) {
        return (int) (milliseconds % SECOND);
    }

    public static long getAllDaysCount(long milliseconds) {
        return milliseconds / DATE;
    }

    public static long getAllHoursCount(long milliseconds) {
        return milliseconds / HOUR;
    }

    public static long getAllMinutesCount(long milliseconds) {
        return milliseconds / MINUTE;
    }

    public static long getAllSecondsCount(long milliseconds) {
        return milliseconds / SECOND;
    }

    public static boolean isLeapYear() {
        return isLeapYear(nowYear());
    }

    public static int nowYear() {
        return Calendar.getInstance().get(1);
    }

    public static List<Date> rangeOf(Date date, int beforeDates, int afterDates) {
        Calendar c = Calendar.getInstance();
        c.setTime(date);
        int size = beforeDates + afterDates + 1;
        List list = Arrays.asList(new Date[size]);
        list.set(beforeDates, date);
        int j = beforeDates;

        while (j > 0) {
            c.add(5, -1);
            --j;
            list.set(j, c.getTime());
        }

        c.add(5, size);
        j = beforeDates + 1;
        int i = size;

        while (i > j) {
            c.add(5, -1);
            --i;
            list.set(i, c.getTime());
        }

        return list;
    }

    public static boolean isToday(String date) {
        Date d1 = parse(date);
        return isTheSameDay(d1, new Date());
    }

    public static int getDayInWeek(Date date) {
        Calendar c = Calendar.getInstance();
        c.setTime(date);
        int result = c.get(7);
        return result == 1 ? 6 : result - 2;
    }

    public static Date getWeekStart(Date date) {
        Calendar c = Calendar.getInstance();
        c.setTime(date);
        c.set(7, 2);
        c.set(11, 0);
        c.set(12, 0);
        c.set(13, 0);
        return c.getTime();
    }

    public static Date getWeekEnd(Date date) {
        Calendar c = Calendar.getInstance();
        c.setTime(date);
        c.set(7, 7);
        c.set(11, 47);
        c.set(12, 59);
        c.set(13, 59);
        return c.getTime();
    }

    public static int getYear(Date date) {
        Calendar c = Calendar.getInstance();
        c.setTime(date);
        return c.get(1);
    }

    public static int getMonth(Date date) {
        Calendar c = Calendar.getInstance();
        c.setTime(date);
        return c.get(2) + 1;
    }

    public static Date getMonthStart(Date date) {
        int month = getMonth(date);
        int year = getYear(date);
        Calendar c = Calendar.getInstance();
        c.set(1, year);
        c.set(2, month - 1);
        c.set(5, 1);
        c.set(11, 0);
        c.set(12, 0);
        c.set(13, 0);
        return c.getTime();
    }

    public static Date getMonthEnd(Date date) {
        int month = getMonth(date);
        int year = getYear(date);
        Calendar c = Calendar.getInstance();
        c.set(1, year);
        c.set(2, month);
        c.set(5, 1);
        c.set(11, 0);
        c.set(12, 0);
        c.set(13, -1);
        return c.getTime();
    }

    public static String parseTimeval(long timeval) {
        int hour = (int) (timeval / 3600L);
        int minute = (int) ((timeval - (long) (hour * 3600)) / 60L);
        int second = (int) (timeval - (long) (hour * 3600) - (long) (minute * 60));
        return String.format("%02d:%02d:%02d",
                Integer.valueOf(hour), Integer.valueOf(minute), Integer.valueOf(second));
    }

    public static String getTimeWithPeriod(Date startTime, Date endTime) {
        String str = "";
        Calendar startCal = Calendar.getInstance();
        startCal.setTime(startTime);
        Calendar endCal = Calendar.getInstance();
        endCal.setTime(endTime);
        int week = startCal.get(7);
        str = str + getWeekChineseName(week) + " ";
        int hour = startCal.get(11);
        if (hour >= 0 && hour < 6) {
            str = str + "凌晨";
        } else if (hour >= 6 && hour < 13) {
            str = str + "上午";
        } else if (hour >= 13 && hour < 19) {
            str = str + "下午";
        } else {
            str = str + "晚上";
        }

        str = str + " ";
        str = str + String.format("%02d", Integer.valueOf(startCal.get(11))) + ":"
            + String.format("%02d", Integer.valueOf(startCal.get(12))) + " - "
            + String.format("%02d", Integer.valueOf(endCal.get(11))) + ":"
            + String.format("%02d", Integer.valueOf(endCal.get(12)));
        return str;
    }

    public static String getFormatDay(Date date) {
        if (date == null) {
            return "";
        } else {
            Calendar startCal = Calendar.getInstance();
            startCal.setTime(date);
            String str = "";
            str = str + String.format("%04d", Integer.valueOf(startCal.get(1))) + "-"
                + String.format("%02d", Integer.valueOf(startCal.get(2) + 1)) + "-"
                + String.format("%02d", Integer.valueOf(startCal.get(5)));
            return str;
        }
    }

    public static String getFormatDay2(Date date) {
        if (date == null) {
            return "";
        } else {
            Calendar startCal = Calendar.getInstance();
            startCal.setTime(date);
            String str = "";
            str = str + String.format("%02d", Integer.valueOf(startCal.get(2) + 1)) + "-"
                + String.format("%02d", Integer.valueOf(startCal.get(5)));
            return str;
        }
    }

    public static String getFormatDay3(Date date) {
        if (date == null) {
            return "";
        } else {
            Calendar startCal = Calendar.getInstance();
            startCal.setTime(date);
            String str = "";
            str = str + String.format("%02d", Integer.valueOf(startCal.get(2) + 1)) + "月"
                + String.format("%02d", Integer.valueOf(startCal.get(5))) + "日" + "("
                + getWeekChineseName(startCal.get(7)) + ")";
            return str;
        }
    }

    public static String getFormatDay4(Date date) {
        if (date == null) {
            return "";
        } else {
            Calendar startCal = Calendar.getInstance();
            startCal.setTime(date);
            String str = "";
            str = str + String.format("%02d", Integer.valueOf(startCal.get(2) + 1)) + "月"
                + String.format("%02d", Integer.valueOf(startCal.get(5))) + "日" + " "
                + getWeekChineseName(startCal.get(7));
            return str;
        }
    }

    public static String getFormatDay5(Date date) {
        if (date == null) {
            return "";
        } else {
            Calendar startCal = Calendar.getInstance();
            startCal.setTime(date);
            String str = "";
            str = str + String.format("%02d", Integer.valueOf(startCal.get(2) + 1)) + "月"
                + String.format("%02d", Integer.valueOf(startCal.get(5))) + "日" + " "
                + getWeekChineseName(startCal.get(7));
            return str;
        }
    }

    public static String getFormatDay6(Date date) {
        if (date == null) {
            return "";
        } else {
            Calendar startCal = Calendar.getInstance();
            startCal.setTime(date);
            String str = "";
            str = str + String.format("%02d", Integer.valueOf(startCal.get(2) + 1)) + "."
                + String.format("%02d", Integer.valueOf(startCal.get(5)));
            return str;
        }
    }

    public static String getFormatTime(Date startDate, Date endDate) {
        if (startDate != null && endDate != null) {
            String str = "";
            Calendar startCal = Calendar.getInstance();
            startCal.setTime(startDate);
            Calendar endCal = Calendar.getInstance();
            endCal.setTime(endDate);
            str = str + String.format("%02d", Integer.valueOf(startCal.get(11))) + ":"
                + String.format("%02d", Integer.valueOf(startCal.get(12))) + "-"
                + String.format("%02d", Integer.valueOf(endCal.get(11))) + ":"
                + String.format("%02d", Integer.valueOf(endCal.get(12)));
            return str;
        } else {
            return "";
        }
    }

    public static String getFormatWeekTime(Date date) {
        if (date == null) {
            return "";
        } else {
            String str = "";
            Calendar c = Calendar.getInstance();
            c.setTime(date);
            int result = c.get(7);
            switch (result) {
                case 1:
                    str = str + "星期日";
                    break;
                case 2:
                    str = str + "星期一";
                    break;
                case 3:
                    str = str + "星期二";
                    break;
                case 4:
                    str = str + "星期三";
                    break;
                case 5:
                    str = str + "星期四";
                    break;
                case 6:
                    str = str + "星期五";
                    break;
                case 7:
                    str = str + "星期六";
            }

            int hour = c.get(11);
            if (hour >= 0 && hour < 6) {
                str = str + "凌晨";
            } else if (hour >= 6 && hour < 13) {
                str = str + "上午";
            } else if (hour >= 13 && hour < 19) {
                str = str + "下午";
            } else {
                str = str + "晚上";
            }

            return str;
        }
    }

    public static String getFormatDayPart(Date date) {
        if (date == null) {
            return "";
        } else {
            String str = "";
            Calendar c = Calendar.getInstance();
            c.setTime(date);
            int hour = c.get(11);
            if (hour >= 0 && hour < 6) {
                str = str + "凌晨";
            } else if (hour >= 6 && hour < 13) {
                str = str + "上午";
            } else if (hour >= 13 && hour < 19) {
                str = str + "下午";
            } else {
                str = str + "晚上";
            }

            return str;
        }
    }

    public static String formatTimerFromLong(long time) {
        if (time < 0L) {
            return "00:00";
        } else if (time < 60L) {
            return "00:" + (time > 9L ? Long.valueOf(time) : "0" + time);
        } else {
            long hour;
            long min;
            if (time < 3600L) {
                hour = time / 60L;
                min = time % 60L;
                return (hour > 9L ? Long.valueOf(hour) : "0" + hour) + ":" + (min > 9L ? Long.valueOf(min) : "0" + min);
            } else if (time < 86400L) {
                hour = time / 3600L;
                min = time % 3600L / 60L;
                long sec = time % 3600L % 60L;
                return (hour > 9L ? Long.valueOf(hour) : "0" + hour) + ":" + (min > 9L ? Long.valueOf(min) : "0" + min)
                    + ":" + (sec > 9L ? Long.valueOf(sec) : "0" + sec);
            } else {
                return "59:59:59";
            }
        }
    }

    public static String getWeekChineseName(int week) {
        switch (week) {
            case 1:
                return "周日";
            case 2:
                return "周一";
            case 3:
                return "周二";
            case 4:
                return "周三";
            case 5:
                return "周四";
            case 6:
                return "周五";
            case 7:
                return "周六";
            default:
                return "";
        }
    }

    public static String getMonthChineseName(int month) {
        switch (month) {
            case 0:
                return "一月";
            case 1:
                return "二月";
            case 2:
                return "三月";
            case 3:
                return "四月";
            case 4:
                return "五月";
            case 5:
                return "六月";
            case 6:
                return "七月";
            case 7:
                return "八月";
            case 8:
                return "九月";
            case 9:
                return "十月";
            case 10:
                return "十一月";
            case 11:
                return "十二月";
            default:
                return "";
        }
    }

    /**
     * 最新时间格式
     *
     * @param time
     * @return
     */
    public static String getFormatTime(String time) {
        if (TextUtils.isEmpty(time)) {
            return "";
        } else {
            try {
                Date e = parse(time);
                return getFormatTime(e.getTime());
            } catch (Exception var17) {
                return time;
            }
        }
    }

    public static String getFormatTime(long cmsec) {
        long now = System.currentTimeMillis();
        long diff = now - cmsec;
        if (diff < 0L) {
            return "刚刚";
        } else {
            long day = diff / MSEC_OF_ONE_DAY;
            long hour = diff % MSEC_OF_ONE_DAY / nMSEC_OF_ONE_HOUR;
            long min = diff % MSEC_OF_ONE_DAY % nMSEC_OF_ONE_HOUR / MSEC_OF_ONE_MINUTE;
            long sec = diff % MSEC_OF_ONE_DAY % nMSEC_OF_ONE_HOUR % MSEC_OF_ONE_MINUTE / MSEC_OF_ONE_SEC;
            StringBuilder sb = new StringBuilder();
            if (day > 0L) {
                sb.append(day).append("天前");
            } else if (hour > 0L) {
                sb.append(hour).append("小时前");
            } else if (min > 0L) {
                sb.append(min).append("分钟前");
            } else {
                if (sec <= 0L) {
                    return "刚刚";
                }

                sb.append(sec).append("秒前");
            }
            return sb.toString();
        }
    }
    
    /**
     * 「刚刚」发布时间距当前时间小于1min
     * 「xx分钟前」发布时间距当前时间大于1min小于1h
     * 「xx小时前」发布时间距当前时间大于1h，且在当天
     * 「昨天」发布时间在当前时间的前一天
     * 「x月x日」发布时间早于当前时间的前一天，且在当年
     * 「2018-09-19」非当年
     *
     * @param cmsec
     * @return
     */
    public static String formatTime1(long cmsec) {
        long now = System.currentTimeMillis();
        long diff = now - cmsec;
        if(diff < MSEC_OF_ONE_MINUTE) {
            return "刚刚";
        } else {
            long day = diff / MSEC_OF_ONE_DAY;
            long hour = diff % MSEC_OF_ONE_DAY / nMSEC_OF_ONE_HOUR;
            long min = diff % MSEC_OF_ONE_DAY % nMSEC_OF_ONE_HOUR / MSEC_OF_ONE_MINUTE;
            long sec = diff % MSEC_OF_ONE_DAY % nMSEC_OF_ONE_HOUR % MSEC_OF_ONE_MINUTE / MSEC_OF_ONE_SEC;
            if(day > 1L) {
                Calendar c = Calendar.getInstance();
                c.setTimeInMillis(cmsec);
                Calendar nowC = Calendar.getInstance();
                boolean isThisYear = c.get(Calendar.YEAR) == nowC.get(Calendar.YEAR);
                if(isThisYear) {
                    return format(MD3, cmsec);
                } else {
                    return format(YMD, cmsec);
                }
            } else if(day == 1L) {
                return "昨天";
            } else if (hour > 0L) {
                return hour + "小时前";
            } else if (min > 0L) {
                return min + "分钟前";
            } else {
                return "刚刚";
            }
        }
    }

    public static String formatTime2(long cmsec) {
        try {
            long e = System.currentTimeMillis();
            long diff = e - cmsec;
            if (diff < 0L) {
                return "刚刚";
            } else {
                Calendar c = Calendar.getInstance();
                c.setTimeInMillis(cmsec);
                Calendar nowC = Calendar.getInstance();
                boolean isThisYear = c.get(Calendar.YEAR) == nowC.get(Calendar.YEAR);
                long hour = diff % MSEC_OF_ONE_DAY / nMSEC_OF_ONE_HOUR;
                long min = diff % MSEC_OF_ONE_DAY % nMSEC_OF_ONE_HOUR / MSEC_OF_ONE_MINUTE;
                long sec = diff % MSEC_OF_ONE_DAY % nMSEC_OF_ONE_HOUR % MSEC_OF_ONE_MINUTE / MSEC_OF_ONE_SEC;
                Calendar today = Calendar.getInstance();
                today.set(11, 0);
                today.set(12, 0);
                today.set(13, 0);
                today.set(14, 0);
                Calendar yes = Calendar.getInstance();
                yes.add(5, -1);
                yes.set(11, 0);
                yes.set(12, 0);
                yes.set(13, 0);
                yes.set(14, 0);
                Calendar beforeYes = Calendar.getInstance();
                beforeYes.add(5, -2);
                beforeYes.set(11, 0);
                beforeYes.set(12, 0);
                beforeYes.set(13, 0);
                beforeYes.set(14, 0);
                StringBuilder sb = new StringBuilder();
                Date d;
                SimpleDateFormat f;
                if (!isThisYear) {
                    d = c.getTime();
                    f = new SimpleDateFormat("yyyy年MM月dd日 HH:mm", Locale.getDefault());
                    return f.format(d);
                } else if (c.before(beforeYes)) {
                    d = c.getTime();
                    f = new SimpleDateFormat("MM月dd日 HH:mm", Locale.getDefault());
                    return f.format(d);
                } else if (c.before(yes)) {
                    d = c.getTime();
                    f = new SimpleDateFormat("前天 HH:mm", Locale.getDefault());
                    return f.format(d);
                } else if (c.before(today)) {
                    d = c.getTime();
                    f = new SimpleDateFormat("昨天 HH:mm", Locale.getDefault());
                    return f.format(d);
                } else if (hour <= 0L && min <= 0L) {
                    return sec > 0L ? "刚刚" : sb.toString();
                } else {
                    d = c.getTime();
                    f = new SimpleDateFormat("HH:mm", Locale.getDefault());
                    return f.format(d);
                }
            }
        } catch (Exception var21) {
            return String.valueOf(cmsec);
        }
    }

    public static String getFormatDateTime(String time) {
        if (TextUtils.isEmpty(time)) {
            return "";
        } else {
            try {
                Date e = parse(time);
                long cmsec = e.getTime();
                long now = System.currentTimeMillis();
                long diff = now - cmsec;
                if (diff < MSEC_OF_ONE_MINUTE) {
                    return "刚刚";
                } else {
                    StringBuilder sb = new StringBuilder();
                    String s = latelyDaysToString(e);
                    if (s.equals("今天")) {
                        sb.append(format("HH:mm", e));
                    } else if (s.equals("昨天")) {
                        sb.append("昨天").append(format("HH:mm", e));
                    } else if (s.equals("前天")) {
                        sb.append("前天").append(format("HH:mm", e));
                    } else if (isTheSameYear(e, new Date())) {
                        sb.append(format("MM月dd日 HH:mm", e));
                    } else {
                        sb.append(format("YYYY年MM月DD日 HH:mm", e));
                    }

                    return sb.toString();
                }
            } catch (Exception var10) {
                return time;
            }
        }
    }

    public static boolean isCloseEnough(long time1, long time2) {
        long time = Math.abs(time1 - time2);
        return time < 300000L;
    }

    public static int compareDate(Calendar date1, Calendar date2) {
        return (int) (date1.getTime().getTime() - date2.getTime().getTime());
    }

    public static String changeDateFormat(String timeStr, String template1, String template2) {
        if (TextUtils.isEmpty(timeStr)) {
            return null;
        } else {
            SimpleDateFormat dateFormat = new SimpleDateFormat();
            dateFormat.applyPattern(template1);
            Date timeDate = null;

            try {
                timeDate = dateFormat.parse(timeStr);
            } catch (ParseException var6) {
                var6.printStackTrace();
                return null;
            }

            if (timeDate == null) {
                return null;
            } else {
                dateFormat.applyPattern(template2);
                return dateFormat.format(timeDate);
            }
        }
    }

    static {
        SECOND = TimeUnit.SECONDS.toMillis(1L);
        MINUTE = TimeUnit.MINUTES.toMillis(1L);
        HOUR = TimeUnit.HOURS.toMillis(1L);
        HOUR8 = TimeUnit.HOURS.toMillis(8L);
        HOUR16 = TimeUnit.HOURS.toMillis(16L);
        DATE = TimeUnit.DAYS.toMillis(1L);
        DAY = DATE;
        WEEK = TimeUnit.DAYS.toMillis(7L);
        DAY7 = WEEK;
        DAY28 = TimeUnit.DAYS.toMillis(28L);
        DAY29 = TimeUnit.DAYS.toMillis(29L);
        DAY30 = TimeUnit.DAYS.toMillis(30L);
        DAY31 = TimeUnit.DAYS.toMillis(31L);
        DAY365 = TimeUnit.DAYS.toMillis(365L);
        DAY366 = TimeUnit.DAYS.toMillis(366L);
        YEAR_COMMON = DAY365;
        YEAR2 = YEAR_COMMON * 2L;
        YEAR_LEAP = DAY366;
        YEAR4 = YEAR_COMMON * 3L + YEAR_LEAP;
        LEAP_DAY_START_IN_4Y = YEAR_COMMON * 2L + DAY31 + DAY28;
        LEAP_DAY_END_IN_4Y = YEAR_COMMON * 2L + DAY31 + DAY29;
    }

    public enum TimeFormatter {
        Y2_M2_D2_H2_MIN2_S2("yy-MM-dd HH:mm:ss"), Y4_M2_D2_H2_MIN2_S2("yyyy-MM-dd HH:mm:ss"), Y2_M2_D2_H2_MIN2(
            "yy-MM-dd HH:mm"), Y4_M2_D2_H2_M2("yyyy-MM-dd HH:mm"), Y4_M2_D2_H2_MIN2_S2_MS3(
                "yyyy-MM-dd HH:mm:ss.SSS"), Y4M2D2H2MIN2S2MS3("yyyyMMddHHmmssSSS"), Y4M2D2("yyyyMMdd"), Y4年M2月D2日(
                    "yyyy年MM月dd日"), Y4_M2_D2_H2_MIN2_S2_URL("yyyy-MM-dd+HH:mm:ss"), Y4_M2_D2_H2(
                        "yyyy-MM-dd-HH"), Y4_M2_D2("yyyy-MM-dd"), M2_D2("MM-dd"), Y4_M2("yyyy-MM"), H2_MIN2(
                            "HH:mm"), M_D("M.d"), H2MIN2("HHmm"), Y4_M2_D2_H2_m2_s2("yyyy-MM-dd-HH-mm-ss");

        public final String pattern;

        TimeFormatter(String pattern) {
            this.pattern = pattern;
        }

        public String format(Date date) {
            return (new SimpleDateFormat(this.pattern)).format(date);
        }
    }


    /**
     * 格式化播放时间
     * @param second    秒
     * @return 格式xx:xx 或 xx:xx:xx
     */
    public static String formatPlayDurition(int second) {
        int s = second % 60;
        int m = second / 60;
        StringBuilder timeBuilder = new StringBuilder();

        // 分
        if (m <= 0) {
            timeBuilder.append("00");
        }
        else if (m < 10) {
            timeBuilder.append("0").append(m);
        }
        else {
            timeBuilder.append(m);
        }
        timeBuilder.append("'");

        // 秒
        if (s <= 0) {
            timeBuilder.append("00");
        }
        else if (s < 10) {
            timeBuilder.append("0").append(s);
        }
        else {
            timeBuilder.append(s);
        }
        timeBuilder.append("''");
        return timeBuilder.toString();
    }

    private static long sLastServerTime = 0;
    private static long sLastServerTimeDiff = 0;

    public static void setLastServerTime(long time) {
        sLastServerTime = time;
        sLastServerTimeDiff = time - System.currentTimeMillis();
    }
    
    /**
     * 获取 最后一次的 server time
     *
     * @return time
     */
    public static long getLastServerTime() {
        return sLastServerTime == 0 ? System.currentTimeMillis() : sLastServerTime;
    }
    
    /**
     * 获取 最后一次的 server time
     * 不存在服务器时间延时
     * @return time
     */
    public static long getLastDiffServerTime() {
        return  sLastServerTimeDiff == 0 ? System.currentTimeMillis() : sLastServerTimeDiff + System.currentTimeMillis();
    }
    
    /**
     * 原获取服务器时间。这个时间可能存在服务器时间延时。调用getLastDiffServerDate()以获取准确的服务器时间
     *
     * @return
     */
    public static Date getLastServerDate() {
        if (sLastServerTime <= 8 * 60 * 60 * 1000) {
            return new Date(System.currentTimeMillis());
        }
        return new Date(sLastServerTime);
    }
    
    /**
     * 获取计算过差值的服务器时间。这个服务器时间肯定是准确的,不存在服务器时间延时
     */
    public static Date getLastDiffServerDate() {
        long curServerDatemill = sLastServerTimeDiff + System.currentTimeMillis();
        return new Date(curServerDatemill);
    }
}
