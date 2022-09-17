package com.kotlin.android.message.ui.privateChat

import com.kotlin.android.api.base.BaseUIModel
import com.kotlin.android.api.base.BinderUIModel
import com.kotlin.android.api.base.call
import com.kotlin.android.app.data.entity.message.UserFollowListResult
import com.kotlin.android.core.BaseViewModel
import com.kotlin.android.message.repository.MessageRepository
import com.kotlin.android.message.ui.privateChat.viewBean.PrivateChatViewBean
import com.kotlin.android.message.widget.EmptyViewBinder
import com.kotlin.android.user.UserManager
import com.kotlin.android.widget.adapter.multitype.adapter.binder.MultiTypeBinder

/**
 * Created by zhaoninglongfei on 2022/3/15
 *
 */
class PrivateChatViewModel : BaseViewModel() {
    private val repo = MessageRepository()

    private val privateChatUiModel =
        BinderUIModel<UserFollowListResult, List<MultiTypeBinder<*>>>()
    val privateChatUiState = privateChatUiModel.uiState

    private val emptyStateUiModel = BaseUIModel<MultiTypeBinder<*>>()
    val emptyStateUiState = emptyStateUiModel.uiState

    fun loadFollowList(isRefresh: Boolean) {
        call(
            uiModel = privateChatUiModel,
            isShowLoading = isRefresh,
            isRefresh = isRefresh,
            converter = {
                PrivateChatViewBean.convertToItemPrivateChatBinders(it)
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
            repo.loadUserFollowList(
                userId = UserManager.instance.userId,
                nextStamp = privateChatUiModel.pageStamp,
                pageSize = privateChatUiModel.pageSize
            )
        }
    }


    fun loadEmptyView() {
        emptyStateUiModel.emitUIState(
            success = EmptyViewBinder()
        )
    }
}