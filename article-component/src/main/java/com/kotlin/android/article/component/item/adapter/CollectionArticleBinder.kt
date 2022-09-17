package com.kotlin.android.article.component.item.adapter

import android.view.View
import com.kotlin.android.article.component.R
import com.kotlin.android.article.component.databinding.ItemCollectionArticleBinding
import com.kotlin.android.article.component.item.bean.CollectionArticleViewBean
import com.kotlin.android.app.data.constant.CommConstant


import com.kotlin.android.app.router.path.RouterProviderPath
import com.kotlin.android.app.router.provider.article.IArticleProvider
import com.kotlin.android.router.ext.getProvider
import com.kotlin.android.widget.adapter.multitype.adapter.binder.MultiTypeBinder

/**
 * create by lushan on 2020/9/14
 * description:文章收藏binder
 */
class CollectionArticleBinder(var bean: CollectionArticleViewBean) : MultiTypeBinder<ItemCollectionArticleBinding>() {

    override fun layoutId(): Int = R.layout.item_collection_article
    override fun areContentsTheSame(other: MultiTypeBinder<*>): Boolean = other is CollectionArticleBinder && other.bean != bean
    override fun onClick(view: View) {
        if (view.id == R.id.articleRootView) {
            val instance:IArticleProvider? = getProvider(IArticleProvider::class.java)
            instance?.startActicleActivity(bean.articleId,CommConstant.PRAISE_OBJ_TYPE_ARTICLE,0L,true)
        } else {
            super.onClick(view)
        }
    }
}