package com.kotlin.android.home.ui.findmovie.view

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.widget.FrameLayout
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.kotlin.android.home.R
import com.kotlin.android.home.ui.findmovie.bean.SearchBean
import com.kotlin.android.ktx.ext.orZero
import com.kotlin.android.widget.adapter.multitype.MultiTypeAdapter
import com.kotlin.android.widget.adapter.multitype.adapter.binder.MultiTypeBinder
import com.kotlin.android.widget.adapter.multitype.createMultiTypeAdapter


/**
 * 找电影条件筛选
 * @author: WangWei
 * @date: 2022/4/14
 */
class FilterConditionView(context: Context) : FrameLayout(context) {
    var list: List<MultiTypeBinder<*>>? = arrayListOf()
    var adapter: MultiTypeAdapter? = null
    var action: ((data: SearchBean) -> Unit)? = null//选中结果回调
    var searchBean: SearchBean = SearchBean()

    init {
        var view = LayoutInflater.from(context)
            .inflate(R.layout.item_filter_condition_contain_layout, null, false)
        var rv = view.findViewById<RecyclerView>(R.id.rv)
        adapter = createMultiTypeAdapter(
            rv,
            GridLayoutManager(context, 4)
        ).apply {
            setOnClickListener(::onBinderClick)
            notifyAdapterDataSetChanged(list)
        }

        addView(view)
    }

    fun setData(
        searchBean: SearchBean,
        list: List<MultiTypeBinder<*>>?,
        action: ((data: SearchBean) -> Unit)?
    ) {
        this.searchBean = searchBean
        this.list = list
        this.action = action
        adapter?.notifyAdapterDataSetChanged(list)
    }

    /**
     * 需要回调再实现
     * 关注、订阅之类
     */
    private fun onBinderClick(view: View, binder: MultiTypeBinder<*>) {
        if (binder is ItemFilmFilterConditionBinder) {
            if (binder != null) {
                when (searchBean.type) {
                    0 -> {//类型
                        searchBean.genreTypes = binder.bean.value.orZero()
                    }
                    1 -> {//地区
                        searchBean.area = binder.bean.value.orZero()
                    }
                    2 -> {//排序
                        searchBean.sortType = binder.bean.value.orZero()
                    }
                    3 -> {//年代
                        searchBean.year =
                            binder.bean.from.orZero().toString() + ":" + binder.bean.to.orZero()
                    }
                    else -> {
                    }
                }
            }
            //把选中的数据回调
            action?.invoke(searchBean)
        }

    }


}