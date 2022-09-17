package com.kotlin.android.app.api.api

import com.kotlin.android.api.ApiResponse
import com.kotlin.android.app.data.entity.message.*
import okhttp3.RequestBody
import retrofit2.http.*

/**
 * Created by zhaoninglongfei on 2022/3/18
 * 消息中心 api
 */
interface ApiMessage {

    companion object {

        //未读消息数目
        const val MESSAGE_GET_UNREAD_COUNT = "/common/message/unread_count.api"

        //清除未读消息(/message/clear.api)
        const val MESSAGE_GET_CLEAR = "/common/message/clear.api"

        //获取点赞列表
        const val MESSAGE_GET_PRAISE_LIST = "/common/message/praise_list.api"

        //获取粉丝列表
        const val MESSAGE_GET_USER_FANS = "/common/message/user_fans.api"

        //获取指定消息的点赞人列表
        const val MESSAGE_GET_PRAISE_USERS = "/common/message/praise/users.api"

        //获取评论列表
        const val MESSAGE_GET_COMMENT_LIST = "/common/message/comment_list.api"

        //删除指定评论
        const val MESSAGE_GET_DEL_COMMENT = "/common/message/comment/del.api"

        //获取电影上映通知列表
        const val MESSAGE_GET_MOVIE_REMIND_LIST = "/common/message/movie_release.api"

        //黑名单列表
        const val MESSAGE_GET_BLOCK_LIST = "/im/block_list.api"

        //获取好友是否在黑名单
        const val MESSAGE_GET_BLOCK_STATUS = "/im/get_block_status.api"

        //拉黑/取消拉黑
        const val MESSAGE_GET_BLOCK = "/im/block.api"

        //分页查询用户的关注列表
        const val MESSAGE_GET_USER_FOLLOW_LIST = "/community/user_follow_list.api"
    }

    /**
     * 消息通知api - 未读消息数目（/message/unreadCount.api）
     * GET
     */
    @GET(MESSAGE_GET_UNREAD_COUNT)
    suspend fun getUnreadCount(): ApiResponse<UnreadCountResult>


    /**
     * 清除未读消息
     * @param type 【可选】消息类型,小于等于0 或者不写的话清空所有类型消息数
     */
    @POST(MESSAGE_GET_CLEAR)
    suspend fun postClearMessages(
        @Body body: RequestBody
    ): ApiResponse<ClearResult>

    /**
     * 获取消息点赞列表(/common/message/praiseList.api)
     * GET
     * @param nextStamp 分页标识
     * @param pageSize  每页大小，默认20，最大20
     */
    @GET(MESSAGE_GET_PRAISE_LIST)
    suspend fun getPraiseListNew(
        @Query("nextStamp") nextStamp: String?,
        @Query("pageSize") pageSize: Long?
    ): ApiResponse<PraiseListResult>

    /**
     * 用户粉丝通知(/common/message/user_fans.api)
     * GET
     * @param nextStamp 分页标识
     * @param pageSize  每页大小，默认20，最大20
     */
    @GET(MESSAGE_GET_USER_FANS)
    suspend fun getUserFans(
        @Query("nextStamp") nextStamp: String?,
        @Query("pageSize") pageSize: Long?
    ): ApiResponse<UserFansListResult>

    /**
     * 获取指定消息的点赞人列表(/common/message/praise/users.api)
     * GET
     * @param messageId    消息id
     * @param nextStamp    分页标识
     * @param pageSize     每页大小，默认20，最大20
     */
    @GET(MESSAGE_GET_PRAISE_USERS)
    suspend fun getPraiseUsers(
        @Query("messageId") messageId: String,
        @Query("nextStamp") nextStamp: String?,
        @Query("pageSize") pageSize: Long?
    ): ApiResponse<PraiseUserResult>

    /**
     * 获取消息评论列表(/common/message/commentList.api)
     * GET
     * @param nextStamp 分页标识
     * @param pageSize  每个页面的数量
     */
    @GET(MESSAGE_GET_COMMENT_LIST)
    suspend fun getCommentListNew(
        @Query("nextStamp") nextStamp: String?,
        @Query("pageSize") pageSize: Long?
    ): ApiResponse<CommentListResult>

    /**
     * 删除指定评论(/common/message/commentList.api)
     * GET
     * @param messageId    消息id
     */
    @POST(MESSAGE_GET_DEL_COMMENT)
    suspend fun delComment(
//        @Query("messageId") messageId: String,
        @Body body: RequestBody
    ): ApiResponse<DelCommentResult>

    /**
     * 电影上映通知列表
     * GET
     * @param nextStamp 分页标识
     * @param pageSize  每个页面的数量
     */
    @GET(MESSAGE_GET_MOVIE_REMIND_LIST)
    suspend fun getMovieRemindList(
        @Query("nextStamp") nextStamp: String?,
        @Query("pageSize") pageSize: Long?
    ): ApiResponse<MovieRemindResult>

    /**
     * 黑名单列表
     * GET   /im/blockList.api
     * @param nextStamp 分页标识
     * @param pageSize  每个页面的数量
     */
    @GET(MESSAGE_GET_BLOCK_LIST)
    suspend fun getBlockList(
        @Query("nextStamp") nextStamp: String?,
        @Query("pageSize") pageSize: Long?
    ): ApiResponse<BlockListResult>

    /**
     * 获取好友是否在黑名单
     * GET /im/getBlockStatus.api
     * @param userId【必填】用户ID
     */
    @GET(MESSAGE_GET_BLOCK_STATUS)
    suspend fun getBlockStatus(
        @Query("userId") userId: Long,
    ): ApiResponse<BlockStatusResult>

    /**
     * 拉黑/取消拉黑
     * GET /im/block.api
     * @param userId 【必填】被拉黑/被取消拉黑用户ID
     * @param action 【必填】操作动作： 1:拉黑 2:取消拉黑
     */
    @POST(MESSAGE_GET_BLOCK)
    suspend fun actionBlock(
        @Body body: RequestBody
    ): ApiResponse<BlockResult>

    /**
     * 分页游标查询用户的关注列表
     * GET
     * @param userId    用户id
     * @param nextStamp 分页标识
     * @param pageSize  每个页面的数量
     */
    @GET(MESSAGE_GET_USER_FOLLOW_LIST)
    suspend fun getUserFollowList(
        @Query("userId") userId: Long,
        @Query("nextStamp") nextStamp: String?,
        @Query("pageSize") pageSize: Long?
    ): ApiResponse<UserFollowListResult>
}