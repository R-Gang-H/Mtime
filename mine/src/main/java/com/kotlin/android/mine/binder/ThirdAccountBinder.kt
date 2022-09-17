package com.kotlin.android.mine.binder

import com.kotlin.android.ktx.ext.core.getColor
import com.kotlin.android.ktx.ext.core.setBackground
import com.kotlin.android.ktx.ext.dimension.dpF
import com.kotlin.android.mine.R
import com.kotlin.android.mine.bean.ThirdAccountDataBean
import com.kotlin.android.mine.databinding.ItemThirdAccountBinding
import com.kotlin.android.widget.adapter.multitype.adapter.binder.MultiTypeBinder

class ThirdAccountBinder(var bean: ThirdAccountDataBean) :
    MultiTypeBinder<ItemThirdAccountBinding>() {

    override fun layoutId(): Int = R.layout.item_third_account

    override fun areContentsTheSame(other: MultiTypeBinder<*>): Boolean {
        return other is ThirdAccountBinder && other.bean != bean
    }

    override fun onBindViewHolder(binding: ItemThirdAccountBinding, position: Int) {
        super.onBindViewHolder(binding, position)
        binding.bindStateTv.apply {
            setBackground(
                strokeColorRes = if (bean.bindStatus == 0) R.color.color_8798af else R.color.color_f2f3f6,
                cornerRadius = 15.dpF,
                strokeWidth = 1,
                colorRes = if (bean.bindStatus == 0) R.color.transparent else R.color.color_f2f3f6
            )
            setTextColor(
                if (bean.bindStatus == 0) getColor(R.color.color_8398B1) else getColor(
                    R.color.color_cbd0d7
                )
            )
        }
    }
}