package com.kotlin.android.home.ui.tashuo.adapter

import androidx.recyclerview.widget.StaggeredGridLayoutManager
import com.kotlin.android.home.R
import com.kotlin.android.home.databinding.ItemTaShuoRcmdFollowItemBinding
import com.kotlin.android.home.databinding.ItemTaShuoRcmdFollowListBinding
import com.kotlin.android.home.ui.tashuo.bean.RcmdFollowUserBean
import com.kotlin.android.ktx.ext.click.onClick
import com.kotlin.android.ktx.ext.span.addStartImage
import com.kotlin.android.mtime.ktx.ext.showToast
import com.kotlin.android.widget.adapter.bindingadapter.BaseBindingAdapter
import com.kotlin.android.widget.adapter.multitype.adapter.binder.MultiTypeBinder
import com.kotlin.android.widget.adapter.multitype.createMultiTypeAdapter

/**
 * 首页-TA说-推荐关注列表 binder
 */
class RcmdFollowListBinder(val list: List<RcmdFollowUserBean>) :
    MultiTypeBinder<ItemTaShuoRcmdFollowListBinding>() {
    override fun layoutId(): Int {
        return R.layout.item_ta_shuo_rcmd_follow_list
    }

    override fun areContentsTheSame(other: MultiTypeBinder<*>): Boolean {
        return other is RcmdFollowListBinder
    }

    override fun onBindViewHolder(binding: ItemTaShuoRcmdFollowListBinding, position: Int) {
        super.onBindViewHolder(binding, position)
        binding.root.apply {
            if (layoutParams is StaggeredGridLayoutManager.LayoutParams) {
                (layoutParams as StaggeredGridLayoutManager.LayoutParams).isFullSpan = true
            }
        }
        createMultiTypeAdapter(binding.recyclerView).apply { 
            notifyAdapterDataSetChanged(list.map { 
                RcmdFollowItemBinder(it)
            })
        }.setOnClickListener(mOnClickListener)
    }
}