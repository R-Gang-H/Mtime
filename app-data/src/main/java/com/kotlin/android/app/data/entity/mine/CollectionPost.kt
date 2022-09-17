package com.kotlin.android.app.data.entity.mine

import com.kotlin.android.app.data.entity.CommContent
import com.kotlin.android.app.data.ProguardRule

/**
 * create by lushan on 2020/9/15
 * description: 分页查询收藏帖子
 */
data class CollectionPost(
        var items: List<Item>? = mutableListOf(),//电影信息
):CollectionBase(), ProguardRule {
    data class Item(
            var obj:Post? = null//收藏帖子主题
    ): ProguardRule

    data class Post(
            var contentId:Long = 0L,
            var title:String? = "",
            var interactive:CommContent.Interactive? = null//帖子交互数

    ): ProguardRule
}