package com.kotlin.android.ugc.detail.component.binder

import com.kotlin.android.ktx.ext.dimension.dp
import com.kotlin.android.mtime.ktx.ext.ShapeExt
import com.kotlin.android.ugc.detail.component.R
import com.kotlin.android.ugc.detail.component.databinding.ItemUgcDetailSpoilerBinding
import com.kotlin.android.widget.adapter.multitype.adapter.binder.MultiTypeBinder

/**
 * Created by lushan on 2020/8/5
 * 评论剧透
 */

open class UgcMovieSpoilerBinder (var spoiler:String):MultiTypeBinder<ItemUgcDetailSpoilerBinding>(){
    override fun layoutId(): Int  = R.layout.item_ugc_detail_spoiler

    override fun areContentsTheSame(other: MultiTypeBinder<*>): Boolean {
        return other is UgcMovieSpoilerBinder && other.spoiler !=spoiler
    }

    override fun onBindViewHolder(binding: ItemUgcDetailSpoilerBinding, position: Int) {
        super.onBindViewHolder(binding, position)
        ShapeExt.setShapeColorAndCorner(binding.spoilerTv,R.color.color_f2f3f6,12.dp)

    }

}