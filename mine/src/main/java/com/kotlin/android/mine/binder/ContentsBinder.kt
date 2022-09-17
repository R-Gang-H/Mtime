package com.kotlin.android.mine.binder

import android.view.View
import com.kotlin.android.app.data.annotation.*
import com.kotlin.android.app.router.provider.publish.IPublishProvider
import com.kotlin.android.ktx.ext.core.setImageDrawable
import com.kotlin.android.ktx.ext.orZero
import com.kotlin.android.mine.R
import com.kotlin.android.mine.bean.ContentItemViewBean
import com.kotlin.android.mine.bean.ContentsViewBean
import com.kotlin.android.mine.databinding.ItemContentsBinding
import com.kotlin.android.mtime.ktx.getColor
import com.kotlin.android.mtime.ktx.getDrawable
import com.kotlin.android.mtime.ktx.getString
import com.kotlin.android.router.ext.getProvider
import com.kotlin.android.widget.adapter.multitype.adapter.binder.MultiTypeBinder

class ContentsBinder(var bean: ContentItemViewBean, val type: Long, private val isDrafts: Boolean) :
    MultiTypeBinder<ItemContentsBinding>() {
    override fun layoutId(): Int = R.layout.item_contents

    override fun areContentsTheSame(other: MultiTypeBinder<*>): Boolean {
        return other is ContentsBinder && other.bean != bean
    }

    override fun onBindViewHolder(binding: ItemContentsBinding, position: Int) {
        super.onBindViewHolder(binding, position)
        binding.apply {
            stateTv.text = bean.showCreatorContentStatus
            val praiseUpCount = if (bean.praiseUpCount == null) 0L else bean.praiseUpCount
            val commentCount = if (bean.commentCount == null) 0L else bean.commentCount
            likeCommentsCountTv.text = String.format(
                "%s %d  ·  %s %d", getString(R.string.mine_message_praise),
                praiseUpCount, getString(R.string.mine_comment_tip), commentCount
            )
            likeCommentsCountTv.visibility = View.VISIBLE
            groupName.text = getString(R.string.mine_content_group_name, "${bean.item.group?.name}")
            longNameTv.text = "${bean.item.fcMovie?.name}"
            moreRl.setImageDrawable(getDrawable(R.drawable.ic_ver_more))
            when (type) {
                CONTENT_TYPE_ARTICLE, CONTENT_TYPE_POST, CONTENT_TYPE_JOURNAL -> {
                    copyWritingPicCV.visibility = View.VISIBLE
                    longFilmReviewPicCV.visibility = View.GONE
                    audioPicCV.visibility = View.GONE
                    tipsTv.visibility = View.GONE
                    groupName.visibility =
                        if (type == CONTENT_TYPE_POST) View.VISIBLE else View.GONE
                    bottomShadow.visibility =
                        if (type == CONTENT_TYPE_POST) View.VISIBLE else View.GONE
                }
                CONTENT_TYPE_FILM_COMMENT -> {
                    copyWritingPicCV.visibility = View.GONE
                    longFilmReviewPicCV.visibility = View.VISIBLE
                    audioPicCV.visibility = View.GONE
                    tipsTv.visibility = View.GONE
                }
                CONTENT_TYPE_VIDEO -> {
                    copyWritingPicCV.visibility = View.VISIBLE
                    longFilmReviewPicCV.visibility = View.GONE
                    audioPicCV.visibility = View.GONE
                    tipsTv.visibility = View.VISIBLE
                    bottomShadow.visibility = View.VISIBLE
                }
                CONTENT_TYPE_AUDIO -> {
                    copyWritingPicCV.visibility = View.GONE
                    longFilmReviewPicCV.visibility = View.GONE
                    audioPicCV.visibility = View.VISIBLE
                    tipsTv.visibility = View.GONE
                }
            }
            if (isDrafts) {
                stateTv.visibility = View.GONE
                likeCommentsCountTv.visibility = View.GONE
                if (type != CONTENT_TYPE_AUDIO) {
                    copyWritingPicCV.setOnClickListener {
                        startEditActivity()
                    }
                    longFilmReviewPicCV.setOnClickListener {
                        startEditActivity()
                    }
                    audioPicCV.setOnClickListener {
                        startEditActivity()
                    }
                    nameTv.setOnClickListener {
                        startEditActivity()
                    }
                }
            }
            when (bean.creatorContentStatus) {
                ContentsViewBean.CREATOR_CONTENT_STATUS_WAIT_HANDLE, ContentsViewBean.CREATOR_CONTENT_STATUS_WAIT_REVIEW, ContentsViewBean.CREATOR_CONTENT_STATUS_RELEASED_WAIT_HANDLE, ContentsViewBean.CREATOR_CONTENT_STATUS_RELEASED_WAIT_REVIEW -> {
                    stateTv.setTextColor(getColor(R.color.color_feb12a))
                    if (bean.creatorContentStatus == ContentsViewBean.CREATOR_CONTENT_STATUS_RELEASED_WAIT_HANDLE || bean.creatorContentStatus == ContentsViewBean.CREATOR_CONTENT_STATUS_RELEASED_WAIT_REVIEW) {
                        stateTv.text =
                            getString(R.string.mine_creator_content_status_change_wait_and_review_tip)
                        likeCommentsCountTv.visibility = View.GONE
                    }
                }
                ContentsViewBean.CREATOR_CONTENT_STATUS_REVIEW_FAIL, ContentsViewBean.CREATOR_CONTENT_STATUS_RELEASED_REVIEW_FAIL -> {
                    stateTv.setTextColor(getColor(R.color.color_ff5a36))
                    if (bean.creatorContentStatus == ContentsViewBean.CREATOR_CONTENT_STATUS_RELEASED_REVIEW_FAIL) {
                        stateTv.text =
                            getString(R.string.mine_creator_content_status_change_wait_and_review_tip)
                        likeCommentsCountTv.visibility = View.GONE
                    }
                }
                else -> stateTv.setTextColor(getColor(R.color.color_cbd0d7))
            }
        }
    }

    private fun startEditActivity() {
        if (type == CONTENT_TYPE_VIDEO) {
            getProvider(IPublishProvider::class.java) {
                startVideoPublishActivity(bean.id.orZero())
            }
        } else {
            val nameCN = bean.item.fcMovie?.name
            val nameEN = bean.item.fcMovie?.nameEn
            val name = if (nameCN.isNullOrEmpty()) {
                nameEN
            } else {
                nameCN
            }
            getProvider(IPublishProvider::class.java)
                ?.startEditorActivity(
                    type = type,
                    contentId = bean.id,
                    recordId = bean.item.creatorAuthority?.btnEdit?.recId,
                    movieId = bean.item.fcMovie?.id,
                    movieName = name,
                    isLongComment = type == CONTENT_TYPE_FILM_COMMENT
                )
        }
    }
}