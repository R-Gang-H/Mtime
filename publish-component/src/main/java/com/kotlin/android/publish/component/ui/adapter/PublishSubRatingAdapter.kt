package com.kotlin.android.publish.component.ui.adapter

import com.kotlin.android.app.data.entity.common.MovieSubItemRating
import com.kotlin.android.ktx.ext.orZero
import com.kotlin.android.publish.component.R
import com.kotlin.android.publish.component.databinding.ItemPublishSubRatingBinding
import com.kotlin.android.widget.adapter.bindingadapter.BaseBindingAdapter

/**
 * @author ZhouSuQiang
 * @email zhousuqiang@126.com
 * @date 2021/5/24
 */
class PublishSubRatingAdapter : BaseBindingAdapter<MovieSubItemRating,
        ItemPublishSubRatingBinding>() {

    var ratingChange: ((hasRating: Boolean) -> Unit)? = null

    var action: ((level: Double) -> Unit)? = null

    override fun onBinding(binding: ItemPublishSubRatingBinding?,
                           item: MovieSubItemRating, position: Int) {
        binding?.apply {
            itemSubRatingTitleTv.text = item.title
            itemSubRatingNumTv.text = "${item.rating}分"
            itemSubRatingProgressView.level = item.rating?.toDouble().orZero()
            itemSubRatingProgressView.action = {
                item.rating = it.toFloat()
                itemSubRatingNumTv.text = "${item.rating}分"
                onChangedLevel()
            }
        }
    }

    private fun onChangedLevel() {
        var hasRating = true
        var totalLevel = 0.0f
        items.forEach {
            val subRating = it.rating ?: 0.0f
            totalLevel += subRating
            if (subRating == 0f) {
                hasRating = false
            }
        }
        val level = totalLevel.toDouble() / itemCount
        action?.invoke(level)
        ratingChange?.invoke(hasRating)
    }
}