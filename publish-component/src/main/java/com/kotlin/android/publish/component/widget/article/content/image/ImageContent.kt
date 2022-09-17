package com.kotlin.android.publish.component.widget.article.content.image

import android.text.SpannableStringBuilder
import com.kotlin.android.publish.component.widget.article.content.IContent
import com.kotlin.android.publish.component.widget.article.content.element.FigCaption
import com.kotlin.android.publish.component.widget.article.content.element.Figure
import com.kotlin.android.publish.component.widget.article.content.element.Img
import com.kotlin.android.publish.component.widget.article.content.element.P
import com.kotlin.android.publish.component.widget.article.sytle.FigureClass
import com.kotlin.android.publish.component.widget.article.sytle.TextAlign

/**
 * 图片卡片（内容）
 *
 * Created on 2022/3/22.
 *
 * @author o.s
 */
class ImageContent : IContent {
    override val body: SpannableStringBuilder
        get() = SpannableStringBuilder().apply {
            // <figure>:图形（图）
            append(Figure.start(figureClass = FigureClass.IMAGE))

            if (url.isNotEmpty() && fileId.isNotEmpty() && imageFormat.isNotEmpty()) {
                // <img>:图片
                append(
                    Img.start(
                        url = url,
                        fileId = fileId,
                        imageFormat = imageFormat,
                    )
                )
                append(Img.end)
            }

            if (figCaption.isNotEmpty()) {
                // <figcaption>:图注
                append(FigCaption.start())

                // <p>:图片描述文字
                append(P.start(textAlign = TextAlign.CENTER, editable = false))
                append(figCaption)
                append(P.end)

                append(FigCaption.end)
            }

            append(Figure.end)
        }

    /**
     * 图片上传后url
     */
    var url: CharSequence = ""

    /**
     * 图片上传后文件ID
     */
    var fileId: CharSequence = ""

    /**
     * 图片格式
     */
    var imageFormat: CharSequence = ""

    /**
     * 图注
     */
    var figCaption: CharSequence = ""

}