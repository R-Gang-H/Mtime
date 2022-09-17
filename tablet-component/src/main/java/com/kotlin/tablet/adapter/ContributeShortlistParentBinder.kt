package com.kotlin.tablet.adapter

import androidx.recyclerview.widget.LinearLayoutManager
import com.kotlin.android.app.data.entity.filmlist.TalentStatistics
import com.kotlin.android.ktx.ext.dimension.dpF
import com.kotlin.android.widget.adapter.multitype.adapter.binder.MultiTypeBinder
import com.kotlin.android.widget.adapter.multitype.createMultiTypeAdapter
import com.kotlin.tablet.R
import com.kotlin.tablet.databinding.ItemContributeShortlistedParentBinding
import com.kotlin.tablet.view.VerticalItemDecoration

/**
 * 创建者: SunHao
 * 创建时间: 2022/3/25
 * 描述:入围达人榜
 **/
class ContributeShortlistParentBinder(val data: List<TalentStatistics>?) :
    MultiTypeBinder<ItemContributeShortlistedParentBinding>() {
    override fun layoutId() = R.layout.item_contribute_shortlisted_parent

    override fun areContentsTheSame(other: MultiTypeBinder<*>): Boolean {
        return other is ContributeShortlistParentBinder
    }

    override fun onBindViewHolder(binding: ItemContributeShortlistedParentBinding, position: Int) {
        createMultiTypeAdapter(
            binding.mShortListRv,
            LinearLayoutManager(binding.root.context)
        ).apply {
            notifyAdapterDataSetChanged(mutableListOf<MultiTypeBinder<*>>().apply {
                data?.filterIndexed { index, _ -> index < 3 }?.forEach {
                    add(ContributeShortlistBinder(it))
                }
            })
        }
        binding.mShortListRv.addItemDecoration(
            VerticalItemDecoration(
                edge = 1.dpF,
                edgeColorRes = R.color.color_1f9dafb4
            )
        )
    }
}