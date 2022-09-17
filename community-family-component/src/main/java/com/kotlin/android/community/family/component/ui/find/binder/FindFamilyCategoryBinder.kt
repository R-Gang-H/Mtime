package com.kotlin.android.community.family.component.ui.find.binder

import androidx.recyclerview.widget.LinearLayoutManager
import com.kotlin.android.app.data.entity.family.FindFamilyCategory
import com.kotlin.android.community.family.component.R
import com.kotlin.android.community.family.component.databinding.ItemCategoryFindFamilyBinding
import com.kotlin.android.community.family.component.ui.clazz.FamilyClassListActivity
import com.kotlin.android.ktx.ext.click.onClick
import com.kotlin.android.ktx.ext.core.setBackground
import com.kotlin.android.ktx.ext.dimension.dpF
import com.kotlin.android.widget.adapter.multitype.adapter.binder.MultiTypeBinder
import com.kotlin.android.widget.adapter.multitype.createMultiTypeAdapter


/**
 * @des 找家族顶部分类binder
 * @author zhangjian
 * @date 2022/3/17 15:30
 */
class FindFamilyCategoryBinder(val data: ArrayList<FindFamilyCategory>) :
    MultiTypeBinder<ItemCategoryFindFamilyBinding>() {
    override fun layoutId(): Int = R.layout.item_category_find_family

    override fun areContentsTheSame(other: MultiTypeBinder<*>): Boolean {
        return other is FindFamilyCategoryBinder
    }

    override fun onBindViewHolder(binding: ItemCategoryFindFamilyBinding, position: Int) {
        super.onBindViewHolder(binding, position)
        binding.llRight.setBackground(colorRes = R.color.color_f3f3f4,cornerRadius = 11.dpF)
        binding.llRight.onClick {
            //跳转家族列表
            FamilyClassListActivity.start(binding.root.context)
        }

        val mAdapter = createMultiTypeAdapter(binding.ctlRv,LinearLayoutManager(binding.root.context,LinearLayoutManager.HORIZONTAL,false))
        val list = ArrayList<FindFamilyCategoryItemBinder>()
        data.forEach {
            list.add(FindFamilyCategoryItemBinder(it))
        }
        mAdapter.notifyAdapterAdded(list)

    }
}