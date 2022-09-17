package com.kotlin.android.search.newcomponent.adapter.binder

import android.view.View
import com.kotlin.android.review.component.R
import com.kotlin.android.review.component.databinding.ItemReviewBinding
import com.kotlin.android.review.component.item.adapter.ReviewBinder
import com.kotlin.android.review.component.item.bean.ReviewItem

/**
 * @author ZhouSuQiang
 * @email zhousuqiang@126.com
 * @date 2021/5/18
 */
class SearchRcmdReviewBinder(val item: ReviewItem) : ReviewBinder(item) {
    override fun onBindViewHolder(binding: ItemReviewBinding, position: Int) {
//        super.onBindViewHolder(binding, position)
    }

    override fun onClick(view: View) {
        if (view.id == R.id.mReviewRoot) {
            mOnClickListener?.invoke(view, this)
        }
        super.onClick(view)
    }
}