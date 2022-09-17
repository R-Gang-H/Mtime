package com.kotlin.android.app.api.api

import com.kotlin.android.api.ApiResponse
import com.kotlin.android.app.data.entity.community.content.ArticleUser
import com.kotlin.android.app.data.entity.community.content.ContentList
import com.kotlin.android.app.data.entity.community.content.ContentTypeCount
import com.kotlin.android.app.data.entity.community.medal.MedalData
import com.kotlin.android.app.data.entity.community.medal.MedalDetail
import okhttp3.RequestBody
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.Query

interface ApiCommunity {

    companion object {
        //创作者内容分页查询
        const val CREATOR_QUERY_CONTENT = "/community/creator/query_content.api"

        //删除内容
        const val DELETE_CONTENT = "/community/delete_content.api"

        //取消审核内容
        const val CANCEL_REVIEW_CONTENT = "/community/cancel_review_content.api"

        //创作者主页
        const val CREATOR_HOME = "/community/creator/home.api"

        //我的勋章首页
        const val CREATOR_MEDAL = "/community/creator_medal.api"

        //我的勋章点击详情页
        const val CREATOR_MEDAL_DETAIL = "/community/creator_medal_details.api"

        //查询当前文章用户信息
        const val QUERY_ARTICLE_USER = "/community/article_user.api"
    }

    /**
     * 获取创作者内容列表
     */
    @POST(CREATOR_QUERY_CONTENT)
    suspend fun getCommunityQueryContent(@Body body: RequestBody): ApiResponse<ContentList>

    /**
     * 删除我的内容
     */
    @POST(DELETE_CONTENT)
    suspend fun deleteContent(
        @Query("type") type: Long,
        @Query("contentId") contentId: Long?
    ): ApiResponse<Any>

    /**
     * 撤销我的内容审核
     */
    @POST(CANCEL_REVIEW_CONTENT)
    suspend fun reviewContent(
        @Query("type") type: Long,
        @Query("contentId") contentId: Long?
    ): ApiResponse<Any>

    /**
     * 获取创作者内容各自审核状态的数量
     */
    @GET(CREATOR_HOME)
    suspend fun getCommunityTypeCount(@Query("type") type: Long): ApiResponse<ContentTypeCount>

    /**
     * 获取我的勋章首页
     */
    @GET(CREATOR_MEDAL)
    suspend fun getMyMedal(): ApiResponse<MedalData>

    /**
     * 获取我的勋章详情页
     */
    @GET(CREATOR_MEDAL_DETAIL)
    suspend fun getMyMedalDetail(@Query("medalId") medalId: Long): ApiResponse<MedalDetail>

    /**
     * 查询当前文章用户信息
     */
    @GET(QUERY_ARTICLE_USER)
    suspend fun getQueryArticleUser(): ApiResponse<ArticleUser>
}