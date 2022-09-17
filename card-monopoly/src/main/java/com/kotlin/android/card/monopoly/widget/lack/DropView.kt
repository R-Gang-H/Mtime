package com.kotlin.android.card.monopoly.widget.lack

import android.content.Context
import android.util.AttributeSet
import android.util.TypedValue
import android.view.Gravity
import android.view.ViewGroup
import android.widget.LinearLayout
import android.widget.TextView
import androidx.core.view.children
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.kotlin.android.card.monopoly.R
import com.kotlin.android.app.data.entity.monopoly.Suit
import com.kotlin.android.ktx.ext.core.getColor
import com.kotlin.android.ktx.ext.core.gone
import com.kotlin.android.ktx.ext.core.setBackground
import com.kotlin.android.ktx.ext.core.visible
import com.kotlin.android.ktx.ext.dimension.dp
import com.kotlin.android.ktx.ext.dimension.dpF
import com.kotlin.android.ktx.ext.statelist.getDrawableStateList

/**
 * 下拉视图：下拉展示缺失卡片套装检索信息列表
 * 展开，收起、选中可选项
 *
 * Created on 2020/9/15.
 *
 * @author o.s
 */
class DropView : LinearLayout {

    private var mDropHeight = 34.dp
    private var mLabelWidth = 16.dp
    private var mLabelHeight = 16.dp
    private var mRecyclerHeight = 200.dp

    private val mRecyclerMarginTop = 10.dp
    private val mDropPaddingLeft = 16.dp
    private val mDropPaddingRight = 11.dp

    private val mDropTextSize = 14F

    private val mData by lazy { ArrayList<Suit>() }

    private var dropView: TextView? = null
    private var recyclerView: RecyclerView? = null

    private val mAdapter by lazy {
        DropAdapter {
            dropView?.text = it.suitName
            state = State.COLLAPSING
            action?.invoke(it)
        }
    }

    var action: ((suit: Suit) -> Unit)? = null

    var stateChange: ((state: State) -> Unit)? = null

    var state: State = State.COLLAPSING
        set(value) {
            if (field != value) {
                stateChange?.invoke(value)
            }
            field = value
            notifyChange()
        }

    fun setData(data: List<Suit>?) {
        mData.clear()
        data?.let {
            mData.addAll(it)
        }
        mAdapter.setData(mData)
        mData.forEach {
            if (it.isSelected) {
                dropView?.text = it.suitName
            }
        }
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
        orientation = VERTICAL

        dropView = addDropView()
        recyclerView = addRecyclerView()

        state = State.COLLAPSING
    }

    private fun addDropView(): TextView {
        return TextView(context).apply {
            layoutParams = LayoutParams(LayoutParams.MATCH_PARENT, mDropHeight).apply {
            }
            setBackground(
                    strokeColorRes = R.color.color_cacfdd,
                    strokeWidth = 2.dp,
                    cornerRadius = 17.dpF
            )
            gravity = Gravity.CENTER_VERTICAL

            getDrawableStateList(
                    normalRes = R.drawable.ic_drop_down,
                    selectedRes = R.drawable.ic_drop_up
            ).apply {
                setBounds(0, 0, mLabelWidth, mLabelHeight)
                setCompoundDrawables(null, null, this, null)
            }

            setPadding(mDropPaddingLeft, 0, mDropPaddingRight, 0)
            setSingleLine()
            setTextSize(TypedValue.COMPLEX_UNIT_SP, mDropTextSize)
            setTextColor(getColor(R.color.color_4e5e73))
            setHintTextColor(getColor(R.color.color_8798af))
            setHint(R.string.hint_please_input_card_name)
            setOnClickListener {
                isSelected = !isSelected
                state = if (isSelected) {
                    State.EXPANDING
                } else {
                    State.COLLAPSING
                }
            }

            addView(this)
        }
    }

    private fun addRecyclerView(): RecyclerView {
        return RecyclerView(context).apply {
            layoutParams = LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT).apply {
                topMargin = mRecyclerMarginTop
            }
            layoutManager = LinearLayoutManager(context)
            adapter = mAdapter

            this@DropView.addView(this)
        }
    }

    private fun notifyChange() {
        when (state) {
            State.COLLAPSING -> {
                dropView?.isSelected = false
                recyclerView?.gone()
            }
            State.EXPANDING ->{
                dropView?.isSelected = true
                recyclerView?.visible()
            }
        }
    }

    /**
     * 展开，收起状态
     */
    enum class State {

        /**
         * 收起状态
         */
        COLLAPSING,

        /**
         * 展开状态
         */
        EXPANDING
    }
}