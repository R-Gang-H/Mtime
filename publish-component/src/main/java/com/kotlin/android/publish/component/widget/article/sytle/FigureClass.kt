package com.kotlin.android.publish.component.widget.article.sytle

/**
 * figure类型
 *
 * Created on 2022/3/23.
 *
 * @author o.s
 */
enum class FigureClass(val clazz: String) {

    /**
     * 图片
     */
    IMAGE("image"),

    /**
     * 电影卡片
     */
    MOVIE_CARD("movieCard");

    companion object {
        fun obtain(clazz: String?): FigureClass? {
            return when (clazz) {
                "image" -> IMAGE
                "movieCard" -> MOVIE_CARD
                else -> null
            }
        }
    }
}