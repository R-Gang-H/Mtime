package com.kotlin.android.ugc.detail.component.binder

import com.kotlin.android.ugc.detail.component.R
import com.kotlin.android.ugc.detail.component.databinding.ItemUgcLinkTitleBinding
import com.kotlin.android.widget.adapter.multitype.adapter.binder.MultiTypeBinder

/**
 * create by lushan on 2022/3/15
 * des:
 **/
class UgcLinkTitleBinder(var title:String = "") : MultiTypeBinder<ItemUgcLinkTitleBinding>() {
    override fun layoutId(): Int  = R.layout.item_ugc_link_title

    override fun areContentsTheSame(other: MultiTypeBinder<*>): Boolean {
        return other is UgcLinkTitleBinder && other.title == title
    }
}