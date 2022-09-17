package com.kotlin.android.publish.component.widget.article.xml.element

import android.text.SpannableStringBuilder
import com.kotlin.android.publish.component.widget.article.xml.appendTextJsonFromXml
import com.kotlin.android.publish.component.widget.article.xml.entity.Element
import org.xmlpull.v1.XmlPullParser

/**
 * <div>
 *
 * Created on 2022/3/24.
 *
 * @author o.s
 */
class Div(val element: Element) : IElement {

    override fun buildSpan(ssb: SpannableStringBuilder) {

    }

    var clazz: String? = null

    var editable: String? = null

    companion object {

        fun appendJson(parser: XmlPullParser, sb: StringBuilder) {
            appendTextJsonFromXml(
                parser = parser,
                sb = sb,
                tag = "div",
                "class" to "clazz",
                "contenteditable" to "editable",
            )
        }
    }
}