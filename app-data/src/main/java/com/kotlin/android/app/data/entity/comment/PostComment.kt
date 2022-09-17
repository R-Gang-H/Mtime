package com.kotlin.android.app.data.entity.comment

/**
 * POST 提交请求评论列表页
 *
 * Created on 2021/7/16.
 *
 * @author o.s
 */
data class PostComment(
    var objType: Long,
    var objId: Long,
    var optId: Long? = null,
    var sort: Long = 1,
    var pageIndex: Long,
    var pageSize: Long,
)
