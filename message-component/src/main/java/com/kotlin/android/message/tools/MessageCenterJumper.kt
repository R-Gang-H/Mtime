package com.kotlin.android.message.tools

import android.content.Context
import com.kotlin.android.app.router.ext.*
import com.kotlin.android.app.router.provider.card_monopoly.ICardMonopolyProvider
import com.kotlin.android.app.router.provider.comment.ICommentProvider
import com.kotlin.android.app.router.provider.ugc.IUgcProvider
import com.kotlin.android.router.ext.getProvider

/**
 * Created by zhaoninglongfei on 2022/4/24
 * 供消息中心内部往外部跳转使用
 *
 */
object MessageCenterJumper {

    //内容跳转
    fun contentJump(context: Context, contentId: Long, contentType: ContentType?) {
        when (contentType) {
            //日志
            ContentType.JOURNAL -> {
                jumpToUgcDetail(contentId, contentType.type)
            }
            //帖子
            ContentType.POST -> {
                jumpToUgcDetail(contentId, contentType.type)
            }
            //影评
            ContentType.FILM_COMMENT -> {
                jumpToUgcDetail(contentId, contentType.type)
            }
            //文章
            ContentType.ARTICLE -> {
                jumpToUgcDetail(contentId, contentType.type)
            }
            //相册
            ContentType.ALBUM -> {
                jumpPage(
                    context, AppLinkExtra(
                        pageType = PAGE_TYPE_albumDetails,
                        albumId = contentId.toString()
                    )
                )
            }
            //榜单
            ContentType.TOPIC_LIST -> {
                jumpPage(
                    context, AppLinkExtra(
                        pageType = PAGE_TYPE_commonListDetail,
                        listID = contentId.toString()
                    )
                )
            }
            //影院
            ContentType.CINEMA -> {
                jumpPage(
                    context, AppLinkExtra(
                        pageType = PAGE_TYPE_cinemaDetail,
                        cinemaId = contentId.toString()
                    )
                )
            }
            //预告片
            ContentType.MOVIE_TRAILER -> {
                jumpPage(
                    context, AppLinkExtra(
                        pageType = PAGE_TYPE_videoPlayDetail,
                        videoId = contentId.toString()
                    )
                )
            }
            //直播
            ContentType.LIVE -> {
                jumpPage(
                    context, AppLinkExtra(
                        pageType = PAGE_TYPE_live_detail,
                        liveId = contentId.toString()
                    )
                )
            }
            //卡片用户
            ContentType.CARD_USER -> {
                getProvider(ICardMonopolyProvider::class.java)?.startCardMainActivity(context)
            }
            //卡片套装
            ContentType.CARD_SUIT -> {
                getProvider(ICardMonopolyProvider::class.java)?.startCardMainActivity(context)
            }
            //视频
            ContentType.VIDEO -> {
                jumpToUgcDetail(contentId, 5L)
            }
            //音频
            ContentType.AUDIO -> {
                jumpToUgcDetail(contentId, 6L)
            }
            //片单
            ContentType.FILM_LIST -> {
                //本期还没有片单的需求
            }
        }
    }

    //跳转到用户主页
    fun jumpToUserHome(context: Context?, userId: Long?) {
        context?.let { c ->
            jumpPage(
                c, AppLinkExtra(
                    pageType = PAGE_TYPE_userProfile,
                    userId = userId.toString()
                )
            )
        }
    }

    //跳转到影片详情页
    fun jumpToMovieDetail(context: Context?, movieId: Long?) {
        context?.let { c ->
            jumpPage(
                c, AppLinkExtra(
                    pageType = PAGE_TYPE_movieDetail,
                    movieId = movieId.toString()
                )
            )
        }
    }

    //跳转到正在购票(影片排期页)
    fun jumpToBuyTicket(context: Context?, movieId: String?) {
        context?.let { c ->
            jumpPage(
                c, AppLinkExtra(
                    pageType = PAGE_TYPE_movieTime,
                    movieId = movieId
                )
            )
        }
    }

    //跳转至评论详情
    fun jumpToCommentDetail(type: Long, commentId: Long) {
        getProvider(ICommentProvider::class.java)?.startComment(
            objType = type,
            commentId = commentId,
        )
    }

    //跳转至1.日志 2.帖子 3.影评 4.文章 5.视频 6.音频
    private fun jumpToUgcDetail(contentId: Long, contentType: Long) {
        getProvider(IUgcProvider::class.java) {
            launchDetail(
                contentId = contentId,
                type = contentType
            )
        }
    }
}