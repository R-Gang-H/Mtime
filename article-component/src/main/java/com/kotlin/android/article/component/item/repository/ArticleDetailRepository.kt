package com.kotlin.android.article.component.item.repository

import android.text.TextUtils
import com.kotlin.android.api.ApiResult
import com.kotlin.android.article.component.R
import com.kotlin.android.article.component.item.bean.ArticleDetailViewBean
import com.kotlin.android.comment.component.bean.UgcCommonBarBean
import com.kotlin.android.comment.component.repository.DetailBaseRepository
import com.kotlin.android.app.data.constant.CommConstant
import com.kotlin.android.article.component.item.bean.ArticleItem
import com.kotlin.android.ktx.ext.orZero
import com.kotlin.android.mtime.ktx.GlobalDimensionExt
import com.kotlin.android.mtime.ktx.formatPublishTime
import com.kotlin.android.mtime.ktx.getString
import com.kotlin.android.ugc.detail.component.bean.*
import com.kotlin.android.ugc.detail.component.binder.*
import com.kotlin.android.ugc.detail.component.ui.UGC_TYPE_ARTICLE
import com.kotlin.android.widget.adapter.multitype.adapter.binder.MultiTypeBinder

/**
 * create by lushan on 2020/8/19
 * description: 文章详情
 */

class ArticleDetailRepository : DetailBaseRepository() {

    /**
     * 加载文章详情
     */
    suspend fun loadArticleDetail(
        contentId: Long,
        type: Long,
        recId: Long
    ): ApiResult<ArticleDetailViewBean> {
        val locationId = GlobalDimensionExt.getDigitsCurrentCityId()
        return request(converter = { content ->
            val articleDetailViewBean = ArticleDetailViewBean()
            if (TextUtils.isEmpty(content.title).not()) {
                articleDetailViewBean.titleData = UgcTitleViewBean(content.title.orEmpty())
            }
            articleDetailViewBean.webContentData = UgcWebViewBean(content.body.orEmpty())
            if (content.fcMovie != null) {
                val releaseDate =
                    if (TextUtils.isEmpty(content.fcMovie?.releaseDate.orEmpty()) && TextUtils.isEmpty(
                            content.fcMovie?.releaseArea.orEmpty()
                        )
                    ) "" else "${content.fcMovie?.releaseDate.orEmpty()}${content.fcMovie?.releaseArea.orEmpty()}上映"
                articleDetailViewBean.movieData = MovieViewBean(
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
            articleDetailViewBean.bannerData = content.images?.map {
                UgcImageViewBean(
                    imageId = it.imageId.orEmpty(),
                    ugcContent = it.imageDesc.orEmpty(),
                    ugcPic = it.imageUrl.orEmpty()
                )
            }?.toMutableList()
                ?: mutableListOf<UgcImageViewBean>()
            articleDetailViewBean.commonBar = UgcCommonBarBean(
                isAlbumType = content.images?.isNotEmpty() == true,
                canComment = content.commentPmsn == 1L,//允许所有人评论
                createUser = UgcCommonBarBean.CreateUser(
                    content.createUser?.authType
                        ?: 0, content.createUser?.avatarUrl.orEmpty(), content.createUser?.followed
                        ?: false, content.createUser?.nikeName.orEmpty(), content.createUser?.userId
                        ?: 0L,
                    formatPublishTime(
                        content.userCreateTime?.stamp
                            ?: 0L
                    ), content.fcRating.orEmpty(),
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
            articleDetailViewBean.copyRight =
                getCopyRight(content.tags, content.createUser?.nikeName.orEmpty())
            val ugcLinkBinderList = mutableListOf<MultiTypeBinder<*>>()
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

            articleDetailViewBean.ugcLinkBinderList.clear()
            articleDetailViewBean.ugcLinkBinderList.addAll(ugcLinkBinderList)


            articleDetailViewBean
        }) {
            apiMTime.getCommunityContent(getContentDetailParams(locationId, type, contentId, recId))
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
     * 加载推荐文章,最多三条
     */
    suspend fun loadRecommendData(
        contentId: Long,
        recId: Long
    ): ApiResult<MutableList<MultiTypeBinder<*>>> {

        return request(converter = { data ->
            val list = mutableListOf<MultiTypeBinder<*>>()
            if (data.items?.isNotEmpty() == true) {
                list.add(UgcLinkTitleBinder(getString(R.string.ugc_detail_recommend)))
                list.addAll(data.items?.map {
                    ArticleItem.converter2ArticleBinder(it, false, true)
                }?.toMutableList() ?: mutableListOf())
            }
            list
        }) {
            apiMTime.getReObjArticleList(contentId, recId, 1, 10)
        }
    }


    /**
     * 进入文章详情上报接口
     */
    suspend fun poplarClick(title: String): ApiResult<Any> {
//        * @param pTyp  Number  影片 1 、  影人 2、  文章 3
//        * @param   sType   Number  影片 0 、 电视剧 1 、 文章、影人 -1
//        * @param   keyword String  影片id 、影人id、文章标题
        return request {
            apiMTime.poplarClick(3, -1, title)
        }
    }

}