package com.kotlin.android.app.data.entity.search

import com.kotlin.android.app.data.ProguardRule

/**
 * 创建者: vivian.wei
 * 创建时间: 2022/3/29
 * 描述: 搜索结果_视频
 */
data class Video(
    var id: Long? = null,               // 视频Id
    var title: String? = null,          // 标题
    var cover: String? = null,          // 封面图
    var authorImg: String? = null,      // 发布人头像
    var authorId: Long? = null,         // 发布人id
    var authorName: String? = null,     // 发布人昵称
    var likeNum: Long? = null,          // 点赞数
    var commentNum: Long? = null,       // 评论数
    var createTime: Long? = null,       // 发布时间
    var isLikeUp: Boolean? = null,      // 当前用户是否点赞
    var authTag: Long? = null,          // 认证标识
): ProguardRule
