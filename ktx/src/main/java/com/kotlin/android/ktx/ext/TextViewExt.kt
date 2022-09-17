package com.kotlin.android.ktx.ext

import android.text.Html
import android.text.Spannable
import android.text.SpannableString
import android.text.style.ForegroundColorSpan
import android.widget.TextView
import androidx.annotation.ColorInt
import androidx.annotation.StringRes
import com.kotlin.android.ktx.ext.core.getString
import java.util.regex.Pattern

/**
 * @author ZhouSuQiang
 * @email zhousuqiang@126.com
 * @date 2020/7/24
 */

fun TextView.setTextWithFormat(@StringRes resId: Int, vararg params: Any) {
    text = getString(resId, *params)
}


/**
 * 从字符串里取出数字，并设置颜色
 */
fun TextView.textHighLight(str: String, @ColorInt color: Int) {
    val p = Pattern.compile("\\d+")
    val m = p.matcher(str)
    val spannableString = SpannableString(str)
    while (m.find()) {
        val span = ForegroundColorSpan(color)
        spannableString.setSpan(span, m.start(), m.end(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE)
    }
    text = spannableString
}

/**
 * 匹配关键词的内容设置高亮颜色
 */
fun TextView.convertToHtml(title: String, color: String, keyword: String) {
    val htmlKey = "<font color=\"${color}\">${keyword}</font>"
    text = Html.fromHtml(title.replace(keyword, htmlKey, false), Html.FROM_HTML_MODE_LEGACY)
}