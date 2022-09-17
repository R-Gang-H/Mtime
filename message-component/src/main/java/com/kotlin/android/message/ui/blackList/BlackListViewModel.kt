package com.kotlin.android.message.ui.blackList

import androidx.lifecycle.viewModelScope
import com.kotlin.android.api.base.BaseUIModel
import com.kotlin.android.api.base.BinderUIModel
import com.kotlin.android.api.base.call
import com.kotlin.android.api.base.checkResult
import com.kotlin.android.app.data.entity.message.BlockListResult
import com.kotlin.android.app.data.entity.message.BlockResult
import com.kotlin.android.core.BaseViewModel
import com.kotlin.android.message.repository.MessageRepository
import com.kotlin.android.message.ui.blackList.viewBean.BlackListViewBean
import com.kotlin.android.widget.adapter.multitype.adapter.binder.MultiTypeBinder
import kotlinx.coroutines.launch

/**
 * Created by zhaoninglongfei on 2022/3/17
 *
 */
class BlackListViewModel : BaseViewModel() {
    private val repo = MessageRepository()

    private val blackListUiModel =
        BinderUIModel<BlockListResult, List<MultiTypeBinder<*>>>()
    val blackListUiState = blackListUiModel.uiState

    private val removeFromBlacklistUIModel = BaseUIModel<BlockResult>()
    val removeFromBlacklistUiState = removeFromBlacklistUIModel.uiState

    fun loadBlackList(isRefresh: Boolean) {
        call(
            uiModel = blackListUiModel,
            isShowLoading = isRefresh,
            isRefresh = isRefresh,
            converter = { result ->
                BlackListViewBean.convertToItemBlackListBinders(
                    result = result,
                    removeFromBlackList = { userId, action ->
                        removeFromBlacklist(userId, action)
                    })
            },
            isEmpty = {
                isRefresh && it.items.isNullOrEmpty()
            },
            hasMore = {
                it.hasNext ?: false
            },
            pageStamp = {
                it.nextStamp
            }
        ) {
            repo.loadBlockList(
                nextStamp = blackListUiModel.pageStamp,
                pageSize = blackListUiModel.pageSize
            )
        }
    }

    private fun removeFromBlacklist(userId: Long, action: Int) {
        viewModelScope.launch(main) {
            removeFromBlacklistUIModel.emitUIState(showLoading = true)

            val result = withOnIO {
                repo.actionBlock(userId, action)
            }
            checkResult(result,
                success = { removeFromBlacklistUIModel.emitUIState(success = it) },
                error = { removeFromBlacklistUIModel.emitUIState(error = it) },
                netError = { removeFromBlacklistUIModel.emitUIState(netError = it) })
        }
    }
}