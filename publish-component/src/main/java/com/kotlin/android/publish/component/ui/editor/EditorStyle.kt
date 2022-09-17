package com.kotlin.android.publish.component.ui.editor

/**
 *
 * Created on 2022/4/20.
 *
 * @author o.s
 */
enum class EditorStyle(val type: Long) {

    /**
     * 日志
     */
    JOURNAL(1),

    /**
     * 帖子/种草
     */
    POST(2),

    /**
     * 影评
     */
    FILM_COMMENT(3),

    /**
     * 文章
     */
    ARTICLE(4),

    /**
     * 视频
     */
    VIDEO(5),

    /**
     * 音频
     */
    AUDIO(6),
}