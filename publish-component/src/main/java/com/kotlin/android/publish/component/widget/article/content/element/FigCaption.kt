package com.kotlin.android.publish.component.widget.article.content.element

/**
 * <figcaption>：图注
 *
 * Created on 2022/3/23.
 *
 * @author o.s
 */
object FigCaption {

    fun create(text: CharSequence) {

    }

    fun start(style: Boolean = false): CharSequence {
        return if (style) {
            """<figcaption style="width: 100%;">"""
        } else {
            "<figcaption>"
        }
    }

    val end: CharSequence
        get() = "</figcaption>"
}