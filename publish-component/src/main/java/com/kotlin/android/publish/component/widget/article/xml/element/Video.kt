package com.kotlin.android.publish.component.widget.article.xml.element

import android.text.SpannableStringBuilder
import com.kotlin.android.publish.component.widget.article.xml.appendTextJsonFromXml
import com.kotlin.android.publish.component.widget.article.xml.entity.Element
import org.xmlpull.v1.XmlPullParser

/**
 * <video> 视频
 *
 * Created on 2022/3/24.
 *
 * @author o.s
 */
class Video(val element: Element) : IElement {

    override fun buildSpan(ssb: SpannableStringBuilder) {

    }

    /**
     * 视频url
     */
    var src: String? = null

    /**
     * 海报url
     */
    var poster: String? = null

    /**
     * 视频宽
     */
    var width: String? = null

    /**
     * 视频高
     */
    var height: String? = null

    /**
     * 视频ID
     */
    var videoId: String? = null

    /**
     * 视频类型
     */
    var videoType: String? = null

    /**
     * 资源类型
     */
    var type: String? = null // audio/ogg

    /**
     * data-mce-fragment="1"
     */
    var mceFragment: String? = null

    var controls: String? = null

    companion object {

        fun appendJson(parser: XmlPullParser, sb: StringBuilder) {
            appendTextJsonFromXml(
                parser = parser,
                sb = sb,
                tag = "video",
                "controls" to "controls",
                "width" to "width",
                "height" to "height",
                "data-video-type" to "videoType",
                "data-video-id" to "videoId",
                "data-mce-fragment" to "mceFragment",
                "src" to "src",
                "poster" to "poster",
            )
        }
    }
}












