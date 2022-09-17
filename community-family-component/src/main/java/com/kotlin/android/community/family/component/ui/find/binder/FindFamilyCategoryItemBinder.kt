package com.kotlin.android.community.family.component.ui.find.binder

import com.kotlin.android.app.data.entity.family.FindFamilyCategory
import com.kotlin.android.community.family.component.R
import com.kotlin.android.community.family.component.databinding.ItemCategoryItemFindFamilyBinding
import com.kotlin.android.community.family.component.ui.clazz.FamilyClassListActivity
import com.kotlin.android.image.coil.ext.loadImage
import com.kotlin.android.ktx.ext.click.onClick
import com.kotlin.android.ktx.ext.dimension.dp
import com.kotlin.android.widget.adapter.multitype.adapter.binder.MultiTypeBinder


/**
 * @des 找家族顶部分类binder
 * @author zhangjian
 * @date 2022/3/17 15:30
 */
class FindFamilyCategoryItemBinder(val data: FindFamilyCategory) :
    MultiTypeBinder<ItemCategoryItemFindFamilyBinding>() {
    override fun layoutId(): Int = R.layout.item_category_item_find_family

    override fun areContentsTheSame(other: MultiTypeBinder<*>): Boolean {
        return other is FindFamilyCategoryItemBinder
    }

    override fun onBindViewHolder(binding: ItemCategoryItemFindFamilyBinding, position: Int) {
        super.onBindViewHolder(binding, position)
        //暂时没有默认图
        binding.ivIcon.loadImage(data = data.logo, width = 50.dp, height = 50.dp)
        //名称
        binding.tvName.text = data.name
        //点击跳转
        binding.llContainer.onClick {
            FamilyClassListActivity.start(binding.root.context,position)
        }
    }
}