package com.kotlin.android.mtime.ktx

import android.text.TextUtils
import java.util.regex.Matcher
import java.util.regex.Pattern

/**
 * create by lushan on 2020/11/10
 * description:
 */
private val regEx_script: String? = "<script[^>]*?>[\\s\\S]*?<\\/script>" // 定义script的正则表达式

private const val regEx_style = "<style[^>]*?>[\\s\\S]*?<\\/style>" // 定义style的正则表达式

private const val regEx_html = "<[^>]+>" // 定义HTML标签的正则表达式

private const val regEx_space = "\\s*|\t|\r|\n" //定义空格回车换行符


/**
 * @param htmlStr
 * @return 删除Html标签
 */
fun delHTMLTag(htmlStr: String): String {
    var htmlStr = htmlStr
    val p_script: Pattern = Pattern.compile(regEx_script, Pattern.CASE_INSENSITIVE)
    val m_script: Matcher = p_script.matcher(htmlStr)
    htmlStr = m_script.replaceAll("") // 过滤script标签
    val p_style: Pattern = Pattern.compile(regEx_style, Pattern.CASE_INSENSITIVE)
    val m_style: Matcher = p_style.matcher(htmlStr)
    htmlStr = m_style.replaceAll("") // 过滤style标签
    val p_html: Pattern = Pattern.compile(regEx_html, Pattern.CASE_INSENSITIVE)
    val m_html: Matcher = p_html.matcher(htmlStr)
    htmlStr = m_html.replaceAll("") // 过滤html标签
    val p_space: Pattern = Pattern.compile(regEx_space, Pattern.CASE_INSENSITIVE)
    val m_space: Matcher = p_space.matcher(htmlStr)
    htmlStr = m_space.replaceAll("") // 过滤空格回车标签
    return htmlStr.trim { it <= ' ' } // 返回文本字符串
}

fun getTextFromHtml(htmlStr: String): String? {
    var htmlStr = htmlStr
    htmlStr = delHTMLTag(htmlStr)
    htmlStr = htmlStr.replace(" ".toRegex(), "")
    return htmlStr
}


/**
 * html 编码
 * @param source
 * @return
 */
fun htmlEncode(source: String?): String? {
    if (source == null) {
        return ""
    }
    var html = ""
    val buffer = StringBuffer()
    for (i in 0 until source.length) {
        val c = source[i]
        when (c) {
            '<' -> buffer.append("&lt;")
            '>' -> buffer.append("&gt;")
            '&' -> buffer.append("&amp;")
            '"' -> buffer.append("&quot;")
            ' ' -> buffer.append("&nbsp;")
            else -> buffer.append(c)
        }
    }
    html = buffer.toString()
    return html
}


/**
 * html 解码
 * @param source
 * @return
 */
fun htmlDecode(source: String): String? {
    var source = source
    if (TextUtils.isEmpty(source)) {
        return ""
    }
    source = source.replace("&lt;", "<")
    source = source.replace("&gt;", ">")
    source = source.replace("&amp;", "&")
    source = source.replace("&quot;", "\"")
    source = source.replace("&nbsp;", " ")
    source = source.replace("&ldquo;", "\"")
    source = source.replace("&rdquo;", "\"")
    return getTextFromHtml(source)
}

fun htmlSimpleDecode(source: String): String {
    var source = source
    if (TextUtils.isEmpty(source)) {
        return ""
    }
    source = source.replace("&nbsp;", " ")
    source = source.replace("&lt;", "<")
    source = source.replace("&gt;", ">")
//    source = source.replace("&amp;", "&")
//    source = source.replace("\\", "&#92;")
    source = source.replace("&quot;", "\"")
    source = source.replace("&ldquo;", "\"")
    source = source.replace("&rdquo;", "\"")
    return source
}
