package com.kotlin.android.app.router.provider.review

import com.kotlin.android.app.router.path.RouterProviderPath
import com.kotlin.android.router.annotation.RouteProvider
import com.kotlin.android.router.provider.IBaseProvider

/**
 * create by lushan on 2020/8/10
 * description: 影评provider
 */
@RouteProvider(path = RouterProviderPath.Provider.PROVIDER_REVIEW)
interface IReviewProvider : IBaseProvider {
    /**
     * 跳转到影评详情页面
     * @param recId 记录id
     * @param contentId 如果是发布过的传内容id 如果没有发布传记录id
     * @param type 1.日志 2.帖子 3.影评 4.文章
     *
     */
    fun startReviewDetail(
        contentId: Long,
        type: Long,
        recId: Long = 0L,
        needToComment: Boolean = true
    )

    /**
     * 跳转到影评分享页面
     * @param contentId 影评id
     * @param   isPublished 是否是审核过的
     */
    fun startReviewShare(
        contentId: Long,
        isPublished: Boolean = true,
        isShowEditBtn: Boolean = false
    )

    /**
     * 影片长影评列表页
     * @param movieId 影片Id
     * @param title   影片名称
     */
    fun startMovieReviewList(movieId: String, title: String)

    /**
     * 影片短影评列表页
     * @param movieId 影片Id
     * @param title   影片名称
     */
    fun startMovieShortCommentList(movieId: String, title: String)

    /**
     * 影片评分详情
     * @param movieId 影片ID
     */
    fun startMovieRatingDetail(movieId: Long)

}