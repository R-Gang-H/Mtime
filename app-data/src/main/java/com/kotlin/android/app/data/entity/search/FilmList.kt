package com.kotlin.android.app.data.entity.search

import com.kotlin.android.app.data.ProguardRule

/**
 * 创建者: vivian.wei
 * 创建时间: 2022/3/29
 * 描述: 搜索结果_片单
 */
data class FilmList(
    var id: Long? = null,           // 片单Id
    var title: String? = null,      // 名称
    var cover: String? = null,      // 封面图
    var coverUrl: String? = null,   // 片单封面图
    var authorImg: String? = null,  // 发布人头像
    var authorId: Long? = null,     // 发布人id
    var authorName: String? = null, // 发布人昵称
    var films: String? = null,      // 影片列表
    var collectNum: Long? = null,   // 收藏数
    var authTag: Long? = null,      // 认证标识
    var movieNum: Long? = null,     // 影片总数
    var watchedNum: Long? = null,   // 已看数
) : ProguardRule
