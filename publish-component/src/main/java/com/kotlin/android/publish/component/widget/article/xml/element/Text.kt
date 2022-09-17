package com.kotlin.android.publish.component.widget.article.xml.element

import android.text.SpannableStringBuilder
import com.kotlin.android.publish.component.widget.article.xml.appendKeyValue
import com.kotlin.android.publish.component.widget.article.xml.entity.Element
import org.xmlpull.v1.XmlPullParser

/**
 * 文本
 *
 * Created on 2022/3/24.
 *
 * @author o.s
 */
class Text(val element: Element) : IElement {

    override fun buildSpan(ssb: SpannableStringBuilder) {
        ssb.append(element.text.orEmpty())
    }

    companion object {

        fun appendJson(parser: XmlPullParser, sb: StringBuilder) {
            if (!sb.endsWith("{") && !sb.endsWith("[") && !sb.endsWith(",")) {
                sb.append(",")
            }
            val isJsonObject = sb.endsWith("},") || sb.endsWith("[")
            if (isJsonObject) {
                sb.append("{")
            }
            sb.appendKeyValue("text", parser.text)
            if (isJsonObject) {
                sb.append("}")
            }
        }
    }
}