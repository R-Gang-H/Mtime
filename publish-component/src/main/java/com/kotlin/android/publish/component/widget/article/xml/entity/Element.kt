package com.kotlin.android.publish.component.widget.article.xml.entity

import android.text.Editable
import android.text.SpannableStringBuilder
import com.kotlin.android.ktx.ext.log.e
import com.kotlin.android.publish.component.widget.article.view.EditorItemLayout
import com.kotlin.android.publish.component.widget.article.view.ItemType
import com.kotlin.android.publish.component.widget.article.view.entity.ImageElementData
import com.kotlin.android.publish.component.widget.article.view.entity.MovieElementData
import com.kotlin.android.publish.component.widget.article.view.entity.TextElementData
import com.kotlin.android.publish.component.widget.article.view.entity.VideoElementData
import com.kotlin.android.publish.component.widget.article.xml.element.*

/**
 *
 * Created on 2022/3/25.
 *
 * @author o.s
 */
data class Element(

    /**
     * 标签名称，为null或""时为普通文本
     */
    val tag: String? = null,

    /**
     * 文本内容： tag == null 时，会有值
     */
    var text: String? = null,

    /**
     * <span>
     */
    var clazz: String? = null,
    var style: String? = null,
    var editable: String? = null,
    var movieId: String? = null, // <figure>
    var type: String? = null, // <source>

    var dataV6: String? = null, // 电影用？

    /**
     * <a>
     */
    var href: String? = null,
    var target: String? = null,
    var rel: String? = null,

    /**
     * <img>
     */
    var src: String? = null, // <source> <video>
    var fileId: String? = null,
    var imageFormat: String? = null,
    var mTimeImg: String? = null,

    /**
     * <video>
     */
    var controls: String? = null,
    var width: String? = null,
    var height: String? = null,
    var videoId: String? = null,
    var videoType: String? = null,
    var mceFragment: String? = null,
    var poster: String? = null,

    var items: ArrayList<Element>? = null,

    // local
    var index: Int = -1,
) {
    var view: EditorItemLayout? = null

    /**
     * 元素内容是否为空
     */
    val isEmpty: Boolean
        get() = when (tag) {
            "figure" -> {
                false
            }
            "video" -> {
                false
            }
            "img" -> {
                false
            }
            null -> {
                text.isNullOrEmpty()
            }
            else -> {
                var empty = true
                items?.forEach {
                    if (!it.isEmpty) {
                        empty = false
                        return@forEach
                    }
                }
                empty
            }
        }

    /**
     * 段落类型，只有最外层段落列表调用有意义 [Body.elements]
     */
    val itemType: ItemType?
        get() = when (tag) {
            "p" -> {
                if (items?.firstOrNull()?.tag == "video") {
                    ItemType.VIDEO_CARD
                } else {
                    ItemType.TEXT_CARD
                }
            }
            "figure" -> {
                when (clazz) {
                    "image" -> ItemType.IMAGE_CARD
                    "movieCard" -> ItemType.MOVIE_CARD
//                    "actorCard" -> ItemType.ACTOR_CARD
                    else -> null
                }
            }
            else -> {
                ItemType.TEXT_CARD
                // null
            }
        }

    val body: String
        get() {
            if (tag == null) {
//                val content = text.orEmpty().replace(" ", "\\u0020")
//                val content = text.orEmpty().replace(" ", "&nbsp;")
//                "content =$content".e()
                return text.orEmpty().replace(" ", "&nbsp;")
            }
            if (tag == "br") {
                return "<br />"
            }
            val sb = StringBuilder()
            sb.append("<")
            sb.append(tag)
            if (clazz != null) {
                sb.append(""" class="$clazz"""")
            }
            if (style != null) {
                sb.append(""" style="$style"""")
            }
            if (editable != null) {
                sb.append(""" contenteditable="$editable"""")
            }
            if (movieId != null) {
                sb.append(""" data-mtime-movie-id="$movieId"""")
            }
            if (type != null) {
                sb.append(""" type="$type"""")
            }
//            if (dataV6 != null) {
//                sb.append(""" data-v-6d079e13="$dataV6"""")
//            }
            if (href != null) {
                sb.append(""" href="$href"""")
            }
            if (target != null) {
                sb.append(""" target="$target"""")
            }
            if (rel != null) {
                sb.append(""" rel="$rel"""")
            }
            if (src != null) {
                sb.append(""" src="$src"""")
            }
            if (fileId != null) {
                sb.append(""" data-mt-fileid="$fileId"""")
            }
            if (imageFormat != null) {
                sb.append(""" imageFormat="$imageFormat"""")
            }
            if (mTimeImg != null) {
                sb.append(""" data-mtime-img="$mTimeImg"""")
            }
            if (controls != null) {
                sb.append(""" controls="$controls"""")
            }
            if (width != null) {
                sb.append(""" width="$width"""")
            }
            if (height != null) {
                sb.append(""" height="$height"""")
            }
            if (videoId != null) {
                sb.append(""" data-video-id="$videoId"""")
            }
            if (videoType != null) {
                sb.append(""" data-video-type="$videoType"""")
            }
            if (mceFragment != null) {
                sb.append(""" data-mce-fragment="$mceFragment"""")
            }
            if (poster != null) {
                sb.append(""" poster="""")
                sb.append(poster)
                sb.append(""""""")
            }
            if (items != null) {
                sb.append(">")

                items?.forEach {
                    sb.append(it.body)
                }

                sb.append("</")
                sb.append(tag)
                sb.append(">")
            } else {
                sb.append(" />")
            }
            return sb.toString()
        }

    private var iElement: IElement? = null
        get() {
            if (field == null) {
                field = when (tag) {
                    "p" -> {
                        P(element = this)
                    }
                    "span" -> {
                        Span(element = this)
                    }
                    "strong" -> {
                        Strong(element = this)
                    }
                    "em" -> {
                        Em(element = this)
                    }
                    "a" -> {
                        A(element = this)
                    }
                    null -> {
                        Text(element = this)
                    }
                    else -> {
                        null
                    }
                }
            }
            return field
        }

    fun toEditable(): Editable {
        val ssb = SpannableStringBuilder()
        buildSpan(ssb)
        return ssb
    }

    fun buildSpan(ssb: SpannableStringBuilder) {
        iElement?.buildSpan(ssb)
//        items?.forEach {
//            it.buildSpan(ssb)
//        }
    }

    companion object {
        fun obtain(type: ItemType): Element {
            return when (type) {
//                ItemType.ACTOR_CARD -> {
//                    Element(tag = "figure")
//                }
                ItemType.IMAGE_CARD -> {
                    ImageElementData().element
                }
                ItemType.MOVIE_CARD -> {
                    MovieElementData().element
                }
                ItemType.TEXT_CARD -> {
                    TextElementData().element
//                    Element(
//                        tag = "p",
//                        items = arrayListOf(
//                            Element(text = "")
//                        )
//                    )
                }
                ItemType.VIDEO_CARD -> {
                    VideoElementData().element
                }
            }
        }
    }
}
