package com.kotlin.android.publish.component.widget.article.xml.element

import android.text.SpannableStringBuilder
import com.kotlin.android.publish.component.widget.article.xml.appendTextJsonFromXml
import com.kotlin.android.publish.component.widget.article.xml.entity.Element
import org.xmlpull.v1.XmlPullParser

/**
 * <source> 资源
 *
 * Created on 2022/3/24.
 *
 * @author o.s
 */
class Source(val element: Element) : IElement {

    override fun buildSpan(ssb: SpannableStringBuilder) {

    }

    companion object {

        fun appendJson(parser: XmlPullParser, sb: StringBuilder) {
            appendTextJsonFromXml(
                parser = parser,
                sb = sb,
                tag = "source",
                "src" to "src",
                "type" to "type",
            )
        }
    }
}