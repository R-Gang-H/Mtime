package com.kotlin.android.review.component.item.adapter

import com.kotlin.android.review.component.databinding.ItemRatingDetailSubRatingBinding
import com.kotlin.android.review.component.item.bean.MovieSubItemRatingBean
import com.kotlin.android.widget.adapter.bindingadapter.BaseBindingAdapter

/**
 * @author ZhouSuQiang
 * @email zhousuqiang@126.com
 * @date 2021/5/24
 */
class RatingSubRatingAdapter : BaseBindingAdapter<MovieSubItemRatingBean,
        ItemRatingDetailSubRatingBinding>() {
    override fun onBinding(binding: ItemRatingDetailSubRatingBinding?,
                           item: MovieSubItemRatingBean, position: Int) {
        binding?.apply {
            itemRatingDetailSubRatingTitleTv.text = item.title
            itemRatingDetailSubRatingNumTv.text = "${item.rating}分"
            itemRatingDetailSubRatingProgressView.level = (item.rating + 0.5f).toDouble()
            itemRatingDetailSubRatingProgressView.supportTouchEvent = false
        }
    }
}