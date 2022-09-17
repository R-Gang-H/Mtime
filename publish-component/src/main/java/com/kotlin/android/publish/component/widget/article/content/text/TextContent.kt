package com.kotlin.android.publish.component.widget.article.content.text

import android.text.SpannableStringBuilder
import com.kotlin.android.publish.component.widget.article.content.IContent
import com.kotlin.android.publish.component.widget.article.content.element.Em
import com.kotlin.android.publish.component.widget.article.content.element.Span
import com.kotlin.android.publish.component.widget.article.content.element.Strong
import com.kotlin.android.publish.component.widget.article.sytle.TextColor
import com.kotlin.android.publish.component.widget.article.sytle.TextFontSize
import com.kotlin.android.publish.component.widget.article.sytle.TextStyle

/**
 * 文本（内容）段落内部内容
 *
 * Created on 2022/3/22.
 *
 * @author o.s
 */
class TextContent : IContent {
    override val body: SpannableStringBuilder
        get() = SpannableStringBuilder().apply {
            append(
                Span.start(
                    color = color,
                    fontSize = fontSize,
                    style = style,
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

            append(Span.end) // span
        }

    /**
     * 字体颜色
     */
    var color: TextColor = TextColor.BLACK

    /**
     * 字体大小
     */
    var fontSize: TextFontSize = TextFontSize.STANDARD

    /**
     * 下划线/中划线样式
     */
    var style: TextStyle? = null

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
}