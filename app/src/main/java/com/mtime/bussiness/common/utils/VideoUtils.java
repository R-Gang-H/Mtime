package com.mtime.bussiness.common.utils;

import android.text.TextUtils;

/**
 * Created by JiaJunHui on 2018/4/2.
 */

public class VideoUtils {

    public static final String FORMAT_TYPE_01 = "%s" + "'" + "%s\"";
    public static final String FORMAT_TYPE_02 = "%s" + ":" + "%s";

    public static String getTime(String formatType, int timeS, String start, String end) {
        if (TextUtils.isEmpty(start))
            start = "";
        if (TextUtils.isEmpty(end))
            end = "";
        return start + getTime(formatType, timeS) + end;
    }

    public static String getTime(String formatType, int timeS){
        if(timeS <= 0)
            return String.format(formatType, "00", "00");

        int hour = timeS/3600;

        String hourHeader = "";
        if(hour >= 1 && FORMAT_TYPE_02.equals(formatType)){
            hourHeader = hour>9?String.valueOf(hour):"0"+hour;
            hourHeader = hourHeader + ":";
        }

        int min = timeS/60;
        int sec = timeS%60;
        return hourHeader + String.format(formatType, (min<=9?"0":"") + min, (sec<=9?"0":"") + sec);
    }

}
