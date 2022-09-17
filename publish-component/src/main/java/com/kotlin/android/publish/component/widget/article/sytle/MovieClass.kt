package com.kotlin.android.publish.component.widget.article.sytle

/**
 * 电影class
 *
 * Created on 2022/3/23.
 *
 * @author o.s
 */
enum class MovieClass(val clazz: String) {

    /**
     * 影片包装
     */
    WRAPPER("DRE-subject-wrapper"),

    /**
     * 影片条目
     */
    ITEM("DRE-subject-item"),

    /**
     * 影片封面
     */
    COVER("DRE-subject-cover"),

    /**
     * 影片信息
     */
    INFO("DRE-subject-info"),

    /**
     * 影片名称
     */
    TITLE("DRE-subject-title"),

    /**
     * 影片评级
     */
    RATING("DRE-subject-rating"),

    /**
     * 影片评分
     */
    SCORE("DRE-subject-score"),

    /**
     * 影片描述
     */
    DESC("DRE-subject-desc");

    companion object {
        fun obtain(clazz: String?): MovieClass? {
            return when(clazz) {
                WRAPPER.clazz -> WRAPPER
                ITEM.clazz -> ITEM
                COVER.clazz -> COVER
                INFO.clazz -> INFO
                TITLE.clazz -> TITLE
                RATING.clazz -> RATING
                SCORE.clazz -> SCORE
                DESC.clazz -> DESC
                else -> null
            }
        }
    }
}