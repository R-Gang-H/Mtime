package com.kotlin.android.app.data.entity.mine

import com.kotlin.android.app.data.entity.CommContent
import com.kotlin.android.app.data.ProguardRule

/**
 * create by lushan on 2020/9/14
 * description:分页查询收藏文章
 */
data class CollectionArticle(
        var items: List<Item>? = mutableListOf()
) : CollectionBase(), ProguardRule {
    data class Item(
//            var collectTime: CommContent.UserCreateTime? = null,//收藏时间
            var obj: CollectionSummar?//收藏文章
    ) : ProguardRule

    data class CollectionSummar(
            var contentId: Long = 0L,//文章id
            var title: String? = "",//文章标题
            var images: List<CommContent.Image>? = mutableListOf(),//文章图片集合
            var createUser: CommContent.User? = null//文章作者
    ) : ProguardRule
}