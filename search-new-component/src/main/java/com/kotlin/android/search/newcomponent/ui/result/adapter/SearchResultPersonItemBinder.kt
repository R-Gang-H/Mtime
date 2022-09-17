package com.kotlin.android.search.newcomponent.ui.result.adapter

import com.kotlin.android.ktx.ext.convertToHtml
import com.kotlin.android.ktx.ext.core.getString
import com.kotlin.android.router.ext.getProvider
import com.kotlin.android.app.router.path.RouterProviderPath
import com.kotlin.android.app.router.provider.main.IMainProvider
import com.kotlin.android.search.newcomponent.R
import com.kotlin.android.search.newcomponent.databinding.ItemSearchResultPersonBinding
import com.kotlin.android.search.newcomponent.ui.result.SearchResultConstant
import com.kotlin.android.search.newcomponent.ui.result.bean.PersonItem
import com.kotlin.android.widget.adapter.multitype.adapter.binder.MultiTypeBinder

/**
 * 搜索结果_影人Item
 * @isResult 搜索结果页传true，其他页不用传，UI有区别
 */
class SearchResultPersonItemBinder(val keyword: String, val bean : PersonItem, val isResult: Boolean? = false) : MultiTypeBinder<ItemSearchResultPersonBinding>() {

    companion object {
        const val PERSON_MOVIES_SHOW_COUNT = 3
        const val LIKE_UNIT = "%"
    }

    val mMainProvider: IMainProvider? = getProvider(IMainProvider::class.java)

    override fun layoutId(): Int {
        return R.layout.item_search_result_person
    }

    override fun areContentsTheSame(other: MultiTypeBinder<*>): Boolean {
        return other is SearchResultPersonItemBinder && other.bean == bean
    }

    override fun onBindViewHolder(binding: ItemSearchResultPersonBinding, position: Int){
        // 搜索关键词标红
        var name = if(bean.name.isEmpty()) bean.nameEn else bean.name
        var nameEn = if(bean.name.isEmpty()) "" else bean.nameEn
        binding.mItemSearchResultPersonNameCnTv.convertToHtml(name, SearchResultConstant.MATCH_KEYWORD_COLOR, keyword)
        binding.mItemSearchResultPersonNameEnTv.apply {
            if(nameEn.isEmpty()) {
                text = ""
            } else {
                convertToHtml(nameEn, SearchResultConstant.MATCH_KEYWORD_COLOR, keyword)
            }
        }
        // 代表作
        binding.mItemSearchResultPersonMovieTv?.apply {
            var movies = ""
            bean.personMovies
                .subList(0, bean.personMovies.size.coerceAtMost(PERSON_MOVIES_SHOW_COUNT))
                .map {
                movies += getString(R.string.search_result_person_movie, it)
            }
            text = if(movies.isNotEmpty()) getString(R.string.search_result_person_movies, movies) else ""
        }
        // 喜爱度
        binding.mItemSearchResultPersonLikeTv?.apply {
            text = if(bean.loveDeep > 0) (bean.loveDeep.toInt().toString() + LIKE_UNIT) else ""
        }
    }

}