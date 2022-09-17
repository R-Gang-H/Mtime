package com.kotlin.android.publish.component.widget.article.content.element

import com.kotlin.android.publish.component.widget.article.sytle.MovieClass
import com.kotlin.android.publish.component.widget.article.sytle.TextColor
import com.kotlin.android.publish.component.widget.article.sytle.TextFontSize
import com.kotlin.android.publish.component.widget.article.sytle.TextStyle

/**
 * <span>：SPAN
 *
 * Created on 2022/3/23.
 *
 * @author o.s
 */
object Span {

    fun start(
        color: TextColor? = null, // 文本颜色
        fontSize: TextFontSize? = null, // 字体class
        style: TextStyle? = null, // 文本对齐方式
        clazz: MovieClass? = null, // 电影class
    ): CharSequence {
        val c = color?.style.orEmpty()
        val s = style?.style.orEmpty()
        val st = if (c.isNotEmpty() || s.isNotEmpty()) {
            """ style="$c$s""""
        } else {
            ""
        }

        val size = fontSize?.clazz.orEmpty()
        val movieClass = clazz?.clazz.orEmpty()
        val cl = if (size.isNotEmpty() || movieClass.isNotEmpty()) {
            val fc = "$size $movieClass".trim()
            """ class="$fc""""
        } else {
            ""
        }

        return "<span$st$cl>"
    }

    val end: CharSequence
        get() = "</span>"
}