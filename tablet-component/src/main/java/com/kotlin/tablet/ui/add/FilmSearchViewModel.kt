package com.kotlin.tablet.ui.add

import com.kotlin.android.api.base.BaseUIModel
import com.kotlin.android.api.base.call
import com.kotlin.android.app.data.entity.monopoly.CommonBizMsgBean
import com.kotlin.android.core.BaseViewModel
import com.kotlin.tablet.adapter.FilmListAddBinder
import com.kotlin.tablet.repository.FilmListRepository

/**
 * 创建者: SunHao
 * 创建时间: 2022/3/15
 * 描述:
 **/
class FilmSearchViewModel : BaseViewModel() {

    private val repo by lazy { FilmListRepository() }
    private val searchUiModel by lazy { BaseUIModel<List<FilmListAddBinder>>() }
    private val addMoviesUiModel by lazy { BaseUIModel<CommonBizMsgBean>() }

    val searchUiState by lazy { searchUiModel.uiState }
    val addMoviesUiState by lazy { addMoviesUiModel.uiState }

    /**
     * 搜索电影
     * @param keyWord 关键字
     */
    fun search(
        keyWord: String,
        isRefresh: Boolean
    ) {
        call(
            searchUiModel,
            isRefresh = isRefresh,
            isEmpty = { it.isNullOrEmpty() },
            hasMore = {
                it.size >= searchUiModel.pageSize
            }
        ) {
            repo.searchFilm(
                keyWord, searchUiModel.pageIndex
            )
        }
    }

    /**
     * 添加电影
     * @param filmListId    Number 片单id
     * @param movieIds    Array 【选填】电影id集合
     */
    fun addMovies(movieIds: List<Long>, filmListId: Long) {
        call(addMoviesUiModel) {
            repo.addMovies(
                movieIds,
                filmListId
            )
        }
    }


}