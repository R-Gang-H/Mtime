package com.kotlin.android.api.header

import android.net.Uri

/**
 *
 * Created on 2020/5/15.
 *
 * @author o.s
 */
object ApiEncode {

    /**
     * 参数编码
     */
    fun encode(s: String?): String? {
        return if (s == null) {
            ""
        } else Uri.encode(s)
        // 空格转+问题： ' ' -> '+' ，get请求需要 ' ' -> %20
//        try {
//            return URLEncoder.encode(s, "UTF-8");
//        } catch (final UnsupportedEncodingException e) {
//            LogManager.debugError("Encode error :" + e.toString());
//            throw new RuntimeException(e.getMessage(), e);
//        }
    }
    /**
     * 编码url中unicode字符
     *
     * @param s
     * @return
     */
    fun urlEncodeUnicode(s: String?): String? {
        if (s == null) {
            return null
        }
        val length = s.length
        val builder = StringBuilder(length) // buffer
        for (i in 0 until length) {
            val ch = s[i]
            if (ch.toInt() and 0xff80 == 0) {
                if (isSafe(ch)) {
                    builder.append(ch)
                } else if (ch == ' ') {
                    builder.append('+')
                } else {
                    builder.append('%')
                    builder.append(intToHex(ch.toInt() shr 4 and 15))
                    builder.append(intToHex(ch.toInt() and 15))
                }
            } else {
                builder.append("%u")
                builder.append(intToHex(ch.toInt() shr 12 and 15))
                builder.append(intToHex(ch.toInt() shr 8 and 15))
                builder.append(intToHex(ch.toInt() shr 4 and 15))
                builder.append(intToHex(ch.toInt() and 15))
            }
        }
        return builder.toString()
    }

    /**
     * int转换为十六进制
     *
     * @param n
     * @return
     */
    private fun intToHex(n: Int): Char {
        return if (n <= 9) {
            (n + 0x30).toChar()
        } else (n - 10 + 0x61).toChar()
    }

    /**
     * 判断url中是否有不安全字符
     *
     * @param ch
     * @return
     */
    private fun isSafe(ch: Char): Boolean {
        return if (ch in 'a'..'z' || ch in 'A'..'Z' || ch in '0'..'9') {
            true
        } else when (ch) {
            '\'', '(', ')', '*', '-', '.', '_', '!' -> true
            else -> false
        }
    }
}