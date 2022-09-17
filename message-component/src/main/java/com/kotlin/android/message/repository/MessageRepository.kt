package com.kotlin.android.message.repository

import androidx.collection.arrayMapOf
import com.kotlin.android.api.ApiResult
import com.kotlin.android.app.api.base.BaseRepository
import com.kotlin.android.app.data.entity.common.CommBizCodeResult
import com.kotlin.android.app.data.entity.message.*
import com.kotlin.android.retrofit.getRequestBody

/**
 * Created by zhaoninglongfei on 2022/3/17
 * 消息中心repo
 */
class MessageRepository : BaseRepository() {

    /**
     * 获取未读消息数
     */
    suspend fun loadUnreadCount(): ApiResult<UnreadCountResult> {
        return request { apiMTime.getUnreadCount() }
    }

    /**
     * 清空所有未读消息
     */
    suspend fun clearAllUnreadMessages(): ApiResult<ClearResult> {
        val params = arrayMapOf<String, Any>("type" to 0)
        return request {
            apiMTime.postClearMessages(
                getRequestBody(params)
            )
        }
    }

    /**
     * 获取点赞列表
     */
    suspend fun loadPraiseList(
        nextStamp: String?,
        pageSize: Long?
    ): ApiResult<PraiseListResult> {
        return request { apiMTime.getPraiseListNew(nextStamp, pageSize) }
    }

    /**
     * 获取粉丝列表
     */
    suspend fun loadUserFans(
        nextStamp: String?,
        pageSize: Long?
    ): ApiResult<UserFansListResult> {
        return request { apiMTime.getUserFans(nextStamp, pageSize) }
    }

    /**
     * 获取指定消息的点赞人列表
     */
    suspend fun loadPraiseUserList(
        messageId: String,
        nextStamp: String?,
        pageSize: Long?
    ): ApiResult<PraiseUserResult> {
        return request { apiMTime.getPraiseUsers(messageId, nextStamp, pageSize) }
    }

    /**
     * 获取评论列表
     */
    suspend fun loadCommentList(
        nextStamp: String?,
        pageSize: Long?
    ): ApiResult<CommentListResult> {
        return request { apiMTime.getCommentListNew(nextStamp, pageSize) }
    }

    /**
     * 删除指定评论
     */
    suspend fun delComment(
        messageId: String,
    ): ApiResult<DelCommentResult> {
        val params = arrayMapOf<String, Any>("messageId" to messageId)
        return request {
            apiMTime.delComment(
                getRequestBody(params)
            )
        }
    }

    /**
     * 获取观影通知列表
     */
    suspend fun loadMovieRemindList(
        nextStamp: String?,
        pageSize: Long?
    ): ApiResult<MovieRemindResult> {
        return request { apiMTime.getMovieRemindList(nextStamp, pageSize) }
    }

    /**
     * 获取黑名单列表
     */
    suspend fun loadBlockList(
        nextStamp: String?,
        pageSize: Long?
    ): ApiResult<BlockListResult> {
        return request { apiMTime.getBlockList(nextStamp, pageSize) }
    }

    /**
     * 获取好友是否在黑名单
     */
    suspend fun loadBlockStatus(userId: Long): ApiResult<BlockStatusResult> {
        return request { apiMTime.getBlockStatus(userId) }
    }

    /**
     * 拉黑/取消拉黑
     */
    suspend fun actionBlock(userId: Long, action: Int): ApiResult<BlockResult> {
        val params = arrayMapOf<String, Any>("userId" to userId, "action" to action)
        return request {
            apiMTime.actionBlock(
                getRequestBody(params)
            )
        }
    }

    /**
     * 查询用户关注的列表
     */
    suspend fun loadUserFollowList(
        userId: Long,
        nextStamp: String?,
        pageSize: Long?
    ): ApiResult<UserFollowListResult> {
        return request { apiMTime.getUserFollowList(userId, nextStamp, pageSize) }
    }

    /**
     * 关注用户
     */
    suspend fun followUser(action: Long, userId: Long): ApiResult<CommBizCodeResult> {
        return request { apiMTime.followUser(action, userId) }
    }
}