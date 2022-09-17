package com.kotlin.android.search.newcomponent.ui.result.adapter

import com.kotlin.android.ktx.ext.convertToHtml
import com.kotlin.android.router.ext.getProvider
import com.kotlin.android.app.router.path.RouterProviderPath
import com.kotlin.android.app.router.provider.ugc.IUgcProvider
import com.kotlin.android.search.newcomponent.R
import com.kotlin.android.search.newcomponent.databinding.ItemSearchResultPostBinding
import com.kotlin.android.search.newcomponent.ui.result.SearchResultConstant
import com.kotlin.android.search.newcomponent.ui.result.bean.PostItem
import com.kotlin.android.widget.adapter.multitype.adapter.binder.MultiTypeBinder

class SearchResultPostItemBinder(val keyword: String, val bean : PostItem) : MultiTypeBinder<ItemSearchResultPostBinding>() {

    val mProvider: IUgcProvider? = getProvider(IUgcProvider::class.java)

    override fun layoutId(): Int {
        return R.layout.item_search_result_post
    }

    override fun areContentsTheSame(other: MultiTypeBinder<*>): Boolean {
        return other is SearchResultPostItemBinder && other.bean == bean
    }

    override fun onBindViewHolder(binding: ItemSearchResultPostBinding, position: Int){
        // 搜索关键词标红
        binding.mItemSearchResultPostTitleTv.convertToHtml(bean.objTitle, SearchResultConstant.MATCH_KEYWORD_COLOR, keyword)
    }

    /**
     * 点赞、取消点赞改变
     */
//    fun praiseUpChanged() {
//        bean.isLikeUp = !bean.isLikeUp
//        if (bean.isLikeUp) {
//            bean.likeUp++
//        } else {
//            bean.likeUp--
//        }
//        notifyAdapterSelfChanged()
//    }

}