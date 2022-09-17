package com.kotlin.tablet.adapter

import com.kotlin.android.app.data.entity.filmlist.MyCreate
import com.kotlin.android.image.coil.ext.loadImage
import com.kotlin.android.ktx.ext.core.setBackground
import com.kotlin.android.ktx.ext.dimension.dp
import com.kotlin.android.ktx.ext.dimension.dpF
import com.kotlin.android.widget.adapter.multitype.adapter.binder.MultiTypeBinder
import com.kotlin.tablet.R
import com.kotlin.tablet.databinding.ItemContributeMyCreateBinding

/**
 * 创建者: SunHao
 * 创建时间: 2022/3/27
 * 描述:我创建的片单-投稿
 **/
class ContributeMyCreateBinder(val bean: MyCreate) :
    MultiTypeBinder<ItemContributeMyCreateBinding>() {
    override fun layoutId() = R.layout.item_contribute_my_create

    override fun areContentsTheSame(other: MultiTypeBinder<*>) =
        other is ContributeMyCreateBinder && bean.filmListId == other.bean.filmListId

    override fun onBindViewHolder(binding: ItemContributeMyCreateBinding, position: Int) {
        binding.mCreateCoverIv.loadImage(
            data = bean.coverUrl,
            width = 138.dp,
            height = 74.dp,
            roundedRadius = 8.dpF
        )
        binding.mContributeCreateTv.setBackground(
            colorRes = R.color.color_20a0da,
            endColorRes = R.color.color_1bafe0,
            cornerRadius = 4.dpF
        )
    }
}