package com.kotlin.android.publish.component.widget.article.content.element

/**
 * <img>：图片
 *
 * Created on 2022/3/23.
 *
 * @author o.s
 */
object Img {

//    fun create(url: CharSequence, fileId: CharSequence, imageFormat: CharSequence): CharSequence {
//        return start(url, fileId, imageFormat)
//    }

    fun start(
        url: CharSequence,
        fileId: CharSequence? = null,
        imageFormat: CharSequence? = null,
        mTimeImg: CharSequence? = null
    ): CharSequence {
        val id = if (fileId.isNullOrEmpty().not()) {
            """ data-mt-fileId="$fileId" data-mt-format="${imageFormat ?: ""}""""
        } else {
            ""
        }
        val img = if (mTimeImg.isNullOrEmpty().not()) {
            """ data-mtime-img="$mTimeImg""""
        } else {
            ""
        }
        return """<img src="$url"$id$img>"""
    }

    val end: CharSequence
        get() = "</img>"
}