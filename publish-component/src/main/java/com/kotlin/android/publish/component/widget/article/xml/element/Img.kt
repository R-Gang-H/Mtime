package com.kotlin.android.publish.component.widget.article.xml.element

import android.text.SpannableStringBuilder
import com.kotlin.android.publish.component.widget.article.xml.appendTextJsonFromXml
import com.kotlin.android.publish.component.widget.article.xml.entity.Element
import org.xmlpull.v1.XmlPullParser

/**
 * <img> 图片
 *
 * Created on 2022/3/24.
 *
 * @author o.s
 */
class Img(val element: Element) : IElement {

    override fun buildSpan(ssb: SpannableStringBuilder) {

    }

    /**
     * 图片上传后url
     */
    var src: String? = null

    /**
     * 图片上传后文件ID
     */
    var fileId: String? = null

    /**
     * 图片格式
     */
    var imageFormat: String? = null

    var mTimeImg: String? = null

    companion object {

        fun appendJson(parser: XmlPullParser, sb: StringBuilder) {
            appendTextJsonFromXml(
                parser = parser,
                sb = sb,
                tag = "img",
                "src" to "src",
                "data-mt-fileid" to "fileId",
                "data-mt-format" to "imageFormat",
                "data-mtime-img" to "mTimeImg",
            )
        }
    }
}