package com.kotlin.tablet.adapter

import com.kotlin.android.app.data.entity.filmlist.TalentStatistics
import com.kotlin.android.app.router.provider.community_person.ICommunityPersonProvider
import com.kotlin.android.ktx.ext.click.onClick
import com.kotlin.android.mtime.ktx.getDrawable
import com.kotlin.android.router.ext.getProvider
import com.kotlin.android.widget.adapter.multitype.adapter.binder.MultiTypeBinder
import com.kotlin.tablet.R
import com.kotlin.tablet.databinding.ItemContributeShortlistedBinding

/**
 * 创建者: SunHao
 * 创建时间: 2022/3/25
 * 描述:入围达人榜
 **/
class ContributeShortlistBinder(val data: TalentStatistics) :
    MultiTypeBinder<ItemContributeShortlistedBinding>() {
    override fun layoutId() = R.layout.item_contribute_shortlisted

    override fun areContentsTheSame(other: MultiTypeBinder<*>): Boolean {
        return other is ContributeShortlistBinder
    }

    override fun onBindViewHolder(binding: ItemContributeShortlistedBinding, position: Int) {
        super.onBindViewHolder(binding, position)
        updateSerialNumber(binding)
    }

    private fun updateSerialNumber(binding: ItemContributeShortlistedBinding) {
        data.serialNumber?.let {
            when (it) {
                1L -> binding.mNumTv.background = getDrawable(R.drawable.ic_shortlist_num1)
                2L -> binding.mNumTv.background = getDrawable(R.drawable.ic_shortlist_num2)
                3L -> binding.mNumTv.background = getDrawable(R.drawable.ic_shortlist_num3)
                else -> {
                }
            }
        }
    }
}