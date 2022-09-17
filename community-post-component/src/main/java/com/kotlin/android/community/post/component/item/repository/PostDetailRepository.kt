package com.kotlin.android.community.post.component.item.repository

import android.text.TextUtils
import androidx.collection.arrayMapOf
import com.kotlin.android.api.ApiResult
import com.kotlin.android.comment.component.bean.DetailBaseExtend
import com.kotlin.android.comment.component.bean.UgcCommonBarBean
import com.kotlin.android.comment.component.repository.DetailBaseRepository
import com.kotlin.android.app.data.annotation.GROUP_ROLE_BLACKLIST
import com.kotlin.android.app.data.entity.community.content.CommunityContent
import com.kotlin.android.app.data.entity.community.content.CommunityContent.Companion.GROUP_JOIN_BLACK_NAME
import com.kotlin.android.ktx.ext.orZero
import com.kotlin.android.mtime.ktx.GlobalDimensionExt
import com.kotlin.android.mtime.ktx.formatPublishTime
import com.kotlin.android.retrofit.getRequestBody
import com.kotlin.android.ugc.detail.component.bean.*
import com.kotlin.android.ugc.detail.component.ui.UGC_TYPE_ARTICLE
import com.kotlin.android.user.isLogin

/**
 * create by lushan on 2020/11/2
 * description: 帖子详情
 */
class PostDetailRepository : DetailBaseRepository() {

    /**
     * 加载pk帖子详情
     * @param contentId pk帖子id
     * @param type  详情类型
     * @param isPublished 是否是已经发布的
     */
    suspend fun loadDetail(
        contentId: Long,
        type: Long,
        recId: Long
    ): ApiResult<UgcDetailViewBean> {
        val locationId = GlobalDimensionExt.getDigitsCurrentCityId()
        return request(
            converter = { content ->
                val ugcDetailViewBean = UgcDetailViewBean()
//            标题
                if (TextUtils.isEmpty(content.title).not()) {
                    ugcDetailViewBean.titleData =
                        UgcTitleViewBean(content.title.orEmpty(), content.top, content.essence)
                }
//            相关影片
                val reMovie: CommunityContent.RoMovie? = if (content.reObjs?.isNotEmpty() == true) {
                    content.reObjs?.get(0)?.roMovie
                } else {
                    null
                }
// h5内容
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

// 相片卡片
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

//            群组卡片
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

//            标题
                ugcDetailViewBean.commonBar = UgcCommonBarBean(
                    isAlbumType = content.images?.isNotEmpty() == true,
//                    1可评论 2不可评论
                    canComment = isLogin().not() || (content.commentPmsn == 1L && content?.group?.userJoin != GROUP_JOIN_BLACK_NAME && content?.group?.userGroupRole != GROUP_ROLE_BLACKLIST),//允许所有人评论,userPostCommentPmsn当前用户帖子评论全新啊
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
                            ?: false, content.interactive?.userPraised ?: 0L,
//                            pk帖子投票信息
                        if (content.interactive?.userVoted?.isNotEmpty() == true) content.interactive?.userVoted?.get(
                            0
                        )
                            ?: 0L else 0L,
                        content.interactive?.voteStat,
                        content.vote
                    ),
                    commentPmsn = content.commentPmsn,
                    editBtn = content.creatorAuthority?.btnEdit

                )

                ugcDetailViewBean
            },
            api = {
                apiMTime.getCommunityContent(
                    getContentDetailParams(
                        locationId,
                        type,
                        contentId, recId
                    )
                )
            }
        )
    }

    /**
     * 投票
     */
    suspend fun vote(
        objType: Long,
        objId: Long,
        voteId: Long,
        extend: Any
    ): ApiResult<DetailBaseExtend<Any>> {
        val body = getRequestBody(
            arrayMapOf(
                "objType" to objType,
                "objId" to objId,
                "voteIds" to arrayOf(voteId)
            )
        )
        return request(api = { apiMTime.communityVote(body) }, converter = {
            DetailBaseExtend(it, extend)
        })
    }
}