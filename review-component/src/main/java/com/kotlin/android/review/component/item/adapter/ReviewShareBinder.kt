package com.kotlin.android.review.component.item.adapter

import android.view.View
import com.kotlin.android.review.component.R
import com.kotlin.android.review.component.databinding.ItemReviewShareBinding
import com.kotlin.android.review.component.item.bean.ReviewShareItemViewBean
import com.kotlin.android.review.component.item.bean.ReviewShareItemViewBean.Companion.REVIEW_SHARE_PLATFORM_SWITCH
import com.kotlin.android.widget.adapter.multitype.adapter.binder.MultiTypeBinder

/**
 * create by lushan on 2020/11/4
 * description:分享的binder
 */
class ReviewShareBinder(var bean: ReviewShareItemViewBean) : MultiTypeBinder<ItemReviewShareBinding>() {
    override fun layoutId(): Int {
        return R.layout.item_review_share
    }

    override fun areContentsTheSame(other: MultiTypeBinder<*>): Boolean {
        return other is ReviewShareBinder && other.bean != bean
    }

    override fun onClick(view: View) {
        when (view.id) {
            R.id.sharePlatTv -> {//如果是匿名，不需要交给上层处理
                if (bean.platformId == REVIEW_SHARE_PLATFORM_SWITCH) {
                    bean.isOpen = !bean.isOpen
                    notifyAdapterSelfChanged()
                }
                super.onClick(view)
            }
            else -> {
                super.onClick(view)
            }
        }

    }
}