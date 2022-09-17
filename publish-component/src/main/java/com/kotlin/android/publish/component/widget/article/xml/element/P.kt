package com.kotlin.android.publish.component.widget.article.xml.element

import android.graphics.Typeface
import android.text.SpannableStringBuilder
import android.text.Spanned
import android.text.style.AlignmentSpan
import android.text.style.RelativeSizeSpan
import android.text.style.StyleSpan
import com.kotlin.android.publish.component.widget.article.sytle.TextAlign
import com.kotlin.android.publish.component.widget.article.sytle.TextFontSize
import com.kotlin.android.publish.component.widget.article.xml.appendTextJsonFromXml
import com.kotlin.android.publish.component.widget.article.xml.entity.Element
import org.xmlpull.v1.XmlPullParser

/**
 * <p> 段落
 *
 * Created on 2022/3/24.
 *
 * @author o.s
 */
class P(val element: Element) : IElement {

    override fun buildSpan(ssb: SpannableStringBuilder) {
        val start = ssb.length
        element.items?.forEach {
            it.buildSpan(ssb)
        }
        val end = ssb.length

        textAlign?.apply {
            ssb.setSpan(AlignmentSpan.Standard(alignment), start, end, Spanned.SPAN_INCLUSIVE_EXCLUSIVE)
        }

        fontSize?.apply {
            ssb.setSpan(RelativeSizeSpan(scale), start, end, Spanned.SPAN_INCLUSIVE_EXCLUSIVE)
        }

        if (isBold) {
            ssb.setSpan(StyleSpan(Typeface.BOLD), start, end, Spanned.SPAN_INCLUSIVE_EXCLUSIVE)
//        } else {
//            ssb.setSpan(StyleSpan(Typeface.NORMAL), start, end, Spanned.SPAN_INCLUSIVE_INCLUSIVE)
        }
    }

    var textAlign: TextAlign?
        get() = when {
            element.style?.contains("text-align: justify;") == true -> TextAlign.JUSTIFY
            element.style?.contains("text-align: left;") == true -> TextAlign.LEFT
            element.style?.contains("text-align: center;") == true -> TextAlign.CENTER
            element.style?.contains("text-align: right;") == true -> TextAlign.RIGHT
            else -> null
        }
        set(value) {
            element.style = value?.value
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
                tag = "p",
                "style" to "style",
                "contenteditable" to "editable",
                "class" to "clazz",
            )
        }
    }
}