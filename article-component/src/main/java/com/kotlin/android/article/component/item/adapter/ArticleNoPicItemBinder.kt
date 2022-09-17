package com.kotlin.android.article.component.item.adapter

import com.kotlin.android.article.component.R
import com.kotlin.android.article.component.databinding.ItemArticleNoPicBinding
import com.kotlin.android.article.component.databinding.ItemArticleSmallPicBinding
import com.kotlin.android.article.component.item.bean.ArticleItem
import com.kotlin.android.widget.adapter.multitype.adapter.binder.MultiTypeBinder

/**
 * @author ZhouSuQiang
 * @email zhousuqiang@126.com
 * @date 2020/7/9
 *
 * 无图item
 */
open class ArticleNoPicItemBinder(articleItem: ArticleItem)
    : ArticleBaseItemBinder<ItemArticleNoPicBinding>(articleItem) {

    override fun layoutId(): Int {
        return R.layout.item_article_no_pic
    }

    override fun areContentsTheSame(other: MultiTypeBinder<*>): Boolean {
        return other is ArticleNoPicItemBinder && other.articleItem.id == articleItem.id
    }

    override fun onBindViewHolder(binding: ItemArticleNoPicBinding, position: Int) {
        super.onBindViewHolder(binding, position)
        binding.mNewsBottom.data = this
    }
}