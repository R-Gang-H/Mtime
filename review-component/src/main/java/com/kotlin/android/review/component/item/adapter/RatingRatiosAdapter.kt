package com.kotlin.android.review.component.item.adapter

import com.kotlin.android.review.component.databinding.ItemRatingDetailRatingRatiosItemBinding
import com.kotlin.android.review.component.item.bean.RatingCountRatioBean
import com.kotlin.android.widget.adapter.bindingadapter.BaseBindingAdapter

/**
 * @author ZhouSuQiang
 * @email zhousuqiang@126.com
 * @date 2021/5/24
 */
class RatingRatiosAdapter : BaseBindingAdapter<RatingCountRatioBean,
        ItemRatingDetailRatingRatiosItemBinding>() {
    override fun onBinding(binding: ItemRatingDetailRatingRatiosItemBinding?,
                           item: RatingCountRatioBean, position: Int) {
        binding?.apply {
            itemRatingRationsTitleTv.text = item.title
            itemRatingRationsProgressBar.progress = item.ratio.toInt()
            itemRatingRationsValueTv.text = "${item.ratio}%"
        }
    }
}