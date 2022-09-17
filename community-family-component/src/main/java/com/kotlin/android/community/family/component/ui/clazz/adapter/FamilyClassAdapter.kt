package com.kotlin.android.community.family.component.ui.clazz.adapter

import android.graphics.Typeface
import com.kotlin.android.community.family.component.R
import com.kotlin.android.community.family.component.databinding.ItemFamilyClassBinding
import com.kotlin.android.community.family.component.ui.clazz.bean.FamilyClassItem
import com.kotlin.android.widget.adapter.bindingadapter.BaseBindingAdapter
import com.kotlin.android.widget.adapter.bindingadapter.BaseBindingHolder

/**
 * @author ZhouSuQiang
 * @email zhousuqiang@126.com
 * @date 2020/7/28
 */
class FamilyClassAdapter(private val itemClickBlock: (item: FamilyClassItem, position: Int) -> Unit):
        BaseBindingAdapter<FamilyClassItem, ItemFamilyClassBinding>() {
    var selectedPosition = 0

    override fun onBindViewHolder(holder: BaseBindingHolder<FamilyClassItem, ItemFamilyClassBinding>, position: Int) {
        items[position].isSelected = selectedPosition == position
        super.onBindViewHolder(holder, position)
    }

    override fun onBinding(binding: ItemFamilyClassBinding?, item: FamilyClassItem, position: Int) {
        //点击事件
        binding?.root?.setOnClickListener {
            if (!item.isSelected) {
                notifyItemChanged(selectedPosition)
                selectedPosition = position
                notifyItemChanged(selectedPosition)
                itemClickBlock.invoke(item, position)
            }
        }
        //字放大和加粗
        binding?.mFamilyClassNameTv?.run {
            textSize = if(item.isSelected) 17f else 15f
            typeface = if (item.isSelected) {
                Typeface.defaultFromStyle(Typeface.BOLD)
            } else {
                Typeface.defaultFromStyle(Typeface.NORMAL)
            }
        }
    }
}