package com.kotlin.android.app.data.entity.movie

import com.kotlin.android.app.data.ProguardRule

data class Comment(
    val commentCount: Long? = null, // 评论回复数量
    val commentId: Long? = null, // 评论Id
    val commentImg: String? = null, // 评论图片
    val commentTime: Long? = null, // 评论时间（时间戳，单位秒）
    val content: String? = null, // 评论内容
    val type: Long? = null // 评论类型（长影评写死1，短影评写死2）
) : ProguardRule