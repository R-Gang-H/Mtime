package com.kotlin.android.widget.filter.view

import android.content.Context
import android.graphics.Color
import android.graphics.Typeface.DEFAULT_BOLD
import android.text.TextUtils
import android.util.AttributeSet
import android.util.TypedValue
import android.view.Gravity
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import android.widget.RelativeLayout
import android.widget.TextView
import com.kotlin.android.ktx.ext.core.getColor
import com.kotlin.android.ktx.ext.core.setTextStyle
import com.kotlin.android.ktx.ext.dimension.dp
import com.kotlin.android.widget.R
import com.kotlin.android.widget.filter.adapter.MenuAdapter
import java.util.*

/**
 * 选择tab
 */
class FixedTabIndicator : LinearLayout {
    private var mContext: Context? = null

    private val mTabTextSize = 13 // 指针文字的大小,sp
    private val mTabDefaultColor = getColor(R.color.color_3d4955) /*-0x3d4955*/ // 未选中默认颜色
    private val mTabSelectedColor =getColor(R.color.color_20a0da)/* -0x20a0da*/ // 指针选中颜色
    private var drawableRight = 10.dp
    private var mTabCount // 设置的条目数量
            = 0
    var currentIndicatorPosition // 上一个指针选中条目
            = 0
        private set
    var lastIndicatorPosition // 上一个指针选中条目
            = 0
        private set
    private var mOnItemClickListener: OnItemClickListener? = null

    @JvmOverloads
    constructor(context: Context, attrs: AttributeSet? = null) : super(context, attrs) {
        init(context)
    }

    constructor(context: Context, attrs: AttributeSet?, defStyle: Int) : super(
        context,
        attrs,
        defStyle
    ) {
        init(context)
    }

    /**
     * 条目点击事件
     */
    interface OnItemClickListener {
        /**
         * 回调方法
         *
         * @param v        当前点击的view
         * @param position 当前点击的position
         * @param open     当前的状态，蓝色为open,筛选器为打开状态
         */
        fun onItemClick(v: View?, position: Int, open: Boolean)
    }

    fun setOnItemClickListener(itemClickListenner: OnItemClickListener?) {
        mOnItemClickListener = itemClickListenner
    }

    private fun init(context: Context) {
        this.mContext = context
        orientation = HORIZONTAL
        setBackgroundColor(Color.WHITE)
        setWillNotDraw(false)
    }


    /**
     * 添加相应的布局进此容器
     */
    fun setTitles(list: List<String?>?) {
        check(!(list == null || list.isEmpty())) { "条目数量位空" }
        removeAllViews()
        mTabCount = list.size
        for (pos in 0 until mTabCount) {
            addView(generateTextView(list[pos], pos))
        }
        postInvalidate()
    }

    fun setTitles(list: Array<String?>) {
        setTitles(Arrays.asList(*list))
    }

    fun setTitles(menuAdapter: MenuAdapter?) {
        if (menuAdapter == null) {
            return
        }
        removeAllViews()
        mTabCount = menuAdapter.menuCount
        for (pos in 0 until mTabCount) {
            addView(generateTextView(menuAdapter.getMenuTitle(pos), pos))
        }
        postInvalidate()
    }

    private fun switchTab(pos: Int) {
        val tv = getChildAtCurPos(pos)
        val drawable = tv.compoundDrawables[2]
        val level = drawable.level
        if (mOnItemClickListener != null) {
            mOnItemClickListener!!.onItemClick(tv, pos, level == 1)
        }
        if (lastIndicatorPosition == pos) {
            // 点击同一个条目时
            tv.setTextColor(if (level == 0) mTabSelectedColor else mTabDefaultColor)
            drawable.level = 1 - level
            return
        }
        currentIndicatorPosition = pos
        resetPos(lastIndicatorPosition)

        //highLightPos(pos);
        tv.setTextColor(mTabSelectedColor)
        tv.compoundDrawables[2].level = 1
        lastIndicatorPosition = pos
    }

    /**
     * 重置字体颜色
     */
    private fun resetPos(pos: Int) {
        val tv = getChildAtCurPos(pos)
        tv.setTextColor(mTabDefaultColor)
        tv.compoundDrawables[2].level = 0
    }

    /**
     * 重置当前字体颜色
     */
    fun resetCurrentPos() {
        resetPos(currentIndicatorPosition)
    }

    /**
     * 获取当前pos内的TextView
     */
    fun getChildAtCurPos(pos: Int): TextView {
        return (getChildAt(pos) as ViewGroup).getChildAt(0) as TextView
    }

    /**
     * 直接用TextView使用weight不能控制图片，需要用用父控件包裹
     */
    private fun generateTextView(title: String?, pos: Int): View {
        // 子空间TextView
        val tv = TextView(mContext)
        tv.gravity = Gravity.CENTER
        tv.text = title
        tv.setTextSize(TypedValue.COMPLEX_UNIT_SP, mTabTextSize.toFloat())
        tv.setTextColor(mTabDefaultColor)
        tv.setSingleLine()
        tv.typeface = DEFAULT_BOLD
        tv.ellipsize = TextUtils.TruncateAt.END
        tv.maxEms = 6 //限制4个字符
        val drawable = resources.getDrawable(R.drawable.level_filter)
        tv.setCompoundDrawablesWithIntrinsicBounds(null, null, drawable, null)
        tv.compoundDrawablePadding = drawableRight

        // 将TextView添加到父控件RelativeLayout
        val rl = RelativeLayout(mContext)
        val rlParams = RelativeLayout.LayoutParams(-2, -2)
        rlParams.addRule(RelativeLayout.CENTER_IN_PARENT)
        rl.addView(tv, rlParams)
        rl.id = pos

        // 再将RelativeLayout添加到LinearLayout中
        val params = LayoutParams(-1, -1)
        params.weight = 1f
        params.gravity = Gravity.CENTER
        rl.layoutParams = params
        rl.setOnClickListener { v -> // 设置点击事件
            switchTab(v.id)
        }
        return rl
    }

    /**
     * 高亮字体颜色
     */
    fun highLightPos(pos: Int) {
        val tv = getChildAtCurPos(pos)
        tv.setTextColor(mTabSelectedColor)
        tv.compoundDrawables[2].level = 1
    }

    fun setCurrentText(text: String?) {
        setPositionText(currentIndicatorPosition, text)
    }

    fun setPositionText(position: Int, text: String?) {
        require(!(position < 0 || position > mTabCount - 1)) { "position 越界" }
        val tv = getChildAtCurPos(position)
        tv.setTextColor(mTabDefaultColor)
        tv.text = text
        tv.compoundDrawables[2].level = 0
    }
}