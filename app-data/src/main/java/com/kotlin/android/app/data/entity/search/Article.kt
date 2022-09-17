package com.kotlin.android.app.data.entity.search

import com.kotlin.android.app.data.ProguardRule
import java.io.Serializable

/**
 * 联合搜索/search/unionSearch
 * 文章
 *
 * Created on 2020/10/15.
 *
 * @author o.s
 */
data class Article(
        val articleId: Long? = null, // 文章Id
        val articleTitle: String? = null, // 文章标题
        val articleSummary: String? = null, // 文章简介
        val commentCount: Long? = null, // 评论数
        val createTime: String? = null, // 文章时间
        val publishTime: Long? = null,  // 发布时间
        val href: String? = null, // 文章链接
        val keyWord: List<String>? = null, // 关键词
        val image: String? = null, // 图片
        val author: Author? = null // 作者
) : ProguardRule, Serializable {
        data class Author(
                val userId: Long? = null,
                val nickName: String? = null
        ): ProguardRule
}