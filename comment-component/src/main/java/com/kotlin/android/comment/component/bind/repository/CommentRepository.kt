package com.kotlin.android.comment.component.bind.repository

import com.kotlin.android.api.ApiResult
import com.kotlin.android.app.api.base.BaseRepository
import com.kotlin.android.app.data.entity.comment.PostComment
import com.kotlin.android.app.data.entity.common.CommBizCodeResult
import com.kotlin.android.app.data.entity.community.content.CommentList
import com.kotlin.android.retrofit.toRequestBody

/**
 *
 * Created on 2021/7/15.
 *
 * @author o.s
 */
class CommentRepository : BaseRepository() {

    /**
     * 加载评论列表 [isReleased] true：审核通过的， false：未审核
     */
    suspend fun loadCommentList(
        isNewComment: Boolean,
        isReleased: Boolean,
        postComment: PostComment
    ): ApiResult<CommentList> {
        postComment.sort = if (isNewComment) 1L else 2L
        return if (isReleased) {
            request {
                apiMTime.postReleaseCommentList(
                    postComment.toRequestBody()
                )
            }
        } else {
            request(
                converter = {
                    it.items?.forEach { item ->
                        item.releasedState = false // isReleased == false
                    }
                    it
                }
            ) {
                apiMTime.postUnReleaseCommentList(
                    postComment.toRequestBody()
                )
            }
        }
    }

    /**
     * 删除评论
     */
    suspend fun deleteComment(
        objType: Long,
        commentId: Long
    ): ApiResult<CommBizCodeResult> {
        return request {
            apiMTime.postDeleteComment(
                objType = objType,
                commentId = commentId
            )
        }
    }

    /**
     * 点赞/取消点赞
     * [action] 动作 1.点赞 2.取消点赞
     */
    suspend fun praiseUp(
        action: Long,
        objType: Long,
        objId: Long
    ): ApiResult<CommBizCodeResult> {
        return request {
            apiMTime.postPraiseUp(
                action = action,
                objType = objType,
                objId = objId
            )
        }
    }
}