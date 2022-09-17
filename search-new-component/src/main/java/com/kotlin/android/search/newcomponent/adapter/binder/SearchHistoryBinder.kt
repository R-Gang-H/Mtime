package com.kotlin.android.search.newcomponent.adapter.binder

import android.text.TextUtils
import com.google.android.flexbox.*
import com.kotlin.android.search.newcomponent.R
import com.kotlin.android.search.newcomponent.databinding.ItemSearchHistoryBinderBinding
import com.kotlin.android.search.newcomponent.databinding.ItemSearchHistoryItemBinding
import com.kotlin.android.widget.adapter.multitype.adapter.binder.MultiTypeBinder
import com.kotlin.android.widget.adapter.multitype.createMultiTypeAdapter

/**
 * @author ZhouSuQiang
 * @email zhousuqiang@126.com
 * @date 2021/5/14
 */
class SearchHistoryBinder(val datas: List<String>) : MultiTypeBinder<ItemSearchHistoryBinderBinding>() {

//    private var mItemDecoration: FlexboxItemDecoration? = null

    override fun layoutId(): Int {
        return R.layout.item_search_history_binder
    }

    override fun areContentsTheSame(other: MultiTypeBinder<*>): Boolean {
        return other is SearchHistoryBinder && datas.hashCode() != other.datas.hashCode()
    }

    override fun onBindViewHolder(binding: ItemSearchHistoryBinderBinding, position: Int) {
        val flexboxLayoutManager = FlexboxLayoutManager(binding.root.context);
        //flexDirection 属性决定主轴的方向（即项目的排列方向）。类似 LinearLayout 的 vertical 和 horizontal。
        flexboxLayoutManager.setFlexDirection(FlexDirection.ROW);//主轴为水平方向，起点在左端。
        //flexWrap 默认情况下 Flex 跟 LinearLayout 一样，都是不带换行排列的，但是flexWrap属性可以支持换行排列。
        flexboxLayoutManager.setFlexWrap(FlexWrap.WRAP);//按正常方向换行
        //justifyContent 属性定义了项目在主轴上的对齐方式。
        flexboxLayoutManager.setJustifyContent(JustifyContent.FLEX_START);//交叉轴的起点对齐。

//        if (null == mItemDecoration) {
//            mItemDecoration = FlexboxItemDecoration(binding.root.context)
//            val itemDrawable = GradientDrawable().apply { setSize(10.dp, 10.dp) }
//            mItemDecoration?.setDrawable(itemDrawable)
//            mItemDecoration?.setOrientation(FlexboxItemDecoration.BOTH)
//        }

        binding.mSearchHistoryListRv.run {
//            removeItemDecoration(mItemDecoration!!)
//            addItemDecoration(mItemDecoration!!)
            createMultiTypeAdapter(this, flexboxLayoutManager)
                    .setOnClickListener { view, binder ->
                        this@SearchHistoryBinder.mOnClickListener?.invoke(view, binder)
                    }
                    .notifyAdapterDataSetChanged(datas.map {
                        SearchHistoryItemBinder(it)
                    })
        }
    }

}

class SearchHistoryItemBinder(val item: String) : MultiTypeBinder<ItemSearchHistoryItemBinding>() {
    override fun layoutId(): Int {
        return R.layout.item_search_history_item
    }

    override fun areContentsTheSame(other: MultiTypeBinder<*>): Boolean {
        return other is SearchHistoryItemBinder && !TextUtils.equals(item, other.item)
    }

    override fun onBindViewHolder(binding: ItemSearchHistoryItemBinding, position: Int) {
        super.onBindViewHolder(binding, position)
    }
}