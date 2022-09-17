package com.kotlin.android.ugc.detail.component.repository

import android.text.TextUtils
import com.kotlin.android.api.ApiResult
import com.kotlin.android.comment.component.bean.UgcCommonBarBean
import com.kotlin.android.comment.component.repository.DetailBaseRepository
import com.kotlin.android.app.data.entity.community.content.CommunityContent
import com.kotlin.android.ktx.ext.orZero
import com.kotlin.android.mtime.ktx.GlobalDimensionExt
import com.kotlin.android.mtime.ktx.formatPublishTime
import com.kotlin.android.ugc.detail.component.bean.*
import com.kotlin.android.ugc.detail.component.ui.UGC_TYPE_ARTICLE

/**
 * ugc日志详情
 */
class UgcDetailRepository : DetailBaseRepository() {

    //    日志、帖子详情
    suspend fun loadDetail(
        contentId: Long,
        type: Long,
        recId: Long
    ): ApiResult<UgcDetailViewBean> {
        val locationId = GlobalDimensionExt.getDigitsCurrentCityId()
        return request(
            converter = { content ->
                val ugcDetailViewBean = UgcDetailViewBean()
                if (TextUtils.isEmpty(content.title).not()) {
                    ugcDetailViewBean.titleData = UgcTitleViewBean(
                        content.title.orEmpty(),
                        isTop = content.top,
                        isEssence = content.essence
                    )
                }

                val reMovie: CommunityContent.RoMovie? = if (content.reObjs?.isNotEmpty() == true) {
                    content.reObjs?.get(0)?.roMovie
                } else {
                    null
                }
                ugcDetailViewBean.h5Data = UgcWebViewBean(content.body.orEmpty())
                if (content.images?.isNotEmpty() == true) {//此时是图集
                    ugcDetailViewBean.bannerData = content.images?.map {
                        UgcImageViewBean(
                            imageId = it.imageId.orEmpty(),
                            ugcContent = it.imageDesc.orEmpty(),
                            ugcPic = it.imageUrl.orEmpty()
                        )
                    }?.toMutableList()
                        ?: mutableListOf<UgcImageViewBean>()
                }

                content.fcMovie?.apply {
                    val releaseDate =
                        if (TextUtils.isEmpty(content.fcMovie?.releaseDate.orEmpty()) && TextUtils.isEmpty(
                                content.fcMovie?.releaseArea.orEmpty()
                            )
                        ) "" else "${content.fcMovie?.releaseDate.orEmpty()}${content.fcMovie?.releaseArea.orEmpty()}上映"
                    ugcDetailViewBean.movieViewBean = MovieViewBean(
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
                        ugcType = UGC_TYPE_ARTICLE
                    )
                }

                content.group?.apply {//帖子群组
                    ugcDetailViewBean.groupViewBean = TopicFamilyViewBean(
                        familyId = this.id,
                        familyName = this.name.orEmpty(),
                        familyPic = this.groupImgUrl.orEmpty(),
                        familyCount = this.memberCount,
                        familyRule = this.groupDesc.orEmpty(),
                        familyStatus = this.userJoin,
                        familyPostStatus = this.groupAuthority ?: 0L,
                        userPostCommentPmsn = this.userPostCommentPmsn ?: 0L,
                        userGroupRole = this.userGroupRole ?: 0L
                    )
                }

                ugcDetailViewBean.commonBar = UgcCommonBarBean(
                    isAlbumType = content.images?.isNotEmpty() == true,
                    canComment = content.commentPmsn == 1L,//允许所有人评论
                    createUser = UgcCommonBarBean.CreateUser(
                        content.createUser?.authType
                            ?: 0,
                        content.createUser?.avatarUrl.orEmpty(),
                        content.createUser?.followed
                            ?: false,
                        content.createUser?.nikeName.orEmpty(),
                        content.createUser?.userId
                            ?: 0L,
                        formatPublishTime(content.userCreateTime?.stamp),
                        content.fcRating.orEmpty(),
                        contentStatus = content.creatorAuthority?.creatorContentStatus ?: -1L
                    ),
                    commentSupport = UgcCommonBarBean.CommentSupport(
                        content.interactive?.commentCount
                            ?: 0L, content.interactive?.praiseDownCount ?: 0L,
                        content.interactive?.praiseUpCount
                            ?: 0L, content.interactive?.userCollected
                            ?: false, content.interactive?.userPraised ?: 0L
                    ),
                    commentPmsn = content.commentPmsn,
                    editBtn = content.creatorAuthority?.btnEdit
                )

                ugcDetailViewBean
            }
        ) {
            apiMTime.getCommunityContent(getContentDetailParams(locationId, type, contentId, recId))
        }
    }

}