package com.kotlin.android.app.api.api

import com.kotlin.android.api.ApiResponse
import com.kotlin.android.app.data.entity.comment.CommentDetail
import com.kotlin.android.app.data.entity.comment.CommentId
import com.kotlin.android.app.data.entity.common.CommBizCodeResult
import com.kotlin.android.app.data.entity.community.content.CommentList
import okhttp3.RequestBody
import retrofit2.http.*

/**
 * 评论相关接口
 *
 * Created on 2021/7/15.
 *
 * @author o.s
 */
interface ApiComment {

    companion object {
        /**
         * 评论api
         */
        const val COMMUNITY_SAVE_COMMENT = "/community/comment.api"//评论api - 保存评论(/comment.api)
        const val COMMUNITY_DELETE_COMMENT =
            "/community/delete_comment.api"//评论api - 删除评论(/delete_comment.api)
        const val COMMUNITY_COMMENT_RELEASED =
            "/community/comments/released.api"//评论api - 分页查询已发布评论(/comments/released.api)
        const val COMMUNITY_COMMENT_UNRELEASED =
            "/community/comments/user_unreleased.api"//评论api - 分页查询用户未发布评论(/comments/user_unreleased.api)

        /**
         * 社区交互-点赞api
         */
        const val COMMUNITY_PRAISE_STATE = "/community/praise_stat.api"//社区交互-点赞api - 查询点赞点踩状态(/praise_stat.api)
        const val COMMUNITY_PRAISE_UP = "/community/praise_up.api"//社区交互-点赞api - 点赞(/praise_up.api)
        const val COMMUNITY_PRAISE_DOWN = "/community/praise_down.api"//社区交互-点赞api - 点踩(/praise_down.api)

    }

    /**
     *   评论api - 分页查询已发布评论(/comments/released.api)
     *   post，提交json串，详情见 http://apidocs.mt-dev.com/community-front-api/index.html#api-%E8%AF%84%E8%AE%BAapi-queryReleasedComment
     *   objType    Number      评论主体类型 JOURNAL(1, "日志"), POST(2, "帖子"), FILM_COMMENT(3, "影评"),
     *                          ARTICLE(4, "文章"), ALBUM(5, "相册"), TOPIC_LIST(6, "榜单"), CINEMA(7, "影院"),
     *                          MOVIE_TRAILER(8, "预告片"), LIVE(9, "直播"), CARD_USER(10, "卡片用户"), CARD_SUIT(11, "卡片套装"),
     *  objId       Number      评论主体对象
     *  optId       Number      观点id
     *  sort        Number      排序 USER_CREATE_TIME_DESC(1, "-userCreateTime"), HOT_DESC(2, "-hot,-userCreateTime");
     *  pageIndex  Number
     *  pageSize   Number
     */
    @POST(COMMUNITY_COMMENT_RELEASED)
    suspend fun postReleaseCommentList(
//        @Field("objType") objType: Long,
//        @Field("objId") objId: Long,
//        @Field("optId") optId: Long,
//        @Field("sort") sort: Long,
//        @Field("pageIndex") pageIndex: Long,
//        @Field("pageSize") pageSize: Long,
        @Body body: RequestBody
    ): ApiResponse<CommentList>

    /**
     * 评论api - 分页查询用户未发布评论(/comments/user_unreleased.api)
     * post，提交json串，详情见 http://apidocs.mt-dev.com/community-front-api/index.html#api-%E8%AF%84%E8%AE%BAapi-queryUserUnreleasedComment
     *   objType    Number      评论主体类型 JOURNAL(1, "日志"), POST(2, "帖子"), FILM_COMMENT(3, "影评"),
     *                          ARTICLE(4, "文章"), ALBUM(5, "相册"), TOPIC_LIST(6, "榜单"), CINEMA(7, "影院"),
     *                          MOVIE_TRAILER(8, "预告片"), LIVE(9, "直播"), CARD_USER(10, "卡片用户"), CARD_SUIT(11, "卡片套装"),
     *  objId       Number      评论主体对象
     *  optId       Number      观点id
     *  sort        Number      排序 USER_CREATE_TIME_DESC(1, "-userCreateTime"), HOT_DESC(2, "-hot,-userCreateTime");
     *  pageIndex  Number
     *  pageSize   Number
     */
    @POST(COMMUNITY_COMMENT_UNRELEASED)
    suspend fun postUnReleaseCommentList(
//        @Field("objType") objType: Long,
//        @Field("objId") objId: Long,
//        @Field("optId") optId: Long,
//        @Field("sort") sort: Long,
//        @Field("pageIndex") pageIndex: Long,
//        @Field("pageSize") pageSize: Long,
        @Body body: RequestBody
    ): ApiResponse<CommentList>



    /**
     * 评论api - 保存评论(/comment.api)
     * 详情见：http://apidocs.mt-dev.com/community-front-api/index.html#api-%E8%AF%84%E8%AE%BAapi-saveComment
     * POST (/community/comment.api)
     * commentId    Number   评论id
     * objType      Number   评论主体类型 JOURNAL(1, "日志"), POST(2, "帖子"), FILM_COMMENT(3, "影评"),
     *                          ARTICLE(4, "文章"), ALBUM(5, "相册"), TOPIC_LIST(6, "榜单"), CINEMA(7, "影院"),
     *                          MOVIE_TRAILER(8, "预告片"), LIVE(9, "直播"), CARD_USER(10, "卡片用户"), CARD_SUIT(11, "卡片套装"),
     *  objId       Number    评论主体对象id
     *  optId       Number     观点id
     *  images      Array       图片集合
     *      imageId     String      图片id
     *      imageUrl    String      图片Url
     *      imageFormat String      图片格式
     *      imageDes    String      图片描述
     *
     *  body        String          富文本正文
     *
     *
     * @param  {
    "images": [
    {
    "imageFormat": "WBKHR",
    "imageId": "hoOAbax",
    "imageUrl": "1IIx42p",
    "imageDesc": "PJS4SYR"
    }
    ],
    "objId": 3359,
    "commentId": 257,
    "body": "Np",
    "objType": 2535,
    "optId": 5625
    }
     */
    @POST(COMMUNITY_SAVE_COMMENT)
    suspend fun postSaveComment(
        @Body body: RequestBody
    ): ApiResponse<CommBizCodeResult>

    /**
     * 评论api - 获取评论(/comment.api) 获取评论详情
     * GET (community/comment.api)
     * @param objType       Number      评论主体类型 JOURNAL(1, "日志"), POST(2, "帖子"), FILM_COMMENT(3, "影评"),
     *                                  ARTICLE(4, "文章"), ALBUM(5, "相册"), TOPIC_LIST(6, "榜单"), CINEMA(7, "影院"),
     *                                  MOVIE_TRAILER(8, "预告片"), LIVE(9, "直播"), CARD_USER(10, "卡片用户"), CARD_SUIT(11, "卡片套装"),
     *  @param commentId    Number      评论id
     */
    @GET(COMMUNITY_SAVE_COMMENT)
    suspend fun getCommentDetail(
        @Query("objType") objType: Long,
        @Query("commentId") commentId: Long
    )
            : ApiResponse<CommentDetail>

    /**
     * 评论api - 删除评论(/delete_comment.api)
     * POST（/community/delete_comment.api）
     *  @param objType      Number      评论主体类型 JOURNAL(1, "日志"), POST(2, "帖子"), FILM_COMMENT(3, "影评"),
     *                                  ARTICLE(4, "文章"), ALBUM(5, "相册"), TOPIC_LIST(6, "榜单"), CINEMA(7, "影院"),
     *                                  MOVIE_TRAILER(8, "预告片"), LIVE(9, "直播"), CARD_USER(10, "卡片用户"), CARD_SUIT(11, "卡片套装"),
     *  @param commentId    Number      评论id
     */
    @POST(COMMUNITY_DELETE_COMMENT)
    suspend fun postDeleteComment(
        @Query("objType") objType: Long,
        @Query("commentId") commentId: Long
    ): ApiResponse<CommBizCodeResult>

    /**
     * 社区交互-点赞api - 点赞
     * POST (/community/praise_up.api)
     * @param action    Number  动作 1.点赞 2.取消点赞
     * @param objType   Number  点赞主体类型 JOURNAL(1, "日志"),POST(2, "帖子"),FILM_COMMENT(3, "影评"),ARTICLE(4, "文章"),
     *                           ALBUM(5, "相册"),TOPIC_LIST(6, "榜单"),JOURNAL_COMMENT(101, "日志评论"),POST_COMMENT(102, "帖子评论"),
     *                              FILM_COMMENT_COMMENT(103, "影评评论"),ARTICLE_COMMENT(104, "文章评论"),ALBUM_COMMENT(105, "相册评论"),
     *                          TOPIC_LIST_COMMENT(106, "榜单评论"),CINEMA_COMMENT(107, "影院评论"),JOURNAL_REPLY(201, "日志回复"),POST_REPLY(202, "帖子回复"),
     *                          FILM_COMMENT_REPLY(203, "影评回复"),ARTICLE_REPLY(204, "文章回复"),ALBUM_REPLY(205, "相册回复"),TOPIC_LIST_REPLY(206, "榜单回复"),CINEMA_REPLY(207, "影院回复");
     * @param objId     Number   点赞主体
     */
    @POST(ApiMTime.COMMUNITY_PRAISE_UP)
    suspend fun postPraiseUp(
        @Query("action") action: Long,
        @Query("objType") objType: Long,
        @Query("objId") objId: Long
    ): ApiResponse<CommBizCodeResult>

    /**
     * 社区交互-点赞api - 点踩(/praise_down.api)
     * POST (/community/praise_down.api)
     * @param action    Number  动作 1.点踩 2.取消点踩
     * @param objType   Number  点赞主体类型 JOURNAL(1, "日志"),POST(2, "帖子"),FILM_COMMENT(3, "影评"),ARTICLE(4, "文章"),
     *                           ALBUM(5, "相册"),TOPIC_LIST(6, "榜单"),JOURNAL_COMMENT(101, "日志评论"),POST_COMMENT(102, "帖子评论"),
     *                              FILM_COMMENT_COMMENT(103, "影评评论"),ARTICLE_COMMENT(104, "文章评论"),ALBUM_COMMENT(105, "相册评论"),
     *                          TOPIC_LIST_COMMENT(106, "榜单评论"),CINEMA_COMMENT(107, "影院评论"),JOURNAL_REPLY(201, "日志回复"),POST_REPLY(202, "帖子回复"),
     *                          FILM_COMMENT_REPLY(203, "影评回复"),ARTICLE_REPLY(204, "文章回复"),ALBUM_REPLY(205, "相册回复"),TOPIC_LIST_REPLY(206, "榜单回复"),CINEMA_REPLY(207, "影院回复");
     * @param objId     Number  点踩主体
     */
    @POST(ApiMTime.COMMUNITY_PRAISE_DOWN)
    suspend fun postPraiseDown(
        @Query("action") action: Long,
        @Query("objType") objType: Long,
        @Query("objId") objId: Long
    ): ApiResponse<CommBizCodeResult>

}