package com.kotlin.android.card.monopoly.widget.card.view

import android.content.Context
import android.util.AttributeSet
import android.view.ViewGroup
import androidx.core.view.children
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.kotlin.android.card.monopoly.widget.AverItemDecoration
import com.kotlin.android.card.monopoly.widget.card.adapter.SuitsAdapter
import com.kotlin.android.app.data.entity.monopoly.Suit
import com.kotlin.android.ktx.ext.dimension.dp

/**
 * 我/TA的套装列表视图：
 *
 * Created on 2020/8/18.
 *
 * @author o.s
 */
class SuitsView : RecyclerView {

    private val edge = 5.dp
    private val edgeTop = 5.dp
    private val spanCount = 3

    /**
     * 最后一行底部空间
     */
    var edgeBottom = 0
        set(value) {
            field = value
            addItemDecoration(AverItemDecoration(spanCount, edge, edgeTop, value))
        }

    private val mAdapter: SuitsAdapter by lazy {
        SuitsAdapter {
            action?.invoke(it)
        }
    }

    var action: ((data: Suit?) -> Unit)? = null

    var data: List<Suit>? = null
        set(value) {
            field = value
            mAdapter.setData(value)
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
        addItemDecoration(AverItemDecoration(spanCount, edge, edgeTop, edgeBottom))
        isNestedScrollingEnabled = false
        post {
            adapter = mAdapter
        }
    }

}