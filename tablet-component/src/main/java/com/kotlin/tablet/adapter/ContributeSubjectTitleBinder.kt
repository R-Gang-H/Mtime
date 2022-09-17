package com.kotlin.tablet.adapter

import com.kotlin.android.widget.adapter.multitype.adapter.binder.MultiTypeBinder
import com.kotlin.tablet.R
import com.kotlin.tablet.databinding.ItemContributeSubjectTitleBinding

/**
 * 创建者: SunHao
 * 创建时间: 2022/3/25
 * 描述:本期主题Title
 **/
class ContributeSubjectTitleBinder() : MultiTypeBinder<ItemContributeSubjectTitleBinding>() {
    override fun layoutId() = R.layout.item_contribute_subject_title

    override fun areContentsTheSame(other: MultiTypeBinder<*>): Boolean {
        return other is ContributeSubjectTitleBinder
    }
}