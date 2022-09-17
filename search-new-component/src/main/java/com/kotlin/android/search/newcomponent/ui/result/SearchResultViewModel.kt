package com.kotlin.android.search.newcomponent.ui.result

import androidx.lifecycle.viewModelScope
import com.kotlin.android.api.base.BaseUIModel
import com.kotlin.android.api.base.checkResult
import com.kotlin.android.app.api.viewmodel.CommViewModel
import com.kotlin.android.app.data.annotation.SearchType
import com.kotlin.android.search.newcomponent.repo.SearchRepository
import com.kotlin.android.widget.adapter.multitype.adapter.binder.MultiTypeBinder
import kotlinx.coroutines.launch

/**
 * 整站搜索_结果页ViewModel
 */
class SearchResultViewModel : CommViewModel<MultiTypeBinder<*>>() {

    companion object {
        // 按分类搜每页item数
        const val PAGE_SIZE = 20L
    }

    // 按分类搜页码
    private var mPageIndex = 1L
    private val repo by lazy { SearchRepository() }

    // 按全部搜索
    private val mSearchResultAllUIModel = BaseUIModel<List<MultiTypeBinder<*>>>()
    val searchResultAllUIState = mSearchResultAllUIModel.uiState

    // 按具体分类搜索
    private val mSearchResultTypeUIModel = BaseUIModel<List<MultiTypeBinder<*>>>()
    val searchResultTypeUIState = mSearchResultTypeUIModel.uiState

    /**
     * 按全部搜索
     */
    fun unionSearchByAll(keyword: String, locationId: Long, longitude: Double?, latitude: Double?) {
        viewModelScope.launch(main) {

            val result = withOnIO {
                repo.unionSearchByAll(keyword, locationId, longitude, latitude)
            }

            checkResult(result, isEmpty = {
                it.list.isEmpty()
            }, empty = {
                mSearchResultAllUIModel.emitUIState(loadMore = false, isEmpty = true)
            }, error = {
                mSearchResultAllUIModel.emitUIState(error = it, loadMore = false)
            }, netError = {
                mSearchResultAllUIModel.emitUIState(netError = it, loadMore = false)
            }, needLogin = {
                mSearchResultAllUIModel.emitUIState(needLogin = true, loadMore = false)
            }, success = {
                mSearchResultAllUIModel.emitUIState(success = it.list, loadMore = false,
                    noMoreData = true
                )
            })
        }
    }

    /**
     * 按具体分类搜索
     */
    fun unionSearchByType(keyword: String, @SearchType searchType: Long, locationId: Long,
                 longitude: Double?, latitude: Double? , isLoadMore: Boolean, sortType: Long?) {
        viewModelScope.launch(main) {
            if (!isLoadMore) {
                mPageIndex = 1
            }
            val result = withOnIO {
                repo.unionSearchByType(keyword, mPageIndex, PAGE_SIZE, searchType,
                    locationId, longitude, latitude, sortType)
            }

            checkResult(result, isEmpty = {
                it.list.isEmpty()
            }, empty = {
                mSearchResultTypeUIModel.emitUIState(isEmpty = true, loadMore = isLoadMore)
            }, error = {
                mSearchResultTypeUIModel.emitUIState(error = it, loadMore = isLoadMore)
            }, netError = {
                mSearchResultTypeUIModel.emitUIState(netError = it, loadMore = isLoadMore)
            }, needLogin = {
                mSearchResultTypeUIModel.emitUIState(needLogin = true, loadMore = isLoadMore)
            }, success = {
                ++mPageIndex
                mSearchResultTypeUIModel.emitUIState(success = it.list, loadMore = isLoadMore,
                    noMoreData = !it.hasMore
                )
            })
        }
    }

}