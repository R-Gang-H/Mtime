package com.kotlin.android.ugc.detail.component.provider

import android.content.Context
import android.os.Bundle
import androidx.collection.arrayMapOf
import com.alibaba.android.arouter.facade.annotation.Route
import com.kotlin.android.app.data.annotation.*
import com.kotlin.android.router.RouterManager
import com.kotlin.android.router.ext.put

import com.kotlin.android.app.router.path.RouterActivityPath
import com.kotlin.android.app.router.path.RouterProviderPath
import com.kotlin.android.app.router.provider.article.IArticleProvider
import com.kotlin.android.app.router.provider.community_post.ICommunityPostProvider
import com.kotlin.android.app.router.provider.review.IReviewProvider
import com.kotlin.android.app.router.provider.ugc.IUgcProvider
import com.kotlin.android.router.ext.getProvider
import com.kotlin.android.ugc.detail.component.ui.*
import com.kotlin.android.ugc.detail.component.ui.album.UgcAlbumActivity


/**
 * create by lushan on 2020/8/10
 * description:
 */
@Route(path = RouterProviderPath.Provider.PROVIDER_UGC_DETAIL)
class UgcDetailProvider : IUgcProvider {
    override fun startUgcDetail(
        contentId: Long,
        ugcType: Long,
        recId: Long,
        needToComment: Boolean
    ) {
        Bundle().apply {
            put(
                arrayMapOf(
                    UGC_DETAIL_TYPE to ugcType,
                    UGC_DETAIL_CONTENT_ID to contentId,
                    UGC_DETAIL_REC_ID to recId,
                    UGC_DETAIL_NEED_TO_COMMENT to needToComment
                )
            )
        }.also {
            RouterManager.instance.navigation(
                RouterActivityPath.UgcDetail.PAGE_UGC_DETAIL_ACTIVITY,
                it
            )
        }
    }

    /**
     * 跳转到相册详情
     */
    override fun startAlbumDetail(albumId: Long) {
        Bundle().apply {
            put(UgcAlbumActivity.KEY_ALBUM_ID, albumId)
        }.also {
            RouterManager.instance.navigation(
                RouterActivityPath.UgcDetail.PAGE_UGC_DETAIL_ALBUM_ACTIVITY,
                it
            )
        }
    }


    /**
     * 跳转到对应详情页面
     * @param contentId 如果是发布过的传内容id 如果没有发布传记录id
     * @param type 1.日志 2.帖子 3.影评 4.文章 5.视频 6.音频
     * @param recId 记录id 获取副本传递，如果没有传0即可
     */
    override fun launchDetail(
        @ContentType contentId: Long,
        type: Long,
        recId: Long,
        needToComment: Boolean
    ) {

        when (type) {
            CONTENT_TYPE_JOURNAL -> {//日志
                getProvider(IUgcProvider::class.java) {
                    startUgcDetail(contentId, type, recId, needToComment)
                }
            }
            CONTENT_TYPE_POST -> {//帖子
                getProvider(ICommunityPostProvider::class.java) {
                    startPostDetail(contentId, type, recId, needToComment)
                }
            }
            CONTENT_TYPE_FILM_COMMENT -> {//影评
                getProvider(IReviewProvider::class.java) {
                    startReviewDetail(contentId, type, recId, needToComment)
                }
            }

            CONTENT_TYPE_ARTICLE -> {//文章
                getProvider(IArticleProvider::class.java) {
                    startActicleActivity(contentId, type, recId, needToComment)
                }
            }
            CONTENT_TYPE_VIDEO, CONTENT_TYPE_AUDIO -> {//音视频
                startUgcMediaDetail(contentId, type, recId, needToComment)
            }

        }
    }

    override fun startUgcMediaDetail(
        contentId: Long,
        ugcType: Long,
        recId: Long,
        needToComment: Boolean
    ) {
        Bundle().apply {
            put(
                arrayMapOf(
                    UGC_DETAIL_TYPE to ugcType,
                    UGC_DETAIL_CONTENT_ID to contentId,
                    UGC_DETAIL_REC_ID to recId,
                    UGC_DETAIL_NEED_TO_COMMENT to needToComment
                )
            )
        }.also {
            RouterManager.instance.navigation(
                RouterActivityPath.UgcDetail.PAGE_UGC_MEDIA_DETAIL_ACTIVITY,
                it
            )
        }
    }

    override fun init(context: Context?) {

    }
}