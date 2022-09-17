package com.kotlin.android.mine.bean

import android.content.Context
import com.kotlin.android.app.data.ProguardRule
import com.kotlin.android.app.data.constant.CommConstant
import com.kotlin.android.app.data.entity.CommContent
import com.kotlin.android.app.data.entity.community.content.ContentList
import com.kotlin.android.ktx.ext.time.TimeExt
import com.kotlin.android.mine.R
import com.kotlin.android.mine.binder.ContentsBinder
import com.kotlin.android.widget.adapter.multitype.adapter.binder.MultiTypeBinder
import com.kotlin.android.app.data.annotation.CONTENT_TYPE_JOURNAL
import com.kotlin.android.app.data.annotation.CONTENT_TYPE_POST
import com.kotlin.android.app.data.annotation.CONTENT_TYPE_FILM_COMMENT
import com.kotlin.android.app.data.annotation.CONTENT_TYPE_ARTICLE
import com.kotlin.android.app.data.annotation.CONTENT_TYPE_VIDEO
import com.kotlin.android.app.data.annotation.CONTENT_TYPE_AUDIO

data class ContentsViewBean(
    val nextStamp: String? = "",
    val pageSize: Long = 0L,
    val hasNext: Boolean,
    var list: MutableList<MultiTypeBinder<*>> = mutableListOf()
) : ProguardRule {
    companion object {

        const val CREATOR_CONTENT_STATUS_DRAFT = 0L //草稿
        const val CREATOR_CONTENT_STATUS_WAIT_HANDLE = 11L //待处理
        const val CREATOR_CONTENT_STATUS_WAIT_REVIEW = 12L //待审核
        const val CREATOR_CONTENT_STATUS_REVIEW_FAIL = 13L //审核失败
        const val CREATOR_CONTENT_STATUS_RELEASED = 20L //已发布
        const val CREATOR_CONTENT_STATUS_RELEASED_WAIT_HANDLE = 21L //已发布编辑待处理
        const val CREATOR_CONTENT_STATUS_RELEASED_WAIT_REVIEW = 22L //已发布编辑待审核
        const val CREATOR_CONTENT_STATUS_RELEASED_REVIEW_FAIL = 23L //已发布编辑审核失败

        fun obtain(
            context: Context,
            contentList: ContentList,
            type: Long,
            isDrafts: Boolean
        ): ContentsViewBean {
            return ContentsViewBean(
                contentList.nextStamp,
                contentList.pageSize,
                contentList.hasNext,
                contentList.items?.map { converterContentBinder(context, it, type, isDrafts) }
                    ?.toMutableList()
                    ?: mutableListOf())
        }

        private fun converterContentBinder(
            context: Context,
            bean: ContentList.Item,
            type: Long,
            isDrafts: Boolean
        ): MultiTypeBinder<*> {
            var showCreatorContentStatus = when (bean.creatorAuthority?.creatorContentStatus) {
                CREATOR_CONTENT_STATUS_WAIT_HANDLE -> context.getString(
                    R.string.mine_creator_content_status_transcoding
                )
                CREATOR_CONTENT_STATUS_WAIT_REVIEW -> context.getString(
                    R.string.mine_creator_content_status_wait_review
                )
                CREATOR_CONTENT_STATUS_REVIEW_FAIL -> context.getString(
                    R.string.mine_creator_content_status_review_fail
                )
                CREATOR_CONTENT_STATUS_RELEASED -> context.getString(R.string.mine_creator_content_status_released)
                CREATOR_CONTENT_STATUS_RELEASED_WAIT_HANDLE, CREATOR_CONTENT_STATUS_RELEASED_WAIT_REVIEW -> context.getString(
                    R.string.mine_creator_content_status_change_wait_review
                )
                CREATOR_CONTENT_STATUS_RELEASED_REVIEW_FAIL -> context.getString(R.string.mine_creator_content_status_change_wait_review_fail)
                else -> ""
            }

            val imageUrl = when (type) {
                CONTENT_TYPE_ARTICLE,
                CONTENT_TYPE_POST,
                CONTENT_TYPE_JOURNAL -> getImageUrl(bean.mixImages)
                CONTENT_TYPE_FILM_COMMENT -> listOf(bean.fcMovie?.imgUrl)
                CONTENT_TYPE_VIDEO -> {
                    if (bean.mixVideos.isNullOrEmpty().not()) listOf(
                        bean.mixVideos[0].posterUrl ?: ""
                    ) else bean.mixVideos.run {
                        map {
                            it.posterUrl ?: ""
                        }
                    }
                }
                CONTENT_TYPE_AUDIO -> getImageUrl(bean.mixImages)
                else -> listOf(bean.createUser?.avatarUrl)
            }
            return ContentsBinder(
                ContentItemViewBean(
                    id = bean.contentId,
                    name = bean.title,
                    picUrl = if (!imageUrl.isNullOrEmpty()) imageUrl?.get(0) else "",
                    userCreateTime = bean.userCreateTime?.show,
                    videoDuration = TimeExt.getVideoFormat(bean.video?.videoSec ?: 0L),
                    commentCount = bean.interactive?.commentCount,
                    praiseUpCount = bean.interactive?.praiseUpCount,
                    creatorContentStatus = bean.creatorAuthority?.creatorContentStatus,
                    showCreatorContentStatus = showCreatorContentStatus,
                    item = bean
                ), type, isDrafts
            )
        }

        private fun getImageUrl(images: List<CommContent.Image>): List<String>? {
            return if (images.isNullOrEmpty().not()) listOf(
                images[0].imageUrl ?: ""
            ) else converterImgs(images)
        }

        private fun converterImgs(mixImages: List<CommContent.Image>?): List<String>? {
            mixImages?.run {
                return map {
                    it.imageUrl ?: ""
                }
            }
            return null
        }
    }
}