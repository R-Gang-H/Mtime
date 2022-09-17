package com.kotlin.android.card.monopoly.widget

import android.content.Context
import android.util.AttributeSet
import android.view.ViewGroup
import android.widget.LinearLayout
import androidx.core.view.children
import com.kotlin.android.app.data.entity.monopoly.Box
import com.kotlin.android.ktx.ext.dimension.dp

/**
 * 宝箱展示视图：铜宝箱、银宝箱、金宝箱、铂金宝箱、钻石宝箱、限量宝箱、今日活动宝箱
 *
 * Created on 2020/8/18.
 *
 * @author o.s
 */
class BoxView : LinearLayout {

    private val mLabelRatio = 42 / 166F
    private val mBoxRatio = 122 / 140F
    private val mLabelWidth = 80.dp
    private val mLabelHeight = (mLabelWidth * mLabelRatio).toInt()
    private val mBoxWidth = 68.dp
    private val mBoxHeight = (mBoxWidth * mBoxRatio).toInt()
    private val mItemWidth = mLabelWidth
    private val mItemHeight = mBoxHeight + mLabelHeight + 12.dp
    private var mItemGap = 0 // 宝箱间隔

    private val boxViewStore = ArrayList<BoxItemView>()

    val boxList: List<Box>
        get() {
            return ArrayList<Box>().apply {
                boxViewStore.forEach {
                    it.getCardBox()?.apply {
                        add(this)
                    }
                }
            }
        }

    var emptyState: ((isEmpty: Boolean) -> Unit)? = null

    var action: ((box: Box) -> Unit)? = null
        set(value) {
            field = value
            boxViewStore.forEach {
                it.action = value
            }
        }

    var data: List<Box>? = null
        set(value) {
            field = value
            fillData()
        }

    /**
     * 重置空状态
     */
    private fun resetEmpty() {
        boxViewStore.forEach {
            it.state = BoxItemView.State.EMPTY
        }
    }

    /**
     * 是否有空位
     */
    private var hasEmptyPosition: Boolean = false
        get() {
            boxViewStore.forEach {
                if (it.state == BoxItemView.State.EMPTY) {
                    return true
                }
            }
            return false
        }
        set(value) {
            field = value
            emptyState?.invoke(value)
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
        orientation = HORIZONTAL

        // 默认添加4个宝箱，可扩展
        addBoxView()
        addBoxView()
        addBoxView()
        addBoxView()
    }

    /**
     * 回收资源
     */
    fun cancel() {
        boxViewStore.forEach {
            it.cancel()
        }
    }

    /**
     * 更新宝箱UI
     */
    fun updateCardBox(cardBox: Box?) {
        cardBox?.apply {
            fillItemData(position - 1, this)
        }
        // 重新计算空位
        hasEmptyPosition = hasEmptyPosition
    }

    /**
     * 填充宝箱列表数据
     */
    private fun fillData() {
        resetEmpty()
        data?.forEach {
            fillItemData(it.position - 1, it)
        }
        // 重新计算空位
        hasEmptyPosition = hasEmptyPosition
    }

    /**
     * 填充指定位置的宝箱数据
     */
    private fun fillItemData(index: Int, box: Box?) {
        if (index in 0 until boxViewStore.size) {
            boxViewStore[index].setCardBox(box)
        }
    }

    /**
     * 添加项
     */
    private fun addBoxView() {
        val boxView = BoxItemView(context).apply {
            LayoutParams(mItemWidth, mItemHeight).apply {
                layoutParams = this
            }
            action = this@BoxView.action
            state = BoxItemView.State.EMPTY
        }
        addView(boxView)
        boxViewStore.add(boxView)
    }

    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec)
        val mWidth = measuredWidth
        mItemGap = (mWidth - mItemWidth * 4) / 3
        boxViewStore.forEachIndexed { index, view ->
            if (index > 0) {
                view.apply {
                    val params = layoutParams as MarginLayoutParams
                    params.marginStart = mItemGap
                    layoutParams = params
                }
            }
        }
    }

}