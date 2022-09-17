package com.kotlin.tablet.adapter

import com.kotlin.android.widget.adapter.multitype.adapter.binder.MultiTypeBinder
import com.kotlin.tablet.R
import com.kotlin.tablet.databinding.ItemContributeBottomBinding

class ContributeBottomBinder(val text: String) : MultiTypeBinder<ItemContributeBottomBinding>() {

    override fun areContentsTheSame(other: MultiTypeBinder<*>): Boolean {
        return other is ContributeBottomBinder && text == other.text
    }

    override fun layoutId(): Int {
        return R.layout.item_contribute_bottom
    }
}
