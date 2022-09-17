package com.kotlin.android.community.ui.person.binder

import android.graphics.drawable.GradientDrawable
import android.view.View
import com.kotlin.android.app.data.annotation.CONTENT_TYPE_FILM_COMMENT
import com.kotlin.android.app.router.provider.publish.IPublishProvider
import com.kotlin.android.app.router.provider.review.IReviewProvider
import com.kotlin.android.app.router.provider.ticket.ITicketProvider
import com.kotlin.android.app.router.provider.ugc.IUgcProvider
import com.kotlin.android.community.R
import com.kotlin.android.community.databinding.ItemCommunityWantseeBinding
import com.kotlin.android.community.ui.person.PERSON_TYPE_HAS_SEEN
import com.kotlin.android.community.ui.person.PERSON_TYPE_WANT_SEE
import com.kotlin.android.community.ui.person.bean.WantSeeViewBean
import com.kotlin.android.ktx.ext.core.gone
import com.kotlin.android.ktx.ext.core.setBackground
import com.kotlin.android.ktx.ext.core.visible
import com.kotlin.android.ktx.ext.dimension.dpF
import com.kotlin.android.mtime.ktx.getString
import com.kotlin.android.router.ext.getProvider
import com.kotlin.android.widget.adapter.multitype.adapter.binder.MultiTypeBinder

/**
 * create by wangwei on 2020/9/14
 * description: 想看看过binder
 */
class CommunityWantSeeBinder(val bean: WantSeeViewBean) :
    MultiTypeBinder<ItemCommunityWantseeBinding>() {
    override fun layoutId(): Int = R.layout.item_community_wantsee

    override fun areContentsTheSame(other: MultiTypeBinder<*>): Boolean {
        return other is CommunityWantSeeBinder && other.bean != bean
    }

    override fun onBindViewHolder(binding: ItemCommunityWantseeBinding, position: Int) {
        super.onBindViewHolder(binding, position)
        binding?.apply {
            when (bean.type) {
                PERSON_TYPE_WANT_SEE -> {//想看
                    if (bean.hasTicket) {
                        this.scoreTv.visible()
                        if (bean.playState == 1L) {//购票
                            this.scoreTv.setBackground(
                                colorRes = R.color.color_20a0da,
                                endColorRes = R.color.color_1bafe0,
                                orientation = GradientDrawable.Orientation.BL_TR,
                                cornerRadius = 13f.dpF
                            )
                            this.scoreTv.text = getString(R.string.comm_movie_btn_ticket)
                        } else if (bean.playState == 3L) {//预售
                            this.scoreTv.setBackground(
                                colorRes = R.color.color_afd956,
                                endColorRes = R.color.color_c0dc4d,
                                orientation = GradientDrawable.Orientation.BL_TR,
                                cornerRadius = 13f.dpF
                            )
                            this.scoreTv.text = getString(R.string.comm_movie_btn_presell)
                        }

                    } else {
                        this.scoreTv.gone()
                    }
                }
                PERSON_TYPE_HAS_SEEN -> {//看过
                    if (bean.hasScored()) {
                        this.scoreTv.gone()
                    } else {
                        this.scoreTv.visible()
                        this.scoreTv.setBackground(
                            colorRes = R.color.color_20a0da, endColorRes = R.color.color_1bafe0,
                            orientation = GradientDrawable.Orientation.BL_TR, cornerRadius = 13f.dpF
                        )
                        this.scoreTv.text = getString(R.string.score)
                    }

                }
            }
        }
    }

    override fun onClick(view: View) {
        if (view.id == R.id.scoreTv) {

            when (bean.type) {
                PERSON_TYPE_WANT_SEE -> {//想看
                    if (bean.hasTicket) {
                        if (bean.playState == 1L || bean.playState == 3L) {//TODO 购票、预售
                            getProvider(ITicketProvider::class.java)?.startMovieShowtimeActivity(
                                bean.movieId
                            )
                        }
                    }
                }
                PERSON_TYPE_HAS_SEEN -> {//看过
                    if (!bean.hasScored()) {//未评分跳转评分
                        getProvider(IPublishProvider::class.java) {
                            startEditorActivity(
                                type = CONTENT_TYPE_FILM_COMMENT,
                                movieId = bean.movieId,
                                movieName = bean.movieName,
                                isLongComment = false
                            )
                        }
                    }
                }
            }

        } else if (view.id == R.id.tv_film_comment) {//长影评
            getProvider(IUgcProvider::class.java) {
                launchDetail(bean.longInteractiveObj?.contentId?:0, CONTENT_TYPE_FILM_COMMENT)
            }
        } else if (view.id == R.id.tv_film_des) {//短影评
            getProvider(IUgcProvider::class.java) {
                launchDetail(bean.shortInteractiveObj?.contentId?:0, CONTENT_TYPE_FILM_COMMENT)
            }
        } else if (view.id == R.id.wantSeeRootView) {
            getProvider(ITicketProvider::class.java) {
                startMovieDetailsActivity(bean.movieId)
            }
        } else {
            super.onClick(view)
        }
    }

}