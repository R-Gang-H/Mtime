package com.kotlin.android.ugc.detail.component.binder

import com.kotlin.android.ugc.detail.component.R
import com.kotlin.android.ugc.detail.component.bean.UgcTitleViewBean
import com.kotlin.android.ugc.detail.component.bean.UgcTitleViewBean.Companion.getTitle
import com.kotlin.android.ugc.detail.component.databinding.ItemUgcDetailTitleBinding
import com.kotlin.android.widget.adapter.multitype.adapter.binder.MultiTypeBinder

/**
 * Created by lushan on 2020/8/5
 * UGC详情文章、日志标题 一句话表头
 */
open class UgcTitleBinder(var titleBean: UgcTitleViewBean) : MultiTypeBinder<ItemUgcDetailTitleBinding>() {
    override fun layoutId(): Int = R.layout.item_ugc_detail_title

    override fun areContentsTheSame(other: MultiTypeBinder<*>): Boolean {
        return other is UgcTitleBinder && other.titleBean.title != titleBean.title
    }

    override fun onBindViewHolder(binding: ItemUgcDetailTitleBinding, position: Int) {
        super.onBindViewHolder(binding, position)
        binding.titleTv.text = getTitle(titleBean,binding.root.context)
    }

}