package com.kotlin.android.card.monopoly.widget.card.view

import android.content.Context
import android.util.AttributeSet
import android.view.ViewGroup
import androidx.core.view.children
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.kotlin.android.card.monopoly.widget.AverItemDecoration
import com.kotlin.android.card.monopoly.widget.card.adapter.PickCardAdapter
import com.kotlin.android.app.data.entity.monopoly.Card
import com.kotlin.android.ktx.ext.dimension.dp

/**
 * 可拾取卡片视图适配器：卡片机器人，开放口袋（卡片中转站）
 *
 * Created on 2020/9/10.
 *
 * @author o.s
 */
class PickCardView : RecyclerView {

    private val edgeTop = 15.dp
    private val edge = 5.dp
    private val spanCount = 5
    private val mAdapter by lazy {
        PickCardAdapter()
    }

//    val selectedCards: List<Card>
//        get() = mAdapter.selectedCards

    var action: ((card: Card?) -> Unit)? = null
        set(value) {
            field = value
            mAdapter.action = value
        }

    var spec: Spec = Spec.TRANSFER
        set(value) {
            field = value
            post {
                mAdapter.spec = value
            }
        }

    var data: List<Card>? = null
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
        addItemDecoration(AverItemDecoration(spanCount, edge, edgeTop))
        isNestedScrollingEnabled = false
        post {
            adapter = mAdapter
        }
    }

    enum class Spec(val spec: Int) {

        /**
         * 卡片中转站（弃卡位）
         */
        TRANSFER(5),

        /**
         * 卡片机器人
         */
        ROBOT(20)
    }

}