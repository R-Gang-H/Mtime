package com.kotlin.android.publish.component.widget.article.xml.element

import android.graphics.Typeface
import android.text.SpannableStringBuilder
import android.text.Spanned
import android.text.style.StyleSpan
import com.kotlin.android.publish.component.widget.article.xml.appendTextJsonFromXml
import com.kotlin.android.publish.component.widget.article.xml.entity.Element
import org.xmlpull.v1.XmlPullParser

/**
 * <em> 斜体
 *
 * Created on 2022/3/24.
 *
 * @author o.s
 */
class Em(val element: Element) : IElement {

    override fun buildSpan(ssb: SpannableStringBuilder) {
        val start = ssb.length
        element.items?.forEach {
            it.buildSpan(ssb)
        }
        val end = ssb.length

        ssb.setSpan(StyleSpan(Typeface.ITALIC), start, end, Spanned.SPAN_INCLUSIVE_EXCLUSIVE)
    }

    companion object {

        fun appendJson(parser: XmlPullParser, sb: StringBuilder) {
            appendTextJsonFromXml(
                parser = parser,
                sb = sb,
                tag = "em",
            )
        }
    }
}