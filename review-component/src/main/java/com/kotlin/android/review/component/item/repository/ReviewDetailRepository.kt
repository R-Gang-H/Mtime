package com.kotlin.android.review.component.item.repository

import android.text.TextUtils
import com.kotlin.android.api.ApiResult
import com.kotlin.android.comment.component.bean.UgcCommonBarBean
import com.kotlin.android.comment.component.repository.DetailBaseRepository
import com.kotlin.android.app.data.constant.CommConstant
import com.kotlin.android.app.data.entity.image.MovieImage
import com.kotlin.android.mtime.ktx.GlobalDimensionExt
import com.kotlin.android.mtime.ktx.formatPublishTime
import com.kotlin.android.mtime.ktx.getString
import com.kotlin.android.review.component.R
import com.kotlin.android.review.component.item.bean.ReviewDetailViewBean
import com.kotlin.android.ugc.detail.component.bean.*
import com.kotlin.android.ugc.detail.component.ui.UGC_TYPE_REVIEW
import com.kotlin.android.app.data.entity.user.User
import com.kotlin.android.ktx.ext.orZero

/**
 * create by lushan on 2020/8/17
 * description:长短影评repo
 */
class ReviewDetailRepository : DetailBaseRepository() {
    /**
     * 加载电影剧照
     */
    suspend fun loadMovePhoto(movieId: Long): ApiResult<MovieImage> {
        return request { apiMTime.getMovieImages(movieId = movieId) }
    }


    private fun getSpoiler(tags: List<Long>?): String {
//标签 ORIGINAL(1, "原创"), SPOILER(2, "剧透"), COPYRIGHT(3, "版权声明"), DISCLAIMER(4, "免责声明");
        return if (tags?.isNotEmpty() == true && tags.contains(CommConstant.TYPE_CONTENT_SPOILER)) {
            "此影评包含剧透"
        } else {
            ""
        }
    }

    private fun getCopyRight(tags: List<Long>?, nickName: String): String {
        //标签 ORIGINAL(1, "原创"), SPOILER(2, "剧透"), COPYRIGHT(3, "版权声明"), DISCLAIMER(4, "免责声明");
        return if (tags?.isNotEmpty() == true && (tags.contains(CommConstant.TYPE_CONTENT_ORIGINAL) || tags.contains(
                CommConstant.TYPE_CONTENT_COPYRIGHT
            ) || tags.contains(CommConstant.TYPE_CONTENT_DISCLAIMER))
        ) {
            getString(R.string.ugc_detail_component_copyright_format).format(nickName)
        } else {
            ""
        }
    }

    /**
     * 加载文章详情
     */
    suspend fun loadReviewDetail(
        contentId: Long,
        type: Long,
        recId: Long,
        isShare: Boolean
    ): ApiResult<ReviewDetailViewBean> {
        val locationId = GlobalDimensionExt.getDigitsCurrentCityId()
        return request(
            converter = { content ->
                val reviewDetailViewBean = ReviewDetailViewBean()
                if (TextUtils.isEmpty(content.title).not()) {
                    reviewDetailViewBean.title = content.title.orEmpty()
                }
                reviewDetailViewBean.spoiler = getSpoiler(content.tags)
                reviewDetailViewBean.copyRight =
                    getCopyRight(content.tags, content.createUser?.nikeName.orEmpty())
                reviewDetailViewBean.ugcWebViewBean = UgcWebViewBean(content.body.orEmpty())
                if (content.fcMovie != null) {
                    val releaseDate =
                        if (TextUtils.isEmpty(content.fcMovie?.releaseDate.orEmpty()) && TextUtils.isEmpty(
                                content.fcMovie?.releaseArea.orEmpty()
                            )
                        ) "" else "${content.fcMovie?.releaseDate.orEmpty()}${content.fcMovie?.releaseArea.orEmpty()}上映"
                    reviewDetailViewBean.movieViewBean = MovieViewBean(
                        movieId = content.fcMovie?.id
                            ?: 0L, nameCn = content.fcMovie?.name.orEmpty(),
                        nameEn = content.fcMovie?.nameEn.orEmpty(),
                        picUrl = content.fcMovie?.imgUrl.orEmpty(),
                        mTimeScore = content.fcMovie?.rating.orEmpty(),
                        duration = content.fcMovie?.minutes.orEmpty(),
                        movieType = content.fcMovie?.genreTypes.orEmpty(),
                        releaseDate = releaseDate,
                        movieStatus = content.fcMovie?.btnShow ?: 0L,
                        isLike = false,
                        ugcType = UGC_TYPE_REVIEW
                    )
                }
                reviewDetailViewBean.commonBar = UgcCommonBarBean(
                    isAlbumType = content.images?.isNotEmpty() == true,
                    canComment = content.commentPmsn == 1L,//允许所有人评论
                    createUser = UgcCommonBarBean.CreateUser(
                        content.createUser?.authType
                            ?: 0L,
                        content.createUser?.avatarUrl.orEmpty(),
                        content.createUser?.followed
                            ?: false,
                        content.createUser?.nikeName.orEmpty(),
                        content.createUser?.userId
                            ?: 0L,
                        if (isShare) content.userCreateTime?.show.orEmpty() else formatPublishTime(
                            content.userCreateTime?.stamp
                                ?: 0L
                        ),
                        content.fcRating.orEmpty(),
                        content.fcSubItemRatings ?: listOf(),
                        content.userCreateTime?.stamp
                            ?: 0L,
                        contentStatus = content.creatorAuthority?.creatorContentStatus ?: -1L
                    ),
                    commentSupport = UgcCommonBarBean.CommentSupport(
                        content.interactive?.commentCount
                            ?: 0L, content.interactive?.praiseDownCount ?: 0L,
                        content.interactive?.praiseUpCount
                            ?: 0L, content.interactive?.userCollected
                            ?: false, content.interactive?.userPraised ?: 0
                    ),
                    isShortReview = content.fcType == 2L,
                    commentPmsn = content.commentPmsn,
                    editBtn = content.creatorAuthority?.btnEdit
                )

                reviewDetailViewBean
            },
            api = {
                apiMTime.getCommunityContent(
                    getContentDetailParams(
                        locationId,
                        type,
                        contentId,
                        recId
                    )
                )
            })
    }

    /**
     * 获取用户详情
     */
    suspend fun getMineUserDetail(): ApiResult<User> {
        return request { apiMTime.getAccountDetail(0L) }
    }

}