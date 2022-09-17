package com.kotlin.android.widget.tablayout

import android.graphics.Typeface
import android.widget.TextView
import androidx.viewpager.widget.ViewPager
import com.kotlin.android.widget.R
import com.ogaclejapan.smarttablayout.SmartTabLayout

/**
 * @author ZhouSuQiang
 * @email zhousuqiang@126.com
 * @date 2020/7/8
 *
 * 首页-tab选中后的效果处理：放大、加粗
 */

fun SmartTabLayout.setSelectedAnim() {
    setOnPageChangeListener(TabSelectedAnimListener(this))
}

class TabSelectedAnimListener(private val tabLayout: SmartTabLayout) : ViewPager.OnPageChangeListener {

    private var curPosition: Int = 0

    /**
     * 设置完数据后调用此方法，手动使指定tab也具有相关的选中效果
     * tab初始化完毕后选中0，但不会回调，所以这里需要手动回调下
     */
    init {
        tabLayout.post {
            handleTab(curPosition, true)
        }
    }

    override fun onPageScrollStateChanged(state: Int) {

    }

    override fun onPageScrolled(position: Int, positionOffset: Float, positionOffsetPixels: Int) {

    }

    override fun onPageSelected(position: Int) {
        handleTab(curPosition, false)
        handleTab(position, true)
        curPosition = position
    }

    private fun handleTab(position: Int, isSelected: Boolean) {
        tabLayout.getTabAt(position)?.findViewById<TextView>(R.id.mTabTextView)?.run {
            val value = if (isSelected) 1.285f else 1.0f
            animate().scaleX(value).scaleY(value).setDuration(200).start()
            typeface = if (isSelected) {
                Typeface.defaultFromStyle(Typeface.BOLD)
            } else {
                Typeface.defaultFromStyle(Typeface.NORMAL)
            }
        }
    }
}