package com.kotlin.android.app.data.entity.comment

/**
 *
 * Created on 2021/7/13.
 *
 * @author o.s
 */
data class CommentTitle(
    var count: Long = 0,
    var isNew: Boolean = false
) {
    val countDesc: String
        get() = count.toString()

}