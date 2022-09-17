package com.kotlin.android.article.component.item.adapter

import com.kotlin.android.article.component.R
import com.kotlin.android.article.component.databinding.ItemArticleBigPicBinding
import com.kotlin.android.article.component.item.bean.ArticleItem
import com.kotlin.android.widget.adapter.multitype.adapter.binder.MultiTypeBinder

/**
 * @author ZhouSuQiang
 * @email zhousuqiang@126.com
 * @date 2020/7/9
 *
 * 文章大图item
 */
open class ArticleBigPicItemBinder(articleItem: ArticleItem)
    : ArticleBaseItemBinder<ItemArticleBigPicBinding>(articleItem) {
    override fun layoutId(): Int {
        return R.layout.item_article_big_pic
    }

    override fun areContentsTheSame(other: MultiTypeBinder<*>): Boolean {
        return other is ArticleBigPicItemBinder && other.articleItem.id == articleItem.id
    }

    override fun onBindViewHolder(binding: ItemArticleBigPicBinding, position: Int) {
        super.onBindViewHolder(binding, position)
        binding.mNewsBottom.data = this
    }
}