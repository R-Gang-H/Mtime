package com.kotlin.android.message.ui.praise.dialog

import androidx.lifecycle.viewModelScope
import com.kotlin.android.api.base.BaseUIModel
import com.kotlin.android.api.base.BinderUIModel
import com.kotlin.android.api.base.call
import com.kotlin.android.api.base.checkResult
import com.kotlin.android.app.data.entity.common.CommBizCodeResult
import com.kotlin.android.app.data.entity.message.PraiseUserResult
import com.kotlin.android.core.BaseViewModel
import com.kotlin.android.message.repository.MessageRepository
import com.kotlin.android.widget.adapter.multitype.adapter.binder.MultiTypeBinder
import kotlinx.coroutines.launch

/**
 * Created by zhaoninglongfei on 2022/4/26
 *
 */
class MultiplePraiseViewModel : BaseViewModel() {

    private val repo = MessageRepository()

    private val multiplePraiseUiModel = BinderUIModel<PraiseUserResult, List<MultiTypeBinder<*>>>()
    val multiplePraiseUiState = multiplePraiseUiModel.uiState

    private val followUserUIModel = BaseUIModel<CommBizCodeResult>()
    val followUserUiState = followUserUIModel.uiState

    fun loadMultiplePraiseList(messageId: String, isRefresh: Boolean) {
        call(
            uiModel = multiplePraiseUiModel,
            isShowLoading = isRefresh,
            isRefresh = isRefresh,
            converter = { result ->
                MultiplePraiseViewBean.convertToItemMultiplePraiseBinders(
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
            repo.loadPraiseUserList(
                messageId = messageId,
                nextStamp = multiplePraiseUiModel.pageStamp,
                pageSize = multiplePraiseUiModel.pageSize
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
                    callback.followSuccess()
                    followUserUIModel.emitUIState(success = it)
                },
                error = { followUserUIModel.emitUIState(error = it) },
                netError = { followUserUIModel.emitUIState(netError = it) })
        }
    }

    // 关注/取消关注成功
    interface FollowSuccessCallback {
        fun followSuccess()
    }
}