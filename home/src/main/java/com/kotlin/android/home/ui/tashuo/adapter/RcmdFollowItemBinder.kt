package com.kotlin.android.home.ui.tashuo.adapter

import com.kotlin.android.app.router.provider.community_person.ICommunityPersonProvider
import com.kotlin.android.home.R
import com.kotlin.android.home.databinding.ItemTaShuoRcmdFollowItemBinding
import com.kotlin.android.home.ui.tashuo.bean.RcmdFollowUserBean
import com.kotlin.android.ktx.ext.click.onClick
import com.kotlin.android.ktx.ext.dimension.dp
import com.kotlin.android.mtime.ktx.ext.showToast
import com.kotlin.android.router.ext.getProvider
import com.kotlin.android.widget.adapter.multitype.adapter.binder.MultiTypeBinder

/**
 * 首页-TA说-推荐关注Item binder
 */
class RcmdFollowItemBinder(val item: RcmdFollowUserBean): MultiTypeBinder<ItemTaShuoRcmdFollowItemBinding>() {
    override fun layoutId(): Int {
        return R.layout.item_ta_shuo_rcmd_follow_item
    }

    override fun areContentsTheSame(other: MultiTypeBinder<*>): Boolean {
        return other is RcmdFollowItemBinder
    }

    override fun onBindViewHolder(binding: ItemTaShuoRcmdFollowItemBinding, position: Int) {
        super.onBindViewHolder(binding, position)
        binding.userFollowTv.compoundDrawablePadding = if (item.followed) 0 else 5.dp
        binding.root.onClick { 
            getProvider(ICommunityPersonProvider::class.java) {
                startPerson(userId = item.userId)
            }
        }
    }
    
    fun followChanged() {
        item.followed = item.followed.not()
        if (item.followed) {
            item.fansCount ++
        } else {
            item.fansCount --
        }
        notifyAdapterSelfChanged()
    }
}