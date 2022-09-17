package com.kotlin.android.card.monopoly.widget.card.view

import android.content.Context
import android.util.AttributeSet
import android.view.ViewGroup
import androidx.core.view.children
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.kotlin.android.card.monopoly.widget.AverItemDecoration
import com.kotlin.android.card.monopoly.widget.card.adapter.CheckCardAdapter
import com.kotlin.android.card.monopoly.widget.card.image.CardState
import com.kotlin.android.card.monopoly.widget.card.item.CheckCardItemView
import com.kotlin.android.app.data.entity.monopoly.Card
import com.kotlin.android.ktx.ext.dimension.dp
import com.kotlin.android.ktx.ext.log.i

/**
 * 可勾选的卡片视图：主页、我的口袋、TA的口袋、保险箱等
 *
 * Created on 2020/8/18.
 *
 * @author o.s
 */
class CheckCardView : RecyclerView {

    private val edge = 5.dp
    private val spanCount = 5
    private val mAdapter: CheckCardAdapter by lazy { CheckCardAdapter() }

    var action: ((event: CheckCardAdapter.ActionEvent) -> Unit)? = null
        set(value) {
            field = value
            mAdapter.action = value
        }

    var spec: Spec = Spec.COFFER
        set(value) {
            field = value
            post {
                mAdapter.spec = value
            }
        }

    var limit: Int = 0
        set(value) {
            field = value
            mAdapter.limit = value
//            post {
//            }
        }

    var data: List<Card>? = null
        set(value) {
            field = value
            mAdapter.setData(data)
            mAdapter.position = position
            "size=${value?.size}, pos=$position".i()
        }

    var position: Int = -1

    val selectedCards: ArrayList<Card>
        get() = mAdapter.selectedCards

    var selectChange: ((count: ArrayList<Card>) -> Unit)? = null
        set(value) {
            field = value
            mAdapter.selectChange = value
        }

    fun reset() {
        children.forEach {
            (it as? CheckCardItemView)?.apply {
                if (CardState.SELECTED == state) {
                    state = CardState.FILL
                }
            }
        }
        mAdapter.reset()
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

    enum class Spec(val spec: Int) {

        /**
         * 保险箱
         */
        COFFER(10),

        /**
         * 我的口袋
         */
        CARD(15),

        /**
         * 卡友丢给我的卡片（卡片中转站，弃卡位）
         */
        TRANSFER(5),

        /**
         * 卡片机器人
         */
        ROBOT(20)
    }

}