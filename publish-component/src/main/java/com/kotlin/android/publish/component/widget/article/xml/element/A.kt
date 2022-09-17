package com.kotlin.android.publish.component.widget.article.xml.element

import android.text.SpannableStringBuilder
import android.text.Spanned
import android.text.style.URLSpan
import com.kotlin.android.publish.component.widget.article.xml.appendTextJsonFromXml
import com.kotlin.android.publish.component.widget.article.xml.entity.Element
import org.xmlpull.v1.XmlPullParser

/**
 * <a> 超链接
 *
 * Created on 2022/3/24.
 *
 * @author o.s
 */
class A(val element: Element) : IElement {

    override fun buildSpan(ssb: SpannableStringBuilder) {
        val start = ssb.length
        element.items?.forEach {
            it.buildSpan(ssb)
        }
        val end = ssb.length

        href?.apply {
            ssb.setSpan(URLSpan(this), start, end, Spanned.SPAN_INCLUSIVE_EXCLUSIVE)
        }
    }

    var href: String?
        get() = element.href
        set(value) {
            element.href = value
        }

    var target: String?
        get() = element.target
        set(value) {
            element.target = value
        }

    var rel: String?
        get() = element.rel
        set(value) {
            element.rel = value
        }

    var text: String?
        get() = element.text
        set(value) {
            element.text = value
        }

    companion object {

        fun appendJson(parser: XmlPullParser, sb: StringBuilder) {
            appendTextJsonFromXml(
                parser = parser,
                sb = sb,
                tag = "a",
                "href" to "href",
                "target" to "target",
                "rel" to "rel",
            )
        }
    }
}