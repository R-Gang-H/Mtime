package com.kotlin.android.community.family.component.ui.manage.adapter

import android.content.Context
import android.widget.TextView
import androidx.core.content.ContextCompat
import com.kotlin.android.community.family.component.R
import com.kotlin.android.community.family.component.databinding.ItemFamilyCategoryBinding
import com.kotlin.android.app.data.entity.community.group.GroupCategory
import com.kotlin.android.ktx.ext.click.onClick
import com.kotlin.android.widget.adapter.multitype.adapter.binder.MultiTypeBinder

/**
 * @author vivian.wei
 * @date 2020/8/17
 * @desc 家族分类
 */
class FamilyCategoryItemBinder (context: Context,
                                val bean: GroupCategory,
                                private val itemClick:
                                (bean: GroupCategory, newSelect: Int) -> Unit)
    : MultiTypeBinder<ItemFamilyCategoryBinding>() {

    private var mContext = context

    override fun layoutId(): Int {
        return R.layout.item_family_category
    }

    override fun areContentsTheSame(other: MultiTypeBinder<*>): Boolean {
        return other is FamilyCategoryItemBinder && other.bean.primaryCategoryId == bean.primaryCategoryId
    }

    override fun onBindViewHolder(binding: ItemFamilyCategoryBinding, position: Int) {
        if(bean.isSelect) {
            setSelectIcon(binding.mItemFamilyCategoryPrimaryNameTv)
        } else {
            binding.mItemFamilyCategoryPrimaryNameTv.setCompoundDrawables(null, null, null, null)
        }

        // 点击事件
        binding.root.onClick {
            if (!bean.isSelect) {
                // 回调
                itemClick.invoke(bean, position)
            }
        }
    }

    /**
     * 设置选中状态图标
     * @param tv
     */
    private fun setSelectIcon(tv: TextView) {
        val drawable = ContextCompat.getDrawable(mContext, R.drawable.ic_family_permisson_check)
        drawable!!.setBounds(0, 0, drawable.minimumWidth, drawable.minimumHeight)
        tv.setCompoundDrawables(null, null, drawable, null)
    }

}