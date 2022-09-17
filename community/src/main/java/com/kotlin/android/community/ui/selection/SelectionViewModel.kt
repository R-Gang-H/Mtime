package com.kotlin.android.community.ui.selection

import androidx.lifecycle.viewModelScope
import com.kotlin.android.api.base.BaseUIModel
import com.kotlin.android.api.base.checkResult
import com.kotlin.android.app.api.viewmodel.CommViewModel
import com.kotlin.android.community.repository.SelectionRepository
import com.kotlin.android.widget.adapter.multitype.adapter.binder.MultiTypeBinder
import kotlinx.coroutines.launch

/**
 * @author ZhouSuQiang
 * @email zhousuqiang@126.com
 * @date 2020/7/23
 */
class SelectionViewModel: CommViewModel<MultiTypeBinder<*>>() {
    private val repo by lazy { SelectionRepository() }

    private val mSelectionUIModel = BaseUIModel<List<MultiTypeBinder<*>>>()

    val uiState = mSelectionUIModel.uiState

    private var pageIndex = 1L
    private var pageSize = 20L

    fun loadData(isLoadMore: Boolean) {
        viewModelScope.launch(main) {
            if (!isLoadMore) {
                pageIndex = 1L
            }
            val result = withOnIO {
                repo.loadData(pageIndex, pageSize)
            }

            checkResult(result, isEmpty = {
                it.list.isNullOrEmpty()
            }, empty = {
                mSelectionUIModel.emitUIState(
                        loadMore = isLoadMore,
                        isEmpty = true)
            }, error = {
                mSelectionUIModel.emitUIState(
                        error = it,
                        loadMore = isLoadMore)
            }, netError = {
                mSelectionUIModel.emitUIState(
                        netError = it,
                        loadMore = isLoadMore)
            }, needLogin = {
                mSelectionUIModel.emitUIState(
                        needLogin = true,
                        loadMore = isLoadMore)
            }, success = {
                ++pageIndex
                mSelectionUIModel.emitUIState(
                        success = it.list,
                        loadMore = isLoadMore,
                        noMoreData = !it.hasMore
                )
            })
        }
    }
}