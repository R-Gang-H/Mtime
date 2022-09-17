package com.kotlin.android.app.data.entity.comment

/**
 * 评论列表：展开全部Item
 * 属性根据需要扩展
 *
 * Created on 2021/7/13.
 *
 * @author o.s
 */
data class CommentAll(
    var objId: Long,
    val objTitle: String,
    val type: Long,
    val title: String = "展开全部",
)