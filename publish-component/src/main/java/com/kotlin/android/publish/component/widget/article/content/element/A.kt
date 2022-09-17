package com.kotlin.android.publish.component.widget.article.content.element

/**
 * <a>：超链接
 *
 * Created on 2022/3/23.
 *
 * @author o.s
 */
object A {

//    fun create(url: CharSequence, text: CharSequence): CharSequence {
//        return start(url).plus(text).plus(end)
//    }

    fun start(url: CharSequence): CharSequence {
        return """<a href="$url" target="_blank" rel="noopener">"""
    }

    val end: CharSequence
        get() = "</a>"
}