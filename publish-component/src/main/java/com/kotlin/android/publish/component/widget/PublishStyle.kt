package com.kotlin.android.publish.component.widget

import com.kotlin.android.app.data.annotation.PUBLISH_FILM_COMMENT
import com.kotlin.android.app.data.annotation.PUBLISH_JOURNAL
import com.kotlin.android.app.data.annotation.PUBLISH_LONG_FILM_COMMENT
import com.kotlin.android.app.data.annotation.PUBLISH_POST

/**
 * 发布头部视图样式
 *
 * Created on 2020/7/20.
 *
 * @author o.s
 */
enum class PublishStyle(type: Long) {

    /**
     * 日志
     */
    JOURNAL(1),

//    /**
//     * 帖子
//     */
//    POS(2),

    /**
     * 影评（短评）
     */
    FILM_COMMENT(3),

    /**
     * 影评（长评）
     */
    LONG_COMMENT(5);

    companion object {
        fun obtain(type: Long): PublishStyle {
            return when (type) {
                PUBLISH_JOURNAL,
                PUBLISH_POST -> JOURNAL
                PUBLISH_FILM_COMMENT -> FILM_COMMENT
                PUBLISH_LONG_FILM_COMMENT -> LONG_COMMENT
                else -> JOURNAL
            }
        }
    }
}