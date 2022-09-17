package com.kotlin.android.card.monopoly.widget.card.view

import android.content.Context
import android.util.AttributeSet
import android.view.ViewGroup
import androidx.core.view.children
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.kotlin.android.card.monopoly.widget.AverItemDecoration
import com.kotlin.android.card.monopoly.widget.card.adapter.SuitCardAdapter
import com.kotlin.android.app.data.entity.monopoly.Card
import com.kotlin.android.ktx.ext.dimension.dp

/**
 * 缺失套装卡片视图：许愿树
 *
 * Created on 2020/9/14.
 *
 * @author o.s
 */
class LackCardView : RecyclerView {

    private val edge = 5.dp
    private val spanCount = 4
    private val mAdapter: SuitCardAdapter by lazy {
        SuitCardAdapter()
    }

    var data: List<Card>? = null
        set(value) {
            field = value
            fillData()
        }

    private fun fillData() {
        mAdapter.setData(data)
    }

    constructor(context: Context) : super(context) {
        initView()
    }

    constructor(context: Context, attrs: AttributeSet) : super(context, attrs) {
        initView()
    }

    constructor(context: Context, attrs: AttributeSet, defStyleAttr: Int) : super(context, attrs, defStyleAttr) {
        initView()
    }

    override fun onAttachedToWindow() {
        super.onAttachedToWindow()
        fitsSystemWindows = (parent as ViewGroup).fitsSystemWindows
        children.forEach {
            it.fitsSystemWindows = fitsSystemWindows
        }
    }

    private fun initView() {
        layoutManager = GridLayoutManager(context, spanCount).apply {
        }
//        itemAnimator = DefaultItemAnimator()
        addItemDecoration(AverItemDecoration(spanCount, edge))
        isNestedScrollingEnabled = false
        post {
            adapter = mAdapter
        }
    }

}