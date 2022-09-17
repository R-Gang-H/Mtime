package com.kotlin.android.comment.component.bind.viewmodel

import com.kotlin.android.api.base.BaseUIModel
import com.kotlin.android.api.base.call
import com.kotlin.android.comment.component.bind.repository.CommentRepository
import com.kotlin.android.core.BaseViewModel
import com.kotlin.android.app.data.entity.comment.PostComment
import com.kotlin.android.app.data.entity.common.CommBizCodeResult
import com.kotlin.android.app.data.entity.community.content.CommentList

/**
 * 评论 ViewModel
 *
 * Created on 2021/7/15.
 *
 * @author o.s
 */
class CommentViewModel : BaseViewModel() {

    private val repo by lazy { CommentRepository() }

    private val uiModel by lazy { BaseUIModel<CommentList>() }
    private val deleteCommentModel by lazy { BaseUIModel<CommBizCodeResult>() }
    private val praiseUpModel by lazy { BaseUIModel<CommBizCodeResult>() }
    val uiState by lazy { uiModel.uiState }
    val deleteCommentUiState by lazy { deleteCommentModel.uiState }
    val praiseUpUiState by lazy { praiseUpModel.uiState }

    /**
     * 加载评论列表 [isReleased] true：审核通过的， false：未审核
     */
    fun loadCommentList(
        isNewComment: Boolean, // true：最新评论；反之，最热评论
        isReleased: Boolean, // true：审核通过发布的；反之，未审核的
        postComment: PostComment
    ) {
        call(
            uiModel = uiModel,
            isEmpty = {
                it.items.isNullOrEmpty()
            },
            hasMore = {
                it.hasNext
            },
        ) {
            repo.loadCommentList(
                isNewComment = isNewComment,
                isReleased = isReleased,
                postComment = postComment
            )
        }
    }

    /**
     * 删除评论
     */
    fun deleteComment(
        objType: Long,
        commentId: Long
    ) {
        call(
            uiModel = deleteCommentModel
        ) {
            repo.deleteComment(
                objType = objType,
                commentId = commentId
            )
        }
    }

    /**
     * 点赞/取消点赞
     * [action] 动作 1.点赞 2.取消点赞
     */
    fun praiseUp(
        action: Long,
        objType: Long,
        objId: Long
    ) {
        call(
            uiModel = praiseUpModel
        ) {
            repo.praiseUp(
                action = action,
                objType = objType,
                objId = objId
            )
        }
    }

}