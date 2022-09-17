package com.kotlin.android.mine.ui.reward.adapter

import android.text.Html
import com.kotlin.android.app.data.entity.mine.CreatorRewardInfo
import com.kotlin.android.ktx.ext.core.gone
import com.kotlin.android.ktx.ext.core.setBackground
import com.kotlin.android.ktx.ext.core.visible
import com.kotlin.android.ktx.ext.dimension.dp
import com.kotlin.android.ktx.ext.dimension.dpF
import com.kotlin.android.mine.R
import com.kotlin.android.mine.databinding.ItemRewardLayoutBinding
import com.kotlin.android.widget.adapter.multitype.adapter.binder.MultiTypeBinder

/**
 * 权益说明
 */
class RewardItemBinder(val list: CreatorRewardInfo, val isShow: Boolean) :
    MultiTypeBinder<ItemRewardLayoutBinding>() {
    override fun layoutId(): Int {
        return R.layout.item_reward_layout
    }

    override fun areContentsTheSame(other: MultiTypeBinder<*>): Boolean {
        return other is RewardItemBinder
    }

    override fun onBindViewHolder(binding: ItemRewardLayoutBinding, position: Int) {
        binding.bean = list
        binding.isShow = isShow
        when (list.status) {
            1L -> {
                binding.tvStatus.setBackground(
                    strokeColorRes = R.color.color_cbd0d7,
                    cornerRadius = 8.dpF,
                    strokeWidth = 1.dp
                )
            }
            2L -> {
                binding.tvStatus.setBackground(
                    strokeColorRes = R.color.color_20a0da,
                    cornerRadius = 8.dpF,
                    strokeWidth = 1.dp
                )
            }
            else -> {
                binding.tvStatus.setBackground(
                    strokeColorRes = R.color.color_cbd0d7,
                    cornerRadius = 8.dpF,
                    strokeWidth = 1.dp
                )
            }
        }
        if (lastPosition == position) {
            binding.mViewTwo.gone()
        } else {
            binding.mViewTwo.visible()
        }
        binding.tvDetails.text = Html.fromHtml(list.details)
    }
}