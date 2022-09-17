package com.kotlin.android.ugc.detail.component.repository

import com.kotlin.android.api.ApiResult
import com.kotlin.android.app.data.entity.video.VideoPlayList
import com.kotlin.android.comment.component.bean.UgcCommonBarBean
import com.kotlin.android.comment.component.repository.DetailBaseRepository
import com.kotlin.android.ktx.ext.orZero
import com.kotlin.android.mtime.ktx.GlobalDimensionExt
import com.kotlin.android.mtime.ktx.getString
import com.kotlin.android.ugc.detail.component.R
import com.kotlin.android.ugc.detail.component.bean.*
import com.kotlin.android.ugc.detail.component.binder.UgcLinkActorBinder
import com.kotlin.android.ugc.detail.component.binder.UgcLinkMovieBinder
import com.kotlin.android.ugc.detail.component.binder.UgcLinkTitleBinder
import com.kotlin.android.widget.adapter.multitype.adapter.binder.MultiTypeBinder

/**
 * create by lushan on 2022/3/17
 * des:音视频详情页repository
 **/
class UgcMediaRepository : DetailBaseRepository() {

    suspend fun loadUgcDetail(
        contentId: Long,
        type: Long,
        recId: Long,
    ): ApiResult<UgcMediaViewBean> {
        val locationId = GlobalDimensionExt.getDigitsCurrentCityId()
        return request(
            converter = { content ->
                UgcMediaViewBean().apply {
                    if (content.covers?.isNotEmpty() == true) {
                        bgImg = content.covers?.get(0)?.imageUrl.orEmpty()
                    }
                    videoShowType = content.video?.showType.orZero()
                    videoBean = VideoPlayList(content.video?.videoResolutions)

                    mediaInfoBean = UgcMediaInfoViewBean(
                        userId = content.createUser?.userId.orZero(),
                        userName = content.createUser?.nikeName.orEmpty(),
                        userImg = content.createUser?.avatarUrl.orEmpty(),
                        userAuthType = content.createUser?.authType.orZero(),
                        fansCount = content.createUser?.fansCount.orZero(),
                        isFollow = content.createUser?.followed == true,
                        title = content.title.orEmpty(),
                        content = content.body.orEmpty(),
                        createTime = content.userCreateTime?.stamp.orZero(),
                        playCount = content.interactive?.viewCount.orZero()
                    )

                    audioViewBean = UgcAudioViewBean(
                        contentId,
                        bgImg,
                        content.audio?.audioUrl.orEmpty(),
                        content.title.orEmpty()
                    )
                    commonBar = UgcCommonBarBean(
                        canComment = content.commentPmsn == 1L,//允许所有人评论
                        isAlbumType = false,
                        createUser = UgcCommonBarBean.CreateUser(
                            contentStatus = content.creatorAuthority?.creatorContentStatus ?: -1L
                        ),
                        commentPmsn = content.commentPmsn,
                        commentSupport = UgcCommonBarBean.CommentSupport(
                            content.interactive?.commentCount
                                ?: 0L, content.interactive?.praiseDownCount ?: 0L,
                            content.interactive?.praiseUpCount
                                ?: 0L, content.interactive?.userCollected
                                ?: false, content.interactive?.userPraised ?: 0L
                        ),
                        editBtn = content.creatorAuthority?.btnEdit
                    )

                    ugcLinkBinderList = mutableListOf()
                    val movieBinderList = mutableListOf<MultiTypeBinder<*>>()
                    val personBinderList = mutableListOf<MultiTypeBinder<*>>()
                    content.reObjs?.forEach {
                        val roMovie = it.roMovie
                        if (roMovie != null) {
                            movieBinderList.add(
                                UgcLinkMovieBinder(LinkMovieViewBean.buildLinkMovie(roMovie))
                            )
                        }
                        val roPerson = it.roPerson
                        if (roPerson != null) {
                            personBinderList.add(UgcLinkActorBinder(LinkActorViewBean.build(roPerson)))
                        }
                    }
                    if (movieBinderList.isNotEmpty()) {
                        ugcLinkBinderList.add(UgcLinkTitleBinder(getString(R.string.ugc_detail_movie)))
                        ugcLinkBinderList.addAll(movieBinderList)
                    }
                    if (personBinderList.isNotEmpty()) {
                        ugcLinkBinderList.add(UgcLinkTitleBinder(getString(R.string.ugc_detail_actor)))
                        ugcLinkBinderList.addAll(personBinderList)
                    }

                }
            }
        ) {
            apiMTime.getCommunityContent(
                getContentDetailParams(
                    locationId,
                    type,
                    contentId,
                    recId
                )
            )
        }
    }
}