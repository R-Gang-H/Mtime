package com.kotlin.android.home.ui.toplist.adapter

import com.kotlin.android.app.data.entity.toplist.TopListInfo
import com.kotlin.android.home.R
import com.kotlin.android.home.databinding.ItemToplistDetailRelatedBinding
import com.kotlin.android.home.provider.HomeProvider

import com.kotlin.android.app.router.path.RouterProviderPath
import com.kotlin.android.router.ext.getProvider
import com.kotlin.android.widget.adapter.multitype.adapter.binder.MultiTypeBinder

/**
 * @author vivian.wei
 * @date 2020/8/21
 * @desc 电影/影人榜单详情页_相关榜单
 */
class TopListDetailRelateItemBinder(val bean: TopListInfo) : MultiTypeBinder<ItemToplistDetailRelatedBinding>() {

    val provider = getProvider(HomeProvider::class.java)

    override fun layoutId(): Int {
        return R.layout.item_toplist_detail_related
    }

    override fun areContentsTheSame(other: MultiTypeBinder<*>): Boolean {
        return other is TopListDetailRelateItemBinder && other.bean.id == bean.id
    }

    override fun onBindViewHolder(binding: ItemToplistDetailRelatedBinding, position: Int) {


    }
}