package com.mtime.mtmovie.network.utils;

/**
 * Created by yinguanping on 17/1/13.
 */

public class CheckUtil {
    // 判断是否NULL
    public static boolean checkNULL(String str) {
        return str == null || "null".equals(str) || "".equals(str);
    }

    // 判断是否NULL
    public static boolean checkCookieNULL(String str) {
        return str == null || "null".equals(str) || "".equals(str) || "{}".equals(str);
    }
}
