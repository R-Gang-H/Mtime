package com.kotlin.android.community.ui.follow

import androidx.lifecycle.viewModelScope
import com.kotlin.android.api.base.BaseUIModel
import com.kotlin.android.api.base.checkResult
import com.kotlin.android.app.api.viewmodel.CommViewModel
import com.kotlin.android.community.post.component.item.adapter.CommunityPostBinder
import com.kotlin.android.community.repository.FollowRepository
import com.kotlin.android.widget.adapter.multitype.adapter.binder.MultiTypeBinder
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

/**
 * @author ZhouSuQiang
 * @email zhousuqiang@126.com
 * @date 2020/7/17
 */
class FollowViewModel: CommViewModel<CommunityPostBinder>() {

    private val repo by lazy { FollowRepository() }

    private val mFollowUIModel = BaseUIModel<List<MultiTypeBinder<*>>>()

    val uiState = mFollowUIModel.uiState

    private var pageStamp = 0L
    private var pageIndex = 1L
    private var pageSize = 20L

    fun loadData(isLoadMore: Boolean) {
        viewModelScope.launch(Dispatchers.Main)  {
            if (!isLoadMore) {
                pageStamp = 0L
                pageIndex = 1L
            }
            val result = withOnIO {
                repo.loadData(pageStamp, pageIndex, pageSize)
            }

            checkResult(result, isEmpty = {
                if (it.hasMore) {
                    ++pageIndex
                }
                it.list.isNullOrEmpty()
            }, empty = {
                mFollowUIModel.emitUIState(
                        loadMore = isLoadMore,
                        isEmpty = true)
            }, error = {
                mFollowUIModel.emitUIState(
                        error = it,
                        loadMore = isLoadMore)
            }, netError = {
                mFollowUIModel.emitUIState(
                        netError = it,
                        loadMore = isLoadMore)
            }, needLogin = {
                mFollowUIModel.emitUIState(
                        needLogin = true,
                        loadMore = isLoadMore)
            }, success = {
                pageStamp = it.pageStamp
                mFollowUIModel.emitUIState(
                        success = it.list,
                        loadMore = isLoadMore,
                        noMoreData = !it.hasMore
                )
            })
        }
    }
}