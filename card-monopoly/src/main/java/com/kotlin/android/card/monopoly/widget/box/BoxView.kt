package com.kotlin.android.card.monopoly.widget.box

import android.content.Context
import android.util.AttributeSet
import android.view.ViewGroup
import android.widget.LinearLayout
import androidx.annotation.IntRange
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

    private val mItemWidth = 75.dp
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
//    var digBoxState: ((Boolean) -> Unit)? = null

    var action: ((event: BoxItemView.ActionEvent) -> Unit)? = null
        set(value) {
            field = value
            boxViewStore.forEach {
                it.action = value
            }
        }

    var data: List<Box>? = null
        set(value) {
            field = value
            fillData2()
        }

    /**
     * 播放扫描动画（点击立即打开时）
     */
    fun playScanAnim(position: Int, hide: Boolean = false) {
        boxViewStore.find { it.position == position }?.apply {
            showScanBoxAnim(!hide)
        }
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
        addBoxView(1)
        addBoxView(2)
        addBoxView(3)
        addBoxView(4)
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
    fun updateCardBox2(cardBox: Box?) {
        cardBox?.apply {
            fillItemData(position - 1, this)
        }
        // 重新计算空位
        hasEmptyPosition = hasEmptyPosition
    }

    /**
     * 删除指定位置的宝箱,1,2,3,4号
     */
    fun remove(@IntRange(from = 1, to = 4) position: Int) {
        if (position in 1 .. 4) {
            boxViewStore[position - 1].state = BoxItemView.State.EMPTY
            hasEmptyPosition = true
        }
    }

    /**
     * 重置挖宝箱状态
     */
    private fun resetDigBoxState() {
        boxViewStore.forEach {
            if (it.state == BoxItemView.State.EMPTY) {
                it.state = BoxItemView.State.DIG_BOX
                hasEmptyPosition = true
                return
            }
        }
    }

    /**
     * 更新宝箱UI
     */
    fun updateCardBox(cardBox: Box?) {
        cardBox?.apply {
            if (position in 1 .. 4) {
                boxViewStore[position - 1].setCardBox(this)
            }
        }
        resetDigBoxState()
    }

    /**
     * 填充宝箱列表数据
     */
    private fun fillData2() {
        data?.apply {
            boxViewStore.forEachIndexed { index, boxItemView ->
                val box = firstOrNull { it.position - 1 == index }
                if (box != null) {
                    boxItemView.setCardBox(box)
                } else {
                    boxItemView.state = BoxItemView.State.EMPTY
                }
            }
        }
        resetDigBoxState()
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
    private fun addBoxView(position: Int) {
        val boxView = BoxItemView(context).apply {
//            LayoutParams(mItemWidth, mItemHeight).apply {
//                layoutParams = this
//            }
            this.position = position
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