package com.kotlin.android.card.monopoly.widget

import android.content.Context
import android.graphics.Typeface
import android.util.AttributeSet
import android.util.TypedValue
import android.view.Gravity
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import android.widget.TextView
import androidx.annotation.ColorInt
import androidx.core.view.children
import com.kotlin.android.card.monopoly.R
import com.kotlin.android.app.data.entity.monopoly.SuitCategory
import com.kotlin.android.ktx.ext.core.getColor
import com.kotlin.android.ktx.ext.dimension.dp

/**
 * 大富翁中间级别选择栏：简、精、终、限
 *
 * Created on 2020/8/18.
 *
 * @author o.s
 */
class CenterBarView : LinearLayout {

    private val mLabelWidth = 58.dp
    private val mDivideWidth = (1.5F).dp
    private val mDivideHeight = 16.dp
    private val mTextSize = 16F
    private val mSelectedTextSize = 21F

    private val menuViewStore = ArrayList<TextView>()

    var action: ((category: SuitCategory) -> Unit)? = null

    var theme: Theme = Theme.DARK
        set(value) {
            field = value
            notifyChange()
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

//        addItemView(Category.SIMPLE)
//        addItemView(Category.ESSENCE)
//        addItemView(Category.ULTIMATE)
//        addItemView(Category.LIMIT)
    }

    /**
     * 设置类别：简、精、终、限
     */
    fun setCategories(categories: List<SuitCategory>?) {
        menuViewStore.clear()
        removeAllViews()
        categories?.forEach {
            addItemView(it)
        }
    }

    /**
     * 添加项
     */
    fun addItemView(category: SuitCategory) {
        if (menuViewStore.size > 0) {
            addDivideView()
        }
        addLabelView(category)
    }

    /**
     * 设置当前选择项
     */
    fun setCurrentItem(position: Int) {
        if (position in 0 until menuViewStore.size) {
            click(menuViewStore[position])
        }
    }

    private fun addLabelView(category: SuitCategory): TextView {
        return TextView(context).apply {
            LayoutParams(mLabelWidth, LayoutParams.MATCH_PARENT).apply {
                layoutParams = this
            }
            gravity = Gravity.CENTER
            setTextColor(getNormalColor())
            setTextSize(TypedValue.COMPLEX_UNIT_SP, mTextSize)
            tag = category
            text = category.shortName
            setOnClickListener {
                click(this)
            }
            addView(this)
            menuViewStore.add(this)
        }
    }

    private fun addDivideView(): View {
        return View(context).apply {
            LayoutParams(mDivideWidth, mDivideHeight).apply {
                gravity = Gravity.CENTER_VERTICAL
                layoutParams = this
            }
            alpha = getDivideAlpha()
            setBackgroundColor(getNormalColor())
            addView(this)
        }
    }

    private fun click(v: TextView) {
        if (!v.isSelected) {
            menuViewStore.forEach {
                if (it.isSelected) {
                    it.isSelected = false
                    normal(it)
                }
            }
            v.isSelected = true
            selected(v)
            action?.invoke(v.tag as SuitCategory)
        }
    }

    private fun normal(v: TextView) {
        v.apply {
            setTextColor(getNormalColor())
            setTextSize(TypedValue.COMPLEX_UNIT_SP, mTextSize)
            typeface = Typeface.defaultFromStyle(Typeface.NORMAL)
        }
    }

    private fun selected(v: TextView) {
        v.apply {
            setTextColor(getSelectedColor())
            setTextSize(TypedValue.COMPLEX_UNIT_SP, mSelectedTextSize)
            typeface = Typeface.defaultFromStyle(Typeface.BOLD)
        }
    }

    @ColorInt
    private fun getNormalColor(): Int {
        return when (theme) {
            Theme.DARK -> getColor(R.color.color_c4ffa0)
            Theme.LIGHT -> getColor(R.color.color_4e5e73)
        }
    }

    @ColorInt
    private fun getSelectedColor(): Int {
        return when (theme) {
            Theme.DARK -> getColor(R.color.color_ffffff)
            Theme.LIGHT -> getColor(R.color.color_20a0da)
        }
    }

    private fun getDivideAlpha(): Float {
        return when (theme) {
            Theme.DARK -> 0.3F
            Theme.LIGHT -> 0.1F
        }
    }

    /**
     * 样式变化
     */
    private fun notifyChange() {
        children.forEach {
            if (it.isSelected && it is TextView) {
                it.setTextColor(getSelectedColor())
            } else {
                if (it is TextView) {
                    it.setTextColor(getNormalColor())
                } else {
                    it.setBackgroundColor(getNormalColor())
                    it.alpha = getDivideAlpha()
                }
            }
        }
    }

    /**
     * 套装类别
     */
    enum class Category(val label: String) {

        /**
         * 简
         */
        SIMPLE("简"),

        /**
         * 精
         */
        ESSENCE("精"),

        /**
         * 终
         */
        ULTIMATE("终"),

        /**
         * 限
         */
        LIMIT("限");

        companion object {
            fun obtain(label: String): Category {
                return when (label) {
                    SIMPLE.label -> SIMPLE
                    ESSENCE.label -> ESSENCE
                    ULTIMATE.label -> ULTIMATE
                    LIMIT.label -> LIMIT
                    else -> SIMPLE
                }
            }
        }
    }

    /**
     * 主题样式
     */
    enum class Theme {

        /**
         * 亮色主题：（白底）
         */
        LIGHT,

        /**
         * 暗色主题：（深色底）
         */
        DARK
    }
}