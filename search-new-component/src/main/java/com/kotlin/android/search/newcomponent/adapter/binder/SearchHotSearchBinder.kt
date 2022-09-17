package com.kotlin.android.search.newcomponent.adapter.binder

import android.graphics.drawable.GradientDrawable
import android.view.View
import com.kotlin.android.ktx.ext.core.setBackground
import com.kotlin.android.ktx.ext.dimension.dpF
import com.kotlin.android.search.newcomponent.R
import com.kotlin.android.search.newcomponent.bean.HotSearchBean
import com.kotlin.android.search.newcomponent.databinding.ItemSearchHistoryHotSearchBinding
import com.kotlin.android.search.newcomponent.databinding.ItemSearchHistoryHotSearchListBinding
import com.kotlin.android.search.newcomponent.databinding.ItemSearchHistoryHotSearchListItemBinding
import com.kotlin.android.widget.adapter.multitype.adapter.binder.MultiTypeBinder
import com.kotlin.android.widget.adapter.multitype.createMultiTypeAdapter

/**
 * @author ZhouSuQiang
 * @email zhousuqiang@126.com
 * @date 2021/5/17
 */
class SearchHotSearchBinder(val datas: Map<Int, List<HotSearchBean>>) : MultiTypeBinder<ItemSearchHistoryHotSearchBinding>() {
    override fun layoutId(): Int {
        return R.layout.item_search_history_hot_search
    }

    override fun areContentsTheSame(other: MultiTypeBinder<*>): Boolean {
        return other is SearchHotSearchBinder && datas.hashCode() != other.datas.hashCode()
    }

    override fun onBindViewHolder(binding: ItemSearchHistoryHotSearchBinding, position: Int) {
        val list = arrayListOf<SearchHotSearchListBinder>()
        datas.forEach {
            list.add(SearchHotSearchListBinder(it.key, it.value))
        }
        createMultiTypeAdapter(binding.mSearchHotSearchRv)
                .setOnClickListener(::onSubBinderClick)
                .notifyAdapterDataSetChanged(list)
    }

    //向上Binder传递
    private fun onSubBinderClick(view: View, binder: MultiTypeBinder<*>) {
        when (binder) {
            is SearchHotSearchListItemBinder -> {
                mOnClickListener?.invoke(view, binder)
            }
        }
    }
}

class SearchHotSearchListBinder(val type: Int, val list: List<HotSearchBean>) : MultiTypeBinder<ItemSearchHistoryHotSearchListBinding>() {
    override fun layoutId(): Int {
        return R.layout.item_search_history_hot_search_list
    }

    override fun areContentsTheSame(other: MultiTypeBinder<*>): Boolean {
        return other is SearchHotSearchListBinder && list.hashCode() != other.list.hashCode()
    }

    override fun onBindViewHolder(binding: ItemSearchHistoryHotSearchListBinding, position: Int) {
        binding.apply {
            var icon = 0
            var text = 0
            when (type) {
                HotSearchBean.HOT_TYPE_FILM -> {
                    icon = R.drawable.ic_film
                    text = R.string.search_newcomponent_search_hot_film_tile
                }
                HotSearchBean.HOT_TYPE_PEOPLE -> {
                    icon = R.drawable.ic_people
                    text = R.string.search_newcomponent_search_hot_people_tile
                }
                else -> {
                    icon = R.drawable.ic_family
                    text = R.string.search_newcomponent_search_hot_family_tile
                }
            }
            mSearchHotSearchListTopBgView.setBackground(
                    colorRes = R.color.color_FFD233,
                    endColorRes = R.color.color_00ffffff,
                    cornerRadius = 5.dpF,
                    orientation = GradientDrawable.Orientation.TOP_BOTTOM
            )
            mSearchHotSearchItemIconIv.setImageResource(icon)
            mSearchHotSearchListTitleTv.setText(text)

            createMultiTypeAdapter(mSearchHotSearchListRankingRv)
                    .setOnClickListener(::onSubBinderClick)
                    .notifyAdapterDataSetChanged(list.map {
                        SearchHotSearchListItemBinder(it)
                    })
        }
    }

    //向上Binder传递
    private fun onSubBinderClick(view: View, binder: MultiTypeBinder<*>) {
        when (binder) {
            is SearchHotSearchListItemBinder -> {
                mOnClickListener?.invoke(view, binder)
            }
        }
    }
}

class SearchHotSearchListItemBinder(val item: HotSearchBean) : MultiTypeBinder<ItemSearchHistoryHotSearchListItemBinding>() {
    override fun layoutId(): Int {
        return R.layout.item_search_history_hot_search_list_item
    }

    override fun areContentsTheSame(other: MultiTypeBinder<*>): Boolean {
        return other is SearchHotSearchListItemBinder && item.hashCode() != other.item.hashCode()
    }

    override fun onBindViewHolder(binding: ItemSearchHistoryHotSearchListItemBinding, position: Int) {
        binding.apply {
            val icon = when (position) {
                0 -> {
                    R.drawable.ic_hot_search_ranking_1
                }
                1 -> {
                    R.drawable.ic_hot_search_ranking_2
                }
                2 -> {
                    R.drawable.ic_hot_search_ranking_3
                }
                else -> {
                    R.drawable.ic_hot_search_ranking_common
                }
            }
            val arrow = when(item.hotLevel) {
                HotSearchBean.HOT_LEVEL_UP -> {
                    R.drawable.ic_hot_search_ranking_up
                }
                HotSearchBean.HOT_LEVEL_DOWN -> {
                    R.drawable.ic_hot_search_ranking_down
                }
                HotSearchBean.HOT_LEVEL_FLAT -> {
                    R.drawable.ic_hot_search_ranking_line
                }
                else -> {
                    0
                }
            }
            mHotSearchListItemIconTv.setBackgroundResource(icon)
            mHotSearchListItemIconTv.text = (position + 1).toString()
            mHotSearchListItemArrowIv.setImageResource(arrow)
        }
    }
}