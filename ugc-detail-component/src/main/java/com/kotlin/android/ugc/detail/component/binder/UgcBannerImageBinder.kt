package com.kotlin.android.ugc.detail.component.binder

import com.kotlin.android.ugc.detail.component.R
import com.kotlin.android.ugc.detail.component.bean.UgcImageViewBean
import com.kotlin.android.ugc.detail.component.databinding.ItemUgcDetailBannerImageBinding
import com.kotlin.android.widget.adapter.multitype.adapter.binder.MultiTypeBinder

/**
 * create by lushan on 2020/8/7
 * description: 图集样式ugc 上方banner
 */
open class UgcBannerImageBinder(var list: MutableList<UgcImageViewBean> = mutableListOf(),var title:String = "") : MultiTypeBinder<ItemUgcDetailBannerImageBinding>() {
    override fun layoutId(): Int = R.layout.item_ugc_detail_banner_image

    override fun areContentsTheSame(other: MultiTypeBinder<*>): Boolean {
        return other is UgcBannerImageBinder && other.list.hashCode() != list.hashCode()
    }
}