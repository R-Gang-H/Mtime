package com.kotlin.android.widget.titlebar

import android.text.SpannableStringBuilder
import android.text.Spanned
import android.text.method.LinkMovementMethod
import android.text.style.ClickableSpan
import android.text.style.ForegroundColorSpan
import android.util.Range
import android.view.View
import androidx.annotation.ColorRes
import com.kotlin.android.ktx.ext.core.getColor
import com.kotlin.android.widget.R

/**
 *
 * Created on 2020/7/21.
 *
 * @author o.s
 */

/**
 * 设置富文本，包含超链接点击事件
 * [title]: 文本
 * [range]: 超链接范围，多个超链接可以设置多个范围参数
 * [linkColor]: 超链接颜色
 */
fun CommonTitleBar.setTitleWithLink(
        title: String,
        vararg range: Range<Int>,
        @ColorRes linkColor: Int = R.color.color_20a0da,
        action: ((text: String) -> Unit)? = null
): CommonTitleBar {
    getTitleView()?.apply {
        val len = title.length
        val ssb = SpannableStringBuilder()
        ssb.append(title)
        range.forEach {
            val start = it.lower
            val end = it.upper
            if (end <= len) {
                val subStr = title.substring(start, end)
                val clickSpan = object : ClickableSpan() {
                    override fun onClick(widget: View) {
                        // 点击超链接把选中状态反转
                        isSelected = !isSelected
                        action?.invoke(subStr)
                    }
                }
                ssb.setSpan(clickSpan, start, end, Spanned.SPAN_INCLUSIVE_EXCLUSIVE)
                val colorSpan = ForegroundColorSpan(getColor(linkColor))
//                val underLineSpan = UnderlineSpan()
//                ssb.setSpan(underLineSpan, start, end, Spanned.SPAN_INCLUSIVE_EXCLUSIVE)
                ssb.setSpan(colorSpan, start, end, Spanned.SPAN_INCLUSIVE_EXCLUSIVE)
            }
        }
        movementMethod = LinkMovementMethod.getInstance()
        text = ssb
    }
    return this
}