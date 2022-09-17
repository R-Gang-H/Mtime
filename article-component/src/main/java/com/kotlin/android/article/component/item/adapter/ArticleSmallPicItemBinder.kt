package com.kotlin.android.article.component.item.adapter

import com.kotlin.android.article.component.R
import com.kotlin.android.article.component.databinding.ItemArticleSmallPicBinding
import com.kotlin.android.article.component.item.bean.ArticleItem
import com.kotlin.android.ktx.ext.dimension.dp
import com.kotlin.android.ktx.ext.dimension.screenWidth
import com.kotlin.android.widget.adapter.multitype.adapter.binder.MultiTypeBinder

/**
 * @author ZhouSuQiang
 * @email zhousuqiang@126.com
 * @date 2020/7/9
 *
 * 文章小图item
 */
open class ArticleSmallPicItemBinder(articleItem: ArticleItem)
    : ArticleBaseItemBinder<ItemArticleSmallPicBinding>(articleItem) {

    override fun layoutId(): Int {
        return R.layout.item_article_small_pic
    }

    override fun areContentsTheSame(other: MultiTypeBinder<*>): Boolean {
        return other is ArticleSmallPicItemBinder && other.articleItem.id == articleItem.id
    }

    override fun onBindViewHolder(binding: ItemArticleSmallPicBinding, position: Int) {
        super.onBindViewHolder(binding, position)
        binding.mNewsBottom.data = this
    }

}