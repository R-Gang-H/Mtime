package com.kotlin.android.message.ui.comment

import androidx.lifecycle.viewModelScope
import com.kotlin.android.api.base.BaseUIModel
import com.kotlin.android.api.base.BinderUIModel
import com.kotlin.android.api.base.call
import com.kotlin.android.api.base.checkResult
import com.kotlin.android.app.data.entity.message.CommentListResult
import com.kotlin.android.app.data.entity.message.DelCommentResult
import com.kotlin.android.core.BaseViewModel
import com.kotlin.android.message.repository.MessageRepository
import com.kotlin.android.message.ui.comment.viewBean.CommentViewBean
import com.kotlin.android.message.widget.EmptyViewBinder
import com.kotlin.android.widget.adapter.multitype.adapter.binder.MultiTypeBinder
import kotlinx.coroutines.launch

/**
 * Created by zhaoninglongfei on 2022/3/15
 * 评论/回复列表
 */
class CommentViewModel : BaseViewModel() {

    private val repo = MessageRepository()

    private val commentUiModel =
        BinderUIModel<CommentListResult, List<MultiTypeBinder<*>>>()
    val commentUiState = commentUiModel.uiState

    private val delCommentUIModel = BaseUIModel<DelCommentResult>()
    val delCommentUiState = delCommentUIModel.uiState

    private val emptyStateUiModel = BaseUIModel<MultiTypeBinder<*>>()
    val emptyStateUiState = emptyStateUiModel.uiState

    fun loadCommentList(isRefresh: Boolean) {
        call(
            uiModel = commentUiModel,
            isShowLoading = isRefresh,
            isRefresh = isRefresh,
            converter = {
                CommentViewBean.convertToItemCommentBinders(
                    result = it,
                    deleteComment = { message ->
                        deleteComment(message)
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
            repo.loadCommentList(
                nextStamp = commentUiModel.pageStamp,
                pageSize = commentUiModel.pageSize
            )
        }
    }

    private fun deleteComment(messageId: String) {
        viewModelScope.launch(main) {
            delCommentUIModel.emitUIState(showLoading = true)

            val result = withOnIO {
                repo.delComment(messageId)
            }
            checkResult(result,
                success = { delCommentUIModel.emitUIState(success = it) },
                error = { delCommentUIModel.emitUIState(error = it) },
                netError = { delCommentUIModel.emitUIState(netError = it) })
        }
    }

    fun loadEmptyView() {
        emptyStateUiModel.emitUIState(
            success = EmptyViewBinder()
        )
    }
}