package com.kotlin.android.comment.component.bar

import android.content.Context
import android.util.AttributeSet
import android.view.Gravity
import android.widget.FrameLayout
import androidx.core.view.children
import com.kotlin.android.comment.component.bar.item.BarButtonItem
import com.kotlin.android.ktx.ext.dimension.dp

/**
 * 栏按钮：
 *
 * Created on 2020/7/31.
 *
 * @author o.s
 */
class BarButton : FrameLayout {
    private val mPadding = 10.dp
    private val mItemWith = 50.dp
    private val mItemHeight = mItemWith
    private val startItems = ArrayList<BarButtonItem.Type>()
    private val endItems = ArrayList<BarButtonItem.Type>()

    var action: ((type: BarButtonItem.Type, isSelected: Boolean) -> Unit)? = null
        set(value) {
            field = value
            children.forEach {
                (it as? BarButtonItem)?.action = action
            }
        }

    constructor(context: Context) : super(context) {
        initView()
    }

    constructor(context: Context, attrs: AttributeSet) : super(context, attrs) {
        initView()
    }

    constructor(context: Context, attrs: AttributeSet, defStyleAttr: Int) : super(
        context,
        attrs,
        defStyleAttr
    ) {
        initView()
    }

    private fun initView() {
        setPadding(mPadding, 0, mPadding, 0)
    }

    /**
     * 根据类型 [type] 顺序依次添加工具条目（水平布局）
     * [isReverse]: 指示反向添加，
     * addItem(A).addItem(B).addItem(C)
     * .addItem(F, true).addItem(G, true)
     * 如图：
     * --A-B-C ... G-F--
     */
    fun addItem(type: BarButtonItem.Type, isReverse: Boolean = false): BarButton {
        if (isReverse) {
            if (!endItems.contains(type)) {
                endItems.add(type)
                fillEndItem(endItems.size - 1, type)
            }
        } else {
            if (!startItems.contains(type)) {
                startItems.add(type)
                fillStartItem(startItems.size - 1, type)
            }
        }
        return this
    }

    /**
     * 删除指定类型的栏按钮项
     */
    fun removeItem(type: BarButtonItem.Type) {
        if (startItems.remove(type) || endItems.remove(type)) {
            removeView(getBarButtonItemByType(type))
            fillItem()
        }
    }

    /**
     * 删除所有的栏按钮项
     */
    fun removeAllItems() {
        removeAllViews()
        startItems.clear()
        endItems.clear()
    }

    /**
     * 设置指定类型栅按钮项的提示信息
     */
    fun setTipsByType(type: BarButtonItem.Type, tips: Long) {
        getBarButtonItemByType(type)?.setTips(tips)
    }

    fun getTipsByType(type: BarButtonItem.Type): Long {
        return getBarButtonItemByType(type)?.getTips()?:0L
    }

    /**
     * 设置指定类型栅按钮项的选中状态
     */
    fun isSelectedByType(type: BarButtonItem.Type, isSelected: Boolean) {
        getBarButtonItemByType(type)?.isSelected = isSelected
    }

    /**
     * 获取制定类型栅按钮项的选中状态
     */
    fun getSelectedStatusByType(type: BarButtonItem.Type): Boolean {
        return getBarButtonItemByType(type)?.isSelected?:false
    }

    private fun fillItem() {
        removeAllViews()
        startItems.forEachIndexed { index, type ->
            fillStartItem(index, type)
        }
        endItems.forEachIndexed { index, type ->
            fillEndItem(index, type)
        }
    }

    /**
     * 从开始方向，填充工具条
     */
    private fun fillStartItem(index: Int, type: BarButtonItem.Type) {
        val view = BarButtonItem(context).apply {
            layoutParams = LayoutParams(mItemWith, mItemHeight).apply {
                marginStart = mItemWith * index
                gravity = Gravity.CENTER_VERTICAL
            }
            tag = type
            this.type = type
            action = this@BarButton.action
        }
        addView(view)
    }

    /**
     * 从结束方向，填充工具条
     */
    private fun fillEndItem(index: Int, type: BarButtonItem.Type) {
        val view = BarButtonItem(context).apply {
            layoutParams = LayoutParams(mItemWith, mItemHeight).apply {
                marginEnd = mItemWith * index
                gravity = Gravity.CENTER_VERTICAL or Gravity.END
            }
            tag = type
            this.type = type
            action = this@BarButton.action
        }
        addView(view)
    }

    private fun getBarButtonItemByType(type: BarButtonItem.Type): BarButtonItem? {
        return try {
            findViewWithTag(type)
        } catch (e: Exception) {
            e.printStackTrace()
            null
        }

    }


    fun hasType(type: BarButtonItem.Type): Boolean {
        return findViewWithTag(type) as? BarButtonItem != null
    }
}