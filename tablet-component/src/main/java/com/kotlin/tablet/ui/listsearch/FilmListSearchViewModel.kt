package com.kotlin.tablet.ui.listsearch

import com.kotlin.android.api.base.BaseUIModel
import com.kotlin.android.api.base.call
import com.kotlin.android.app.data.entity.filmlist.FilmListEntity
import com.kotlin.android.app.data.entity.filmlist.FilmListSearchEntity
import com.kotlin.android.core.BaseViewModel
import com.kotlin.tablet.repository.FilmListRepository

class FilmListSearchViewModel : BaseViewModel() {
    private val repo by lazy { FilmListRepository() }
    private val listUiModel by lazy { BaseUIModel<FilmListEntity>() }
    val listUiState by lazy { listUiModel.uiState }

    private val searchUIModel by lazy { BaseUIModel<FilmListSearchEntity>() }
    val searchUIState by lazy { searchUIModel.uiState }

    /**
     * 加载列表数据
     */
    fun loadData(
        isRefresh: Boolean,
        category: Long? = null
    ) {
        call(
            uiModel = listUiModel,
            isRefresh = isRefresh,
            pageStamp = {
                it.nextStamp
            }
        ) {
            repo.list(
                category = category,
                pageSize = listUiModel.pageSize,
                nextStamp = listUiModel.pageStamp
            )
        }
    }

    /**
     * 搜索
     */
    fun searchList(keyword: String, filter: Long? = 0L, isRefresh: Boolean) {
        var filterId: String? = null
        if (filter != 0L && filter != null) {
            filterId = "1:$filter"
        }
        call(
            uiModel = searchUIModel,
            isRefresh = isRefresh,
            isEmpty = {
                it.filmListItems.isNullOrEmpty()
            },
            hasMore = {
                it.filmListItems!!.size >= searchUIModel.pageSize
            }
        ) {
            repo.searchList(
                keyWord = keyword,
                pageIndex = searchUIModel.pageIndex,
                pageSize = searchUIModel.pageSize,
                filter = filterId
            )
        }
    }
}