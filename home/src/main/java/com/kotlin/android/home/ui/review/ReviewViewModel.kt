package com.kotlin.android.home.ui.review

import androidx.lifecycle.viewModelScope
import com.kotlin.android.api.base.BaseUIModel
import com.kotlin.android.api.base.checkResult
import com.kotlin.android.app.api.viewmodel.CommViewModel
import com.kotlin.android.home.repository.ReviewRepository
import com.kotlin.android.widget.adapter.multitype.adapter.binder.MultiTypeBinder
import kotlinx.coroutines.launch

/**
 * @author ZhouSuQiang
 * @email zhousuqiang@126.com
 * @date 2020/7/7
 */
class ReviewViewModel : CommViewModel<MultiTypeBinder<*>>() {
    private val repo by lazy { ReviewRepository() }

    private val mReviewUIModel = BaseUIModel<List<MultiTypeBinder<*>>>()

    val uiState = mReviewUIModel.uiState

    private var pageIndex = 1L
    private var pageSize = 20L

    fun loadData(isLoadMore: Boolean) {
        viewModelScope.launch(main) {
            if (!isLoadMore) {
                pageIndex = 1
            }
            val result = withOnIO {
                repo.loadData(pageIndex, pageSize)
            }

            checkResult(result, isEmpty = {
                it.list.isNullOrEmpty()
            }, empty = {
                mReviewUIModel.emitUIState(
                        loadMore = isLoadMore,
                        isEmpty = true)
            }, error = {
                mReviewUIModel.emitUIState(
                        error = it,
                        loadMore = isLoadMore)
            }, netError = {
                mReviewUIModel.emitUIState(
                        netError = it,
                        loadMore = isLoadMore)
            }, needLogin = {
                mReviewUIModel.emitUIState(
                        needLogin = true,
                        loadMore = isLoadMore)
            }, success = {
                ++pageIndex
                mReviewUIModel.emitUIState(
                        success = it.list,
                        loadMore = isLoadMore,
                        noMoreData = !it.hasMore
                )
            })
        }
    }
}