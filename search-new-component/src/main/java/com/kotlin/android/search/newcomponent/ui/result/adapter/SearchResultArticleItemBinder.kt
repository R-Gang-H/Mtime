package com.kotlin.android.search.newcomponent.ui.result.adapter

import com.kotlin.android.ktx.ext.convertToHtml
import com.kotlin.android.ktx.ext.dimension.screenWidth
import com.kotlin.android.mtime.ktx.formatPublishTime
import com.kotlin.android.router.ext.getProvider
import com.kotlin.android.app.router.path.RouterProviderPath
import com.kotlin.android.app.router.provider.ugc.IUgcProvider
import com.kotlin.android.search.newcomponent.R
import com.kotlin.android.search.newcomponent.databinding.ItemSearchResultArticleBinding
import com.kotlin.android.search.newcomponent.ui.result.SearchResultConstant
import com.kotlin.android.search.newcomponent.ui.result.bean.ArticleItem
import com.kotlin.android.widget.adapter.multitype.adapter.binder.MultiTypeBinder

class SearchResultArticleItemBinder(val keyword: String, val bean : ArticleItem) : MultiTypeBinder<ItemSearchResultArticleBinding>() {

    val mProvider: IUgcProvider? = getProvider(IUgcProvider::class.java)

    override fun layoutId(): Int {
        return R.layout.item_search_result_article
    }

    override fun areContentsTheSame(other: MultiTypeBinder<*>): Boolean {
        return other is SearchResultArticleItemBinder && other.bean == bean
    }

    override fun onBindViewHolder(binding: ItemSearchResultArticleBinding, position: Int){
        // 搜索关键词标红
        binding.mItemSearchResultArticleTitleTv.convertToHtml(bean.articleTitle, SearchResultConstant.MATCH_KEYWORD_COLOR, keyword)
        // 昵称长度：右边不超过屏幕的中间）
        binding.mItemSearchResultArticlePublisherTv.maxWidth = (screenWidth - 60) / 2
        // 发布时间
        binding.mItemSearchResultArticleCreateTimeTv.text = formatPublishTime(bean.publishTime)
    }
}