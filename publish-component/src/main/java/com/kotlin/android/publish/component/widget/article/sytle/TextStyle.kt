package com.kotlin.android.publish.component.widget.article.sytle

/**
 *
 * Created on 2022/3/23.
 *
 * @author o.s
 */
enum class TextStyle(val bit: Int, val style: String) {

    /**
     * 没有
     */
    NONE(0b0000, ""),

    /**
     * 粗体标签 <strong>
     */
    BOLD(0b0001, "strong;"),

    /**
     * 斜体标签 <em>
     */
    ITALIC(0b0010, "em;"),

    /**
     * <span style="text-decoration: underline;">下划线</span>
     */
    UNDERLINE(0b0100, "text-decoration: underline;"),

    /**
     * <span style="text-decoration: line-through;">删除线</span>
     */
    LINE_THROUGH(0b1000, "text-decoration: line-through;"),

}