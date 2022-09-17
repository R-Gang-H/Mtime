package com.kotlin.android.share.ui.widget

import android.content.Context
import android.graphics.drawable.Drawable
import android.util.AttributeSet
import android.view.View
import androidx.appcompat.widget.LinearLayoutCompat
import androidx.core.view.children
import androidx.viewpager.widget.ViewPager
import com.kotlin.android.ktx.ext.log.e
import com.kotlin.android.ktx.ext.dimension.dp

/**
 * 分享UI分页指示器
 *
 * Created on 2020/7/1.
 *
 * @author o.s
 */
class IndicatorView : LinearLayoutCompat {

    private var normalColor: Drawable? = null
    private var selectColor: Drawable? = null
    private var normalAlpha: Float = 0.2F // 未选中的透明度
    private var selectAlpha: Float = 1F // 选中的透明度

    private val indicatorWidth = 6.dp
    private val cornerRadius = indicatorWidth / 2

    constructor(context: Context) : super(context)
    constructor(context: Context, attrs: AttributeSet) : super(context, attrs)
    constructor(context: Context, attrs: AttributeSet, defStyleAttr: Int) : super(context, attrs, defStyleAttr)

    fun setSelectColor(selectColor: Drawable) {
        this.selectColor = selectColor
    }

    fun setNormalColor(normalColor: Drawable) {
        this.normalColor = normalColor
    }

    fun setNormalAlpha(normalAlpha: Float) {
        this.normalAlpha = normalAlpha
    }

    fun setViewPager(viewPager: ViewPager?) {
        viewPager?.run {
            val count = adapter?.count ?: 0
            initView(count, currentItem)
            addOnPageChangeListener(pageChangeListener)
        }
    }

    fun select(position: Int) {
        changeIndicator(position)
    }

    private fun initView(count: Int, currentItem: Int) {
        removeAllViews()
        if (count > 1) {
//            count.e()
            (0 until count).forEach {
                val itemView = View(context)
                val lp = LayoutParams(indicatorWidth, indicatorWidth)
                lp.leftMargin = if (it == 0) 0 else indicatorWidth
                itemView.layoutParams = lp
                itemView.background = normalColor
                addView(itemView)
            }
        }
        select(currentItem)
    }

    private fun changeIndicator(position: Int) {
        if (childCount <= 0) {
            return
        }
        val butterPosition = position % childCount
        children.forEachIndexed { index, view ->
            if (index == butterPosition) {
                selectIndicator(view)
            } else {
                normalIndicator(view)
            }
        }
    }

    private fun selectIndicator(view: View) {
        if (needUpdateBackground()) {
            view.background = selectColor
        }
        view.alpha = selectAlpha
    }

    private fun normalIndicator(view: View) {
        if (needUpdateBackground()) {
            view.background = normalColor
        }
        view.alpha = normalAlpha
    }

    private fun needUpdateBackground(): Boolean = normalColor != selectColor

    private val pageChangeListener = object : ViewPager.OnPageChangeListener {
        /**
         * Called when the scroll state changes. Useful for discovering when the user
         * begins dragging, when the pager is automatically settling to the current page,
         * or when it is fully stopped/idle.
         *
         * @param state The new scroll state.
         * @see ViewPager.SCROLL_STATE_IDLE
         *
         * @see ViewPager.SCROLL_STATE_DRAGGING
         *
         * @see ViewPager.SCROLL_STATE_SETTLING
         */
        override fun onPageScrollStateChanged(state: Int) {
        }

        /**
         * This method will be invoked when the current page is scrolled, either as part
         * of a programmatically initiated smooth scroll or a user initiated touch scroll.
         *
         * @param position Position index of the first page currently being displayed.
         * Page position+1 will be visible if positionOffset is nonzero.
         * @param positionOffset Value from [0, 1) indicating the offset from the page at position.
         * @param positionOffsetPixels Value in pixels indicating the offset from position.
         */
        override fun onPageScrolled(position: Int, positionOffset: Float, positionOffsetPixels: Int) {
        }

        /**
         * This method will be invoked when a new page becomes selected. Animation is not
         * necessarily complete.
         *
         * @param position Position index of the new selected page.
         */
        override fun onPageSelected(position: Int) {
            select(position)
        }

    }

    class Builder {
        private var context: Context? = null
        private var viewPager: ViewPager? = null
        private var selectColor: Drawable? = null
        private var normalColor: Drawable? = null
        private var normalAlpha: Float = 0.2F

        fun setContext(context: Context): Builder {
            this.context = context
            return this
        }

        fun setViewPager(viewPager: ViewPager): Builder {
            this.viewPager = viewPager
            return this
        }

        fun setSelectColor(selectColor: Drawable): Builder {
            this.selectColor = selectColor
            return this
        }

        fun setNormalColor(normalColor: Drawable): Builder {
            this.normalColor = normalColor
            return this
        }

        fun setNormalAlpha(normalAlpha: Float): Builder {
            this.normalAlpha = normalAlpha
            return this
        }

        fun builder(): IndicatorView? {
            return context?.run {
                IndicatorView(this).apply {
                    selectColor?.run { setSelectColor(this) }
                    normalColor?.run { setNormalColor(this) }
                    setNormalAlpha(normalAlpha)
                    setViewPager(viewPager)
                }
            }
        }
    }
}