package com.kotlin.android.publish.component.widget.article.content.element

import com.kotlin.android.publish.component.widget.article.sytle.TextAlign
import com.kotlin.android.publish.component.widget.article.sytle.TextFontSize

/**
 * <p>：段落
 *
 * Created on 2022/3/23.
 *
 * @author o.s
 */
object P {

    fun start(
        textAlign: TextAlign? = null,
        fontSize: TextFontSize? = null,
        editable: Boolean? = null,
    ): CharSequence {
        val align = textAlign?.value.orEmpty()
        val size = fontSize?.clazz.orEmpty()
        val style = if (align.isNotEmpty() || size.isNotEmpty()) {
            val s = "$align $size".trim()
            """ style="$s""""
        } else {
            ""
        }

        val edit = when (editable) {
            false -> {
                """ contenteditable="false""""
            }
            true -> {
                """ contenteditable="true""""
            }
            else -> {
                ""
            }
        }
        return "<p$style$edit>"
    }

    val end: CharSequence
        get() = "</p>"
}