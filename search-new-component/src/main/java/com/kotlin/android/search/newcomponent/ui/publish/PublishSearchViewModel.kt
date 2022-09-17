package com.kotlin.android.search.newcomponent.ui.publish

import com.kotlin.android.api.base.BinderUIModel
import com.kotlin.android.api.base.call
import com.kotlin.android.app.data.annotation.SEARCH_PERSON
import com.kotlin.android.app.data.entity.search.UnionSearch
import com.kotlin.android.core.BaseViewModel
import com.kotlin.android.search.newcomponent.repo.SearchRepository
import com.kotlin.android.search.newcomponent.ui.result.bean.MovieItem
import com.kotlin.android.search.newcomponent.ui.result.bean.PersonItem
import com.kotlin.android.widget.adapter.multitype.adapter.binder.MultiTypeBinder

/**
 * 创建者: vivian.wei
 * 创建时间: 2022/4/6
 * 描述:
 */
class PublishSearchViewModel: BaseViewModel() {

    private val repo by lazy { SearchRepository() }

    // 搜索结果列表
    private val mUIModel = BinderUIModel<UnionSearch, MutableList<MultiTypeBinder<*>>>()
    val uIState = mUIModel.uiState

    /**
     * 联合搜索2
     */
    fun unionSearch(isRefresh: Boolean, keyword: String, searchType: Long) {
        call(
                isShowLoading = isRefresh,
                uiModel = mUIModel,
                isRefresh = isRefresh,
                converter = {
                    when (searchType) {
                        SEARCH_PERSON -> {
                            PersonItem.buildPublishSearch(it.persons)
                        }
                        else -> {
                            MovieItem.buildPublishSearch(it.movies)
                        }
                    }
                }
        ) {
            repo.unionSearch(
                    keyword = keyword,
                    searchType = searchType
            )
        }
    }

}