package com.kotlin.android.article.component.item.provider

import android.os.Bundle
import androidx.collection.arrayMapOf
import com.alibaba.android.arouter.facade.annotation.Route
import com.kotlin.android.article.component.item.ui.detail.ArticleDetailActivity
import com.kotlin.android.router.RouterManager
import com.kotlin.android.router.ext.put
import com.kotlin.android.app.router.path.RouterActivityPath
import com.kotlin.android.app.router.path.RouterProviderPath
import com.kotlin.android.app.router.provider.article.IArticleProvider
import com.kotlin.android.ugc.detail.component.ui.UGC_DETAIL_NEED_TO_COMMENT
import com.kotlin.android.ugc.detail.component.ui.UGC_DETAIL_REC_ID

/**
 * create by lushan on 2020/8/20
 * description:
 */
@Route(path = RouterProviderPath.Provider.PROVIDER_ARTICLE)
class ArticleProvider : IArticleProvider {
    override fun startActicleActivity(contentId: Long, type: Long, recId:Long, needToComment: Boolean) {
        Bundle().apply {
            put(arrayMapOf(ArticleDetailActivity.ARTICLE_CONTENT_ID to contentId, ArticleDetailActivity.ARTICLE_TYPE to type,
                    UGC_DETAIL_REC_ID to recId,
                    UGC_DETAIL_NEED_TO_COMMENT to needToComment))
        }.also {
            RouterManager.instance.navigation(RouterActivityPath.Article.PAGE_ARTICLE_DETAIL_ACTIVITY, it)
        }
    }

}