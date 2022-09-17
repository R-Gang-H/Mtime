package com.kotlin.android.publish.component.widget.article.xml.element

import android.graphics.Typeface
import android.text.SpannableStringBuilder
import android.text.Spanned
import android.text.style.*
import com.kotlin.android.publish.component.widget.article.sytle.TextColor
import com.kotlin.android.publish.component.widget.article.sytle.TextFontSize
import com.kotlin.android.publish.component.widget.article.xml.appendTextJsonFromXml
import com.kotlin.android.publish.component.widget.article.xml.entity.Element
import org.xmlpull.v1.XmlPullParser

/**
 * <span>
 *
 * Created on 2022/3/24.
 *
 * @author o.s
 */
class Span(val element: Element) : IElement {

    override fun buildSpan(ssb: SpannableStringBuilder) {
        val start = ssb.length
        element.items?.forEach {
            it.buildSpan(ssb)
        }
        val end = ssb.length

        fontSize?.apply {
            ssb.setSpan(RelativeSizeSpan(scale), start, end, Spanned.SPAN_INCLUSIVE_EXCLUSIVE)
        }

        textColor?.apply {
            ssb.setSpan(ForegroundColorSpan(color), start, end, Spanned.SPAN_INCLUSIVE_EXCLUSIVE)
        }

        if (isU) {
            ssb.setSpan(UnderlineSpan(), start, end, Spanned.SPAN_INCLUSIVE_EXCLUSIVE)
        }

        if (isS) {
            ssb.setSpan(StrikethroughSpan(), start, end, Spanned.SPAN_INCLUSIVE_EXCLUSIVE)
        }

        if (isBold) {
            ssb.setSpan(StyleSpan(Typeface.BOLD), start, end, Spanned.SPAN_INCLUSIVE_EXCLUSIVE)
//        } else {
//            ssb.setSpan(StyleSpan(Typeface.NORMAL), start, end, Spanned.SPAN_INCLUSIVE_INCLUSIVE)
        }
    }

    var fontSize: TextFontSize?
        get() = when {
            element.clazz?.contains("mini_") == true -> TextFontSize.SMALL
            element.clazz?.contains("standard_") == true -> TextFontSize.STANDARD
            element.clazz?.contains("medium_") == true -> TextFontSize.BIG
            element.clazz?.contains("large_") == true -> TextFontSize.LARGER
            else -> null
        }
        set(value) {
            if (isBold) {
                element.clazz = value?.clazzBold
            } else {
                element.clazz = value?.clazz
            }
        }

    var textColor: TextColor?
        get() = when {
            element.style?.contains(TextColor.BLACK.style) == true || element.style?.contains(TextColor.BLACK.rgb) == true -> TextColor.BLACK
            element.style?.contains(TextColor.GRAY.style) == true || element.style?.contains(TextColor.GRAY.rgb) == true -> TextColor.GRAY
            element.style?.contains(TextColor.ORANGE.style) == true || element.style?.contains(TextColor.ORANGE.rgb) == true -> TextColor.ORANGE
            element.style?.contains(TextColor.YELLOW.style) == true || element.style?.contains(TextColor.YELLOW.rgb) == true -> TextColor.YELLOW
            element.style?.contains(TextColor.CYAN.style) == true || element.style?.contains(TextColor.CYAN.rgb) == true -> TextColor.CYAN
            element.style?.contains(TextColor.BLUE.style) == true || element.style?.contains(TextColor.BLUE.rgb) == true -> TextColor.BLUE
            else -> null
        }
        set(value) {
            val style = if (isU && isS) {
                "text-decoration: underline line-through;"
            } else if (isU) {
                "text-decoration: underline;"
            } else if (isS) {
                "text-decoration: line-through;"
            } else {
                ""
            }
            element.style = "${value?.style.orEmpty()}$style"
        }

    var isU: Boolean
        get() = element.style?.contains("underline") ?: false
        set(value) {
            val style = if (value && isS) {
                "text-decoration: underline line-through;"
            } else if (value) {
                "text-decoration: underline;"
            } else if (isS) {
                "text-decoration: line-through;"
            } else {
                ""
            }
            element.style = "${textColor?.style.orEmpty()}$style"
        }

    var isS: Boolean
        get() = element.style?.contains("line-through") ?: false
        set(value) {
            val style = if (value && isU) {
                "text-decoration: underline line-through;"
            } else if (value) {
                "text-decoration: line-through;"
            } else if (isU) {
                "text-decoration: underline;"
            } else {
                ""
            }
            element.style = "${textColor?.style.orEmpty()}$style"
        }

    var isBold: Boolean
        get() = element.clazz?.contains("strong") ?: false
        set(value) {
            if (value) {
                element.clazz = (fontSize ?: TextFontSize.STANDARD).clazzBold
            } else {
                element.clazz = fontSize?.clazz
            }
        }

    companion object {

        fun appendJson(parser: XmlPullParser, sb: StringBuilder) {
            appendTextJsonFromXml(
                parser = parser,
                sb = sb,
                tag = "span",
                "style" to "style",
                "class" to "clazz",
            )
        }
    }
}