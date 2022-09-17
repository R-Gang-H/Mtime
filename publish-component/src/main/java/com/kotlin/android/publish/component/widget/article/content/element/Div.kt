package com.kotlin.android.publish.component.widget.article.content.element

import com.kotlin.android.publish.component.widget.article.sytle.MovieClass

/**
 * <div>：DIV
 *
 * Created on 2022/3/23.
 *
 * @author o.s
 */
object Div {

//    fun create(url: CharSequence, text: CharSequence): CharSequence {
//        return start(url).plus(text).plus(end)
//    }

    fun start(
        clazz: MovieClass,
        isDisplayNone: Boolean? = null,
    ): CharSequence {
        val cl = if (clazz != null) {
            """ class="$clazz""""
        } else {
            ""
        }

        val st = if (isDisplayNone == true) {
            """ style="display: none;""""
        } else {
            ""
        }

        return """<div$cl$st>"""
    }

    val end: CharSequence
        get() = "</div>"
}