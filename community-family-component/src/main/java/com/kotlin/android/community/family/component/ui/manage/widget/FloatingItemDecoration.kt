package com.kotlin.android.community.family.component.ui.manage.widget

import android.content.Context
import android.graphics.Canvas
import android.graphics.Rect
import android.graphics.Typeface
import android.graphics.drawable.Drawable
import android.text.TextUtils
import android.util.TypedValue
import android.view.Gravity
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.kotlin.android.ktx.ext.dimension.screenWidth
import java.util.*

/**
 * @author vivian.wei
 * @date 2020/9/11
 * @desc 分组Title悬浮的效果，只支持LinearLayoutManager垂直滑动的情况
 */
class FloatingItemDecoration(context: Context) : RecyclerView.ItemDecoration() {
    private var mDivider: Drawable? = null
    private var dividerHeight = 0
    private val keys: MutableMap<Int, CharSequence> = HashMap()
    private val titleViews: MutableMap<CharSequence?, View> = HashMap()
    private var mTitleHeight = 0
    private var mTitleTextColor = 0
    private var mTitleTextSize = 0f
    private var mBold = false
    private var mCenter = false
    private var mTitleBackgroundColor = 0
    private var mContext: Context? = null

    /**
     * 滚动列表的时候是否一直显示悬浮头部
     */
    private var showFloatingHeaderOnScrolling = true
    private var mLeftOffset = 0
    private var mTitleViewPaddingLeft = 0

    init {
        mContext = context
    }

    override fun onDraw(c: Canvas, parent: RecyclerView, state: RecyclerView.State) {
        super.onDraw(c, parent, state)
        drawVertical(c, parent)
    }

    override fun onDrawOver(c: Canvas, parent: RecyclerView, state: RecyclerView.State) {
        super.onDrawOver(c, parent, state)
        if (!showFloatingHeaderOnScrolling) {
            return
        }
        val firstVisiblePos = (parent.layoutManager as LinearLayoutManager?)!!.findFirstVisibleItemPosition()
        if (firstVisiblePos == RecyclerView.NO_POSITION) {
            return
        }
        val title = getTitle(firstVisiblePos)
        if (TextUtils.isEmpty(title)) {
            return
        }
        var flag = false
        if (getTitle(firstVisiblePos + 1) != null && title != getTitle(firstVisiblePos + 1)) {
            //说明是当前组最后一个元素，但不一定碰撞了
            val child = parent.findViewHolderForAdapterPosition(firstVisiblePos)!!.itemView
            if (child.top + child.measuredHeight < mTitleHeight) {
                //进一步检测碰撞
                c.save() //保存画布当前的状态
                flag = true
                c.translate(0f, child.top + child.measuredHeight - mTitleHeight.toFloat()) //负的代表向上
            }
        }
        val left = parent.paddingLeft
        val right = parent.width - parent.paddingRight
        val top = parent.paddingTop
        val bottom = top + mTitleHeight
        drawTitleView(mContext, title, c, left, top, right, bottom)
        if (flag) {
            //还原画布为初始状态
            c.restore()
        }
    }

    override fun getItemOffsets(outRect: Rect, view: View, parent: RecyclerView, state: RecyclerView.State) {
        super.getItemOffsets(outRect, view, parent, state)
        val pos = parent.getChildViewHolder(view).adapterPosition
        if (keys.containsKey(pos)) { //留出头部偏移
            outRect[0, mTitleHeight, 0] = 0
        } else {
            outRect[0, dividerHeight, 0] = 0
        }
    }

    /**
     * *如果该位置没有，则往前循环去查找标题，找到说明该位置属于该分组
     *
     * @param position
     * @return
     */
    private fun getTitle(position: Int): CharSequence? {
        var position = position
        while (position >= 0) {
            if (keys.containsKey(position)) {
                return keys[position]
            }
            position--
        }
        return null
    }

    private fun drawVertical(c: Canvas, parent: RecyclerView) {
        val left = parent.paddingLeft
        val right = parent.width - parent.paddingRight
        var top = 0
        var bottom = 0
        for (i in 0 until parent.childCount) {
            val child = parent.getChildAt(i)
            val params = child.layoutParams as RecyclerView.LayoutParams
            if (!keys.containsKey(params.viewLayoutPosition)) {
                //画普通分割线
                if (null != mDivider) {
                    top = child.top - params.topMargin - dividerHeight
                    bottom = top + dividerHeight
                    mDivider!!.setBounds(left, top, right, bottom)
                    mDivider!!.draw(c)
                }
            } else {
                //画头部
                top = child.top - params.topMargin - mTitleHeight
                bottom = top + mTitleHeight
                val text = keys[params.viewLayoutPosition]
                drawTitleView(mContext, text, c, left, top, right, bottom)
            }
        }
    }

    private fun drawTitleView(context: Context?, text: CharSequence?, c: Canvas, left: Int, top: Int, right: Int, bottom: Int) {
        val titleView: TextView?
        if (!titleViews.containsKey(text)) {
            titleView = TextView(context)
            val params = ViewGroup.LayoutParams(screenWidth, mTitleHeight)
            titleView.layoutParams = params
            titleView.gravity = if (mCenter) Gravity.CENTER else Gravity.CENTER_VERTICAL
            titleView.setBackgroundColor(mTitleBackgroundColor)
            titleView.setTextColor(mTitleTextColor)
            if (mBold) {
                titleView.setTypeface(Typeface.defaultFromStyle(Typeface.BOLD))
            }
            titleView.setTextSize(TypedValue.COMPLEX_UNIT_PX, mTitleTextSize)
            titleView.setPadding(mTitleViewPaddingLeft, 0, 0, 0)
            titleView.text = text
            val widthSpec = View.MeasureSpec.makeMeasureSpec(params.width,
                    if (params.width == ViewGroup.LayoutParams.WRAP_CONTENT) View.MeasureSpec.AT_MOST else View.MeasureSpec.EXACTLY)
            val heightSpec = View.MeasureSpec.makeMeasureSpec(params.height,
                    if (params.height == ViewGroup.LayoutParams.WRAP_CONTENT) View.MeasureSpec.AT_MOST else View.MeasureSpec.EXACTLY)
            titleView.measure(widthSpec, heightSpec)
            titleViews[text] = titleView
        } else {
            titleView = titleViews[text] as TextView?
        }
        c.save()
        c.translate(left + mLeftOffset.toFloat(), top.toFloat())
        titleView!!.layout(left, top, right, bottom)
        titleView.draw(c)
        c.restore()
    }

    fun setShowFloatingHeaderOnScrolling(showFloatingHeaderOnScrolling: Boolean) {
        this.showFloatingHeaderOnScrolling = showFloatingHeaderOnScrolling
    }

    fun setTitles(keys: Map<Int, CharSequence>?) {
        this.keys.clear()
        this.keys.putAll(keys!!)
    }

    fun appendTitles(key: Int, text: CharSequence) {
        keys[key] = text
    }

    fun setTitleHeight(titleHeight: Int) {
        mTitleHeight = titleHeight
    }

    fun setTitleTextSize(textSize: Float) {
        mTitleTextSize = textSize
    }

    fun setTitleTextColor(color: Int) {
        mTitleTextColor = color
    }

    fun setTitleTextBold(bold: Boolean) {
        mBold = bold
    }

    // 文字是否居中
    fun setTitleTextGravity(center: Boolean) {
        mCenter = center
    }

    fun setTtitleBackground(color: Int) {
        mTitleBackgroundColor = color
    }

    fun setTitleViewmTitleViewPaddingLeft(paddingLeft: Int) {
        mTitleViewPaddingLeft = paddingLeft
    }

    fun clearTitles() {
        keys.clear()
    }

    fun setLeftOffset(leftOffset: Int) {
        mLeftOffset = leftOffset
    }
}