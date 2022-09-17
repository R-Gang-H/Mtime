package com.kotlin.android.card.monopoly.widget.card.view

import android.content.Context
import android.util.AttributeSet
import android.view.ViewGroup
import androidx.core.view.children
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.kotlin.android.card.monopoly.widget.AverItemDecoration
import com.kotlin.android.card.monopoly.widget.card.adapter.SelectCardAdapter
import com.kotlin.android.app.data.entity.monopoly.Card
import com.kotlin.android.ktx.ext.dimension.dp

/**
 * 可选择卡片视图：口袋卡片、打开卡片宝箱等
 *
 * Created on 2020/9/14.
 *
 * @author o.s
 */
class SelectCardView : RecyclerView {

    private val edge = 5.dp
    private val spanCount = 5
    private val mAdapter: SelectCardAdapter by lazy {
        SelectCardAdapter {
            action?.invoke(it)
        }
    }

    /**
     * 选择模式
     */
    var selectModel: SelectModel = SelectModel.SINGLE
        set(value) {
            field = value
            mAdapter.selectModel = value
        }

    val selectedCards: List<Card>
        get() = mAdapter.selectedCards

    var action: ((card: Card?) -> Unit)? = null

    var data: List<Card>? = null
        set(value) {
            field = value
            fillData()
        }

    var position: Int = 0
        set(value) {
            field = value
            mAdapter.position = value
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

    /**
     * 选择模式
     */
    enum class SelectModel {
        /**
         * 单选
         */
        SINGLE,

        /**
         * 多选
         */
        MULTIPART,

        /**
         * 单选不可反选
         */
        SINGLE_NOT_CANCEL,
    }
}