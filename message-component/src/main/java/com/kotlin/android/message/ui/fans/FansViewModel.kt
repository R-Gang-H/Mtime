package com.kotlin.android.message.ui.fans

import androidx.lifecycle.viewModelScope
import com.kotlin.android.api.base.BaseUIModel
import com.kotlin.android.api.base.BinderUIModel
import com.kotlin.android.api.base.call
import com.kotlin.android.api.base.checkResult
import com.kotlin.android.app.data.entity.common.CommBizCodeResult
import com.kotlin.android.app.data.entity.message.UserFansListResult
import com.kotlin.android.core.BaseViewModel
import com.kotlin.android.message.repository.MessageRepository
import com.kotlin.android.message.ui.fans.viewBean.FansViewBean
import com.kotlin.android.message.widget.EmptyViewBinder
import com.kotlin.android.widget.adapter.multitype.adapter.binder.MultiTypeBinder
import kotlinx.coroutines.launch

/**
 * Created by zhaoninglongfei on 2022/3/15
 *
 */
class FansViewModel : BaseViewModel() {

    private val repo = MessageRepository()

    private val fansUiModel = BinderUIModel<UserFansListResult, List<MultiTypeBinder<*>>>()
    val fansUiState = fansUiModel.uiState

    private val followUserUIModel = BaseUIModel<CommBizCodeResult>()
    val followUserUiState = followUserUIModel.uiState

    private val emptyStateUiModel = BaseUIModel<MultiTypeBinder<*>>()
    val emptyStateUiState = emptyStateUiModel.uiState

    fun loadFansList(isRefresh: Boolean) {
        call(
            uiModel = fansUiModel,
            isShowLoading = isRefresh,
            isRefresh = isRefresh,
            converter = { result ->
                FansViewBean.convertToItemFansBinders(
                    result = result,
                    followUser = { action, userId, callback ->
                        followUser(action, userId, callback)
                    })
            },
            pageStamp = {
                it.nextStamp
            },
            isEmpty = {
                isRefresh && it.items.isNullOrEmpty()
            },
            hasMore = {
                it.hasNext ?: false
            }
        ) {
            repo.loadUserFans(
                nextStamp = fansUiModel.pageStamp,
                pageSize = fansUiModel.pageSize
            )
        }
    }

    private fun followUser(action: Long, userId: Long, callback: FollowSuccessCallback) {
        viewModelScope.launch(main) {
            followUserUIModel.emitUIState(showLoading = true)

            val result = withOnIO {
                repo.followUser(action, userId)
            }
            checkResult(result,
                success = {
                    followUserUIModel.emitUIState(success = it)
                    callback.followSuccess()
                },
                error = { followUserUIModel.emitUIState(error = it) },
                netError = { followUserUIModel.emitUIState(netError = it) })
        }
    }

    fun loadEmptyView() {
        emptyStateUiModel.emitUIState(
            success = EmptyViewBinder()
        )
    }

    // 关注/取消关注成功
    interface FollowSuccessCallback {
        fun followSuccess()
    }
}