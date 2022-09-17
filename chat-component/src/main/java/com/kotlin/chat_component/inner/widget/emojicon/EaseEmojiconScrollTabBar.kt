package com.kotlin.chat_component.inner.widget.emojicon

import android.content.Context
import android.util.AttributeSet
import android.view.LayoutInflater
import android.view.View
import android.widget.HorizontalScrollView
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.RelativeLayout
import androidx.core.view.ViewCompat
import com.hyphenate.util.DensityUtil
import com.kotlin.chat_component.R
import java.util.ArrayList

class EaseEmojiconScrollTabBar @JvmOverloads constructor(
    context: Context?,
    attrs: AttributeSet? = null
) : RelativeLayout(context, attrs) {
    private var scrollView: HorizontalScrollView? = null
    private var tabContainer: LinearLayout? = null
    private val tabList: MutableList<ImageView> = ArrayList()
    private var itemClickListener: EaseScrollTabBarItemClickListener? = null

    constructor(context: Context?, attrs: AttributeSet?, defStyle: Int) : this(context, attrs) {}

    init {
        init(context, attrs)
    }

    private fun init(context: Context?, attrs: AttributeSet?) {
        LayoutInflater.from(context).inflate(R.layout.ease_widget_emojicon_tab_bar, this)
        scrollView = findViewById<View>(R.id.scroll_view) as HorizontalScrollView
        tabContainer = findViewById<View>(R.id.tab_container) as LinearLayout
    }

    /**
     * add tab
     * @param icon
     */
    fun addTab(icon: Int) {
        val tabView = inflate(context, R.layout.ease_scroll_tab_item, null)
        val imageView = tabView.findViewById<View>(R.id.iv_icon) as ImageView
        imageView.setImageResource(icon)
        val tabWidth = 60
        val imgParams = LinearLayout.LayoutParams(
            DensityUtil.dip2px(context, tabWidth.toFloat()),
            LayoutParams.MATCH_PARENT
        )
        imageView.layoutParams = imgParams
        tabContainer!!.addView(tabView)
        tabList.add(imageView)
        val position = tabList.size - 1
        imageView.setOnClickListener {
            if (itemClickListener != null) {
                itemClickListener!!.onItemClick(position)
            }
        }
    }

    /**
     * remove tab
     * @param position
     */
    fun removeTab(position: Int) {
        tabContainer!!.removeViewAt(position)
        tabList.removeAt(position)
    }

    fun selectedTo(position: Int) {
        scrollTo(position)
        for (i in tabList.indices) {
            if (position == i) {
                tabList[i].setBackgroundColor(resources.getColor(R.color.emojicon_tab_selected))
            } else {
                tabList[i].setBackgroundColor(resources.getColor(R.color.emojicon_tab_nomal))
            }
        }
    }

    private fun scrollTo(position: Int) {
        val childCount = tabContainer!!.childCount
        if (position < childCount) {
            scrollView!!.post(Runnable {
                val mScrollX = tabContainer!!.scrollX
                val childX = ViewCompat.getX(tabContainer!!.getChildAt(position))
                    .toInt()
                if (childX < mScrollX) {
                    scrollView!!.scrollTo(childX, 0)
                    return@Runnable
                }
                val hsvWidth = scrollView!!.width
                val childRight = childX + tabContainer!!.getChildAt(position).width
                val scrollRight = mScrollX + hsvWidth
                if (childRight > scrollRight) {
                    scrollView!!.scrollTo(childRight - scrollRight, 0)
                    return@Runnable
                }
            })
        }
    }

    fun setTabBarItemClickListener(itemClickListener: EaseScrollTabBarItemClickListener?) {
        this.itemClickListener = itemClickListener
    }

    interface EaseScrollTabBarItemClickListener {
        fun onItemClick(position: Int)
    }
}