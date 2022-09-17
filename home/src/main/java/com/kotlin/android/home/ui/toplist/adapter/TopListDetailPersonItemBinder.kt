package com.kotlin.android.home.ui.toplist.adapter

import com.kotlin.android.app.data.entity.toplist.TopListItem
import com.kotlin.android.home.R
import com.kotlin.android.home.databinding.ItemToplistDetailPersonBinding

import com.kotlin.android.app.router.path.RouterProviderPath
import com.kotlin.android.app.router.provider.main.IMainProvider
import com.kotlin.android.router.ext.getProvider
import com.kotlin.android.widget.adapter.multitype.adapter.binder.MultiTypeBinder

/**
 * @author vivian.wei
 * @date 2020/8/21
 * @desc 电影榜单详情页_影片列表
 */
class TopListDetailPersonItemBinder(val bean: TopListItem) : MultiTypeBinder<ItemToplistDetailPersonBinding>() {

    val mainProvider = getProvider(IMainProvider::class.java)

    override fun layoutId(): Int {
        return R.layout.item_toplist_detail_person
    }

    override fun areContentsTheSame(other: MultiTypeBinder<*>): Boolean {
        return other is TopListDetailPersonItemBinder && other.bean.itemId == bean.itemId
    }

    override fun onBindViewHolder(binding: ItemToplistDetailPersonBinding, position: Int) {
        // 排名Icon
        binding.mItemToplistDetailPersonRankTv.let {
            var rankTv = it
            bean.rank?.let {
                when (it) {
                    1L -> {
                        rankTv.setBackgroundResource(R.drawable.ic_toplist_movie_first)
                    }
                    2L -> {
                        rankTv.setBackgroundResource(R.drawable.ic_toplist_movie_second)
                    }
                    3L -> {
                        rankTv.setBackgroundResource(R.drawable.ic_toplist_movie_third)
                    }
                    else -> {
                        rankTv.setBackgroundResource(R.drawable.ic_toplist_movie_rank_other)
                        if (it > 99) {
                            rankTv.textSize = 6f
                        } else if(it > 9) {
                            rankTv.textSize = 8f
                        } else {
                            rankTv.textSize = 10f
                        }
                    }
                }
            }
        }
    }
}