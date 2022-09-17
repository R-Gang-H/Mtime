package com.kotlin.android.ktx.ext

import android.text.TextUtils
import com.google.gson.JsonObject
import com.kotlin.android.ktx.ext.log.e
import java.io.File
import java.util.regex.Matcher
import java.util.regex.Pattern

/**
 *
 * Created on 2020/5/13.
 *
 * @author o.s
 */

const val PROTOCOL_HTTPS = "https://"
const val PROTOCOL_HTTP = "http://"

fun String.protocolUrl(isHttps: Boolean = false): String {
    return if (isHttps) {
        when {
            startsWith(PROTOCOL_HTTPS, true) -> {
                this
            }
            startsWith(PROTOCOL_HTTP, true) -> {
                replaceFirst(PROTOCOL_HTTP, PROTOCOL_HTTPS, true)
            }
            else -> {
                "$PROTOCOL_HTTPS$this"
            }
        }
    } else {
        when {
            startsWith(PROTOCOL_HTTP, true) -> {
                this
            }
            startsWith(PROTOCOL_HTTPS, true) -> {
                replaceFirst(PROTOCOL_HTTPS, PROTOCOL_HTTP, true)
            }
            else -> {
                "$PROTOCOL_HTTP$this"
            }
        }
    }
}

/**
 * 检查非空
 */
fun <T> checkNotNullState(any: T?): T {
    if (any == null) {
        throw NullPointerException("$any is null")
    }
    return any
}


/**
 * 使用正则表达式提取中括号中的内容
 * @param msg
 * @return
 */
fun extractMessage(msg: String, begin: Char = '<', end: Char = '>'): String {
    val str = StringBuffer()
    var start = 0
    var startFlag = 0
    var endFlag = 0
    for (i in msg.indices) {
        if (msg[i] == begin) {
            startFlag++
            if (startFlag == endFlag + 1) {
                start = i
            }
        } else if (msg[i] == end) {
            endFlag++
            if (endFlag == startFlag) {
                str.append(msg.substring(start + 1, i))
            }
        }
    }
    return str.toString()
}

fun getTestApiContent(list: MutableList<JsonObject>, type: String, tag: String = "msg"): String? {
    list.forEach {
        val msg: String? = it.get(tag).asString
        if (type == msg) {
            ("本地匹配成功   $it").e()
            return it.toString()
        }
    }
    return ""
}

/**
 * 校验身份证号
 */
fun checkIdCard(idCard: String): Boolean {
    return idCard.length == 18
//    val realIdCard = idCard.trim().toUpperCase()
//    if (TextUtils.isEmpty(realIdCard)) {//身份证号为空
//        return false
//    }
//    if (realIdCard.length != 18) {
//        return false
//    }
//
//    val ID_CARD_CHAR = arrayListOf<Char>('0', '1', '2', '3', '4', '5', '6', '7', '8', '9', 'X')
//    if (!(realIdCard.substring(realIdCard.indices).isDigitsOnly() && realIdCard[realIdCard.length - 1] in ID_CARD_CHAR)) {
//        return false
//    }
//
//    var sum: Long = 0L
//    var ex = 1L
//    (realIdCard.indices).forEach {
//        val c = realIdCard[18 - it - 1]
//        "char:$c == ${c.toString().toLong()}".e()
//        sum += (ex % 11L) * (if (c == 'X') 10L else c.toString().toLong())
//        ex *= 2
//    }
//
//    "mode:${sum % 11L}".e()
//    return sum % 11L == 1L

}


/**
 * 判断字符串中是否包含空格
 */
fun String.isNotRealName(): Boolean {
    val trim = this.trim()
    return TextUtils.isEmpty(trim) || trim.contains(" ")
}

/**
 * 是否是手机号
 */
fun String.isPhoneNum(): Boolean {
    val trim = this.trim()
    if (TextUtils.isEmpty(trim)) {
        return false
    }
    if (trim.length != 11) {
        return false
    }

    return trim.startsWith("1")
}

/**
 * 是否是邮箱
 */
fun String.isPostBox(): Boolean {
    val trim = this.trim()
    if (TextUtils.isEmpty(trim)) {
        return false
    }
    return trim.contains("@") && (trim.contains("com") || trim.contains("cn"))
}

/**
 * 获取文件后缀名，.jpg、.gif
 */
fun getSuffixName(url: String): String {
    if (TextUtils.isEmpty(url)) {
        return ""
    }
    val lastIndexOf = url.lastIndexOf(".")
    if (lastIndexOf < 0) {
        return ""
    }
    return url.substring(lastIndexOf)
}

fun String?.toLongOrDefault(defaultValue: Long): Long {
    return try {
        this?.let {
            toLong()
        } ?: defaultValue
    } catch (_: NumberFormatException) {
        defaultValue
    }
}