package com.kotlin.android.ugc.detail.component.binder

import com.kotlin.android.ugc.detail.component.R
import com.kotlin.android.ugc.detail.component.databinding.ItemUgcCopyRightBinding
import com.kotlin.android.widget.adapter.multitype.adapter.binder.MultiTypeBinder

/**
 * create by lushan on 2020/11/18
 * description:
 */
class UgcCopyRightBinder(var copyRight:String = ""): MultiTypeBinder<ItemUgcCopyRightBinding>() {
    override fun layoutId(): Int {
        return R.layout.item_ugc_copy_right
    }

    override fun areContentsTheSame(other: MultiTypeBinder<*>): Boolean {
        return other is UgcCopyRightBinder && other.copyRight == copyRight
    }
}