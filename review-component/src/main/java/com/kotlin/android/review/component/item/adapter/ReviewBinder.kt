package com.kotlin.android.review.component.item.adapter

import android.view.View
import com.kotlin.android.app.data.entity.CommContent
import com.kotlin.android.ktx.ext.core.setBackground
import com.kotlin.android.review.component.R
import com.kotlin.android.review.component.databinding.ItemReviewBinding
import com.kotlin.android.review.component.item.bean.ReviewItem
import com.kotlin.android.router.ext.getProvider
import com.kotlin.android.app.router.path.RouterProviderPath
import com.kotlin.android.app.router.provider.community_person.ICommunityPersonProvider
import com.kotlin.android.app.router.provider.ticket.ITicketProvider
import com.kotlin.android.app.router.provider.ugc.IUgcProvider
import com.kotlin.android.widget.adapter.multitype.adapter.binder.MultiTypeBinder

/**
 * @author ZhouSuQiang
 * @email zhousuqiang@126.com
 * @date 2020/7/13
 */
open class ReviewBinder(val reviewItem: ReviewItem) : MultiTypeBinder<ItemReviewBinding>() {
    private val mUgcProvider by lazy { getProvider(IUgcProvider::class.java) }
    private val mTicketProvider by lazy { getProvider(ITicketProvider::class.java) }

    override fun layoutId(): Int {
        return R.layout.item_review
    }

    override fun areContentsTheSame(other: MultiTypeBinder<*>): Boolean {
        return other is ReviewBinder && (other.reviewItem.likeCount != reviewItem.likeCount
                || other.reviewItem.dislikeCount != reviewItem.dislikeCount)
    }

    override fun onBindViewHolder(binding: ItemReviewBinding, position: Int) {
        setTopBgGradient(binding, position)
    }

    private fun setTopBgGradient(binding: ItemReviewBinding, position: Int) {
        binding.mReviewItemTopBgView.run {
            if (position == 0) {
                background = null
                visibility = View.GONE
            } else {
                setBackground(
                    colorRes = R.color.color_f2f3f6,
                    endColorRes = R.color.color_ffffff
                )
                visibility = View.VISIBLE
            }
        }
    }

    override fun onClick(view: View) {
        when (view.id) {
            R.id.mReviewRoot -> {
                mUgcProvider?.launchDetail(reviewItem.id, CommContent.TYPE_REVIEW)
            }
            R.id.mReviewMovieNameTv,
            R.id.mReviewMovieLayout -> {
                mTicketProvider?.startMovieDetailsActivity(reviewItem.movieId)
            }
            R.id.mReviewUserProfileIv,
            R.id.mReviewUserNameTv -> {
                getProvider(ICommunityPersonProvider::class.java)
                        ?.startPerson(userId = reviewItem.userId)
            }
            else -> {
                super.onClick(view)
            }
        }
    }

    /**
     * 点赞、取消点赞改变
     */
    fun praiseUpChanged() {
        if (reviewItem.isDislike) {
            reviewItem.isDislike = false
            reviewItem.dislikeCount--
        }
        reviewItem.isLike = !reviewItem.isLike
        if (reviewItem.isLike) {
            reviewItem.likeCount++
        } else {
            reviewItem.likeCount--
        }
        notifyAdapterSelfChanged()
    }

    /**
     * 点踩、取消点踩改变
     */
    fun praiseDownChanged() {
        if (reviewItem.isLike) {
            reviewItem.isLike = false
            reviewItem.likeCount--
        }
        reviewItem.isDislike = !reviewItem.isDislike
        if (reviewItem.isDislike) {
            reviewItem.dislikeCount++
        } else {
            reviewItem.dislikeCount--
        }
        notifyAdapterSelfChanged()
    }
}