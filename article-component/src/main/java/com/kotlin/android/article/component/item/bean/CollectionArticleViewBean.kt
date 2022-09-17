package com.kotlin.android.article.component.item.bean

import com.kotlin.android.app.data.ProguardRule

/**
 * create by lushan on 2020/9/14
 * description:收藏文章viewBean
 */
data class CollectionArticleViewBean(
        var articleId: Long = 0L,//文章id
        var articlePic: String = "",//文章图片
        var articleTitle: String = "",//文章标题
        var articleAuth: String = ""//文章作者
) : ProguardRule {
    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as CollectionArticleViewBean

        if (articleId != other.articleId) return false
        if (articlePic != other.articlePic) return false
        if (articleTitle != other.articleTitle) return false
        if (articleAuth != other.articleAuth) return false

        return true
    }

    override fun hashCode(): Int {
        var result = articleId.hashCode()
        result = 31 * result + articlePic.hashCode()
        result = 31 * result + articleTitle.hashCode()
        result = 31 * result + articleAuth.hashCode()
        return result
    }
}