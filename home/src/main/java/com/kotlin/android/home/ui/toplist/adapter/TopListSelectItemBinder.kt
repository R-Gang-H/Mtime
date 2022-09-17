package com.kotlin.android.home.ui.toplist.adapter

import android.content.Context
import androidx.recyclerview.widget.GridLayoutManager
import com.kotlin.android.app.data.entity.toplist.TopListInfo
import com.kotlin.android.app.data.entity.toplist.TopListItem
import com.kotlin.android.home.R
import com.kotlin.android.home.databinding.ItemToplistSelectBinding
import com.kotlin.android.home.ui.toplist.constant.TopListConstant

import com.kotlin.android.app.router.provider.home.IHomeProvider
import com.kotlin.android.router.ext.getProvider
import com.kotlin.android.widget.adapter.multitype.MultiTypeAdapter
import com.kotlin.android.widget.adapter.multitype.adapter.binder.MultiTypeBinder
import com.kotlin.android.widget.adapter.multitype.createMultiTypeAdapter

/**
 * @author vivian.wei
 * @date 2020/8/20
 * @desc 首页_榜单_精选片单
 */
class TopListSelectItemBinder (val context: Context, val bean: TopListInfo) :
        MultiTypeBinder<ItemToplistSelectBinding>() {

    companion object {
        const val ITEM_MAX_SIZE = 3
    }

    val homeProvider = getProvider(IHomeProvider::class.java)

    private lateinit var mAdapter: MultiTypeAdapter

    override fun layoutId(): Int {
        return R.layout.item_toplist_select
    }

    override fun areContentsTheSame(other: MultiTypeBinder<*>): Boolean {
        return other is TopListSelectItemBinder && other.bean.id == bean.id
    }

    override fun onBindViewHolder(binding: ItemToplistSelectBinding, position: Int) {
        mAdapter = createMultiTypeAdapter(binding.mItemToplistSelectRv,
                GridLayoutManager(context, 3))
        mAdapter.notifyAdapterAdded(getBinderList(bean.items))
    }

    /**
     * 影片列表
     */
    private fun getBinderList(list: List<TopListItem>?): List<MultiTypeBinder<*>> {
        val binderList = ArrayList<TopListSelectMovieItemBinder>()
        list.run {
            var count = 1
            this?.map {
                var isVisible: Boolean
                when (bean.type) {
                    TopListConstant.TOPLIST_TYPE_PERSON -> {
                        isVisible = it.personInfo != null && it.personInfo!!.personName != null
                                && it.personInfo!!.personName?.isNotEmpty()!!
                    }
                    else -> {
                        isVisible = it.movieInfo != null && it.movieInfo!!.movieName != null
                                && it.movieInfo!!.movieName?.isNotEmpty()!!
                    }
                }
                if (isVisible) {
                    var binder = TopListSelectMovieItemBinder(it)
                    binderList.add(binder)
                    // 最多显示3个
                    if (count == ITEM_MAX_SIZE) {
                        return@run
                    }
                    count++
                }
            }
        }
        return binderList
    }
}