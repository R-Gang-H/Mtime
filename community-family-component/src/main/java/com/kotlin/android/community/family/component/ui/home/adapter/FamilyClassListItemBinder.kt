package com.kotlin.android.community.family.component.ui.home.adapter

import android.view.View
import com.kotlin.android.community.family.component.R
import com.kotlin.android.community.family.component.databinding.ItemCommunityFamilyClassBinding
import com.kotlin.android.community.family.component.databinding.ItemCommunityFamilyClassListBinding
import com.kotlin.android.community.family.component.ui.clazz.FamilyClassListActivity
import com.kotlin.android.community.family.component.ui.clazz.bean.FamilyClassItem
import com.kotlin.android.ktx.ext.click.onClick
import com.kotlin.android.widget.adapter.multitype.adapter.binder.MultiTypeBinder
import com.kotlin.android.widget.adapter.multitype.createMultiTypeAdapter

/**
 * @author ZhouSuQiang
 * @email zhousuqiang@126.com
 * @date 2020/7/27
 *
 * 社区频道-家族tab-家族分类
 */
class FamilyClassListItemBinder(val list: List<FamilyClassItemBinder>):
        MultiTypeBinder<ItemCommunityFamilyClassListBinding>() {
    override fun layoutId(): Int {
        return R.layout.item_community_family_class_list
    }

    override fun areContentsTheSame(other: MultiTypeBinder<*>): Boolean {
        return other is FamilyClassListItemBinder && list != other.list
    }

    override fun onBindViewHolder(binding: ItemCommunityFamilyClassListBinding, position: Int) {
        createMultiTypeAdapter(binding.mFamilyClassListRv)
                .notifyAdapterAdded(list)
    }

    override fun onClick(view: View) {
        when(view.id) {
            R.id.mFamilyClassListAllTv -> {
                //跳转到家族分类列表
                FamilyClassListActivity.start(view.context)
            }
            else -> {
                super.onClick(view)
            }
        }
    }
}

class FamilyClassItemBinder(val item: FamilyClassItem):
        MultiTypeBinder<ItemCommunityFamilyClassBinding>() {
    override fun layoutId(): Int {
        return R.layout.item_community_family_class
    }

    override fun areContentsTheSame(other: MultiTypeBinder<*>): Boolean {
        return other is FamilyClassItemBinder && item.id != other.item.id
    }

    override fun onBindViewHolder(binding: ItemCommunityFamilyClassBinding, position: Int) {
        binding.root.onClick {
            FamilyClassListActivity.start(it.context, position)
        }
    }
}