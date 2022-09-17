package com.kotlin.android.publish.component.widget.article.content.element

import com.kotlin.android.publish.component.widget.article.sytle.FigureClass

/**
 * <figure>：图形
 *
 * Created on 2022/3/23.
 *
 * @author o.s
 */
object Figure {

    fun start(figureClass: FigureClass, movieId: CharSequence? = null): CharSequence {
        return when (figureClass) {
            FigureClass.IMAGE -> "<figure class=\"${figureClass.clazz}\" contenteditable=\"false\">"
            FigureClass.MOVIE_CARD -> "<figure class=\"${figureClass.clazz}\" contenteditable=\"false\" data-mtime-movie-id=\"$movieId\">"
        }
    }

    val end: CharSequence
        get() = "</figure>"
}