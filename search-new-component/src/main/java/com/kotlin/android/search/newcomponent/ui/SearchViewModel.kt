package com.kotlin.android.search.newcomponent.ui

import com.kotlin.android.api.base.BaseUIModel
import com.kotlin.android.core.BaseViewModel
import com.kotlin.android.app.data.entity.search.Movie

/**
 *
 * Created on 2020/10/14.
 *
 * @author o.s
 */
class SearchViewModel : BaseViewModel() {

//    private val repo by lazy { SearchRepository() }
//
    private val uiModel by lazy { BaseUIModel<List<Movie>>() }
    val uiState = uiModel.uiState

//    fun searchMovie(
//            keyword: String,
//            isShowLoading: Boolean
//    ) {
//        launchOnUI {
//            uiModel.emitUIState(showLoading = isShowLoading)
//            val result = withOnIO {
//                repo.searchMovie(keyword)
//            }
//            uiModel.checkResultAndEmitUIState(result)
//        }
//    }
}