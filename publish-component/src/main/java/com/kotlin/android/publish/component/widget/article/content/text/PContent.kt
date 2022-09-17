package com.kotlin.android.publish.component.widget.article.content.text

import android.text.SpannableStringBuilder
import com.kotlin.android.publish.component.widget.article.content.IContent
import com.kotlin.android.publish.component.widget.article.content.element.Em
import com.kotlin.android.publish.component.widget.article.content.element.P
import com.kotlin.android.publish.component.widget.article.content.element.Strong
import com.kotlin.android.publish.component.widget.article.content.link.LinkContent
import com.kotlin.android.publish.component.widget.article.sytle.TextAlign
import com.kotlin.android.publish.component.widget.article.sytle.TextFontSize

/**
 * P文本（内容）段落级
 *
 * Created on 2022/3/22.
 *
 * @author o.s
 */
class PContent : IContent {

    private val contentList by lazy {
        ArrayList<IContent>()
    }

    override val body: SpannableStringBuilder
        get() = SpannableStringBuilder().apply {
            append(
                P.start(
                    textAlign = textAlign,
                    fontSize = fontSize,
                    editable = editable,
                )
            )

            // 粗体
            if (isBold) {
                append(Strong.start)
            }

            // 斜体
            if (isEm) {
                append(Em.start)
            }

            append(text)

            if (isEm) {
                append(Em.end)
            }

            if (isBold) {
                append(Strong.end)
            }

            contentList.forEach {
                append(it.body)
            }

            append(P.end) // p
        }

//    /**
//     * 字体颜色
//     */
//    var color: TextColor = TextColor.BLACK

    /**
     * 字体大小
     */
    var fontSize: TextFontSize = TextFontSize.STANDARD

//    /**
//     * 下划线/中划线样式
//     */
//    var style: TextStyle? = null

    /**
     * <strong>加粗</strong>
     */
    var isBold: Boolean = false

    /**
     * <em>倾斜</em>
     */
    var isEm: Boolean = false

    /**
     * 文本内容
     */
    var text: CharSequence = ""

    /**
     * 文本对齐方式
     */
    var textAlign: TextAlign = TextAlign.NONE

    /**
     * 可编辑的
     */
    var editable: Boolean? = null

    fun addTextContent(textContent: TextContent) {
        contentList.add(textContent)
    }

    fun addLinkContent(linkContent: LinkContent) {
        contentList.add(linkContent)
    }

    fun getCurrentContent(): IContent {
        return contentList.last()
    }

}