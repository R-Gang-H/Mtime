package com.kotlin.android.widget.tablayout

import android.text.TextUtils
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.annotation.LayoutRes
import androidx.viewpager.widget.PagerAdapter
import androidx.viewpager.widget.ViewPager
import com.kotlin.android.ktx.ext.core.gone
import com.kotlin.android.ktx.ext.core.visible
import com.kotlin.android.widget.R
import com.ogaclejapan.smarttablayout.SmartTabLayout

/**
 * xx
 */

fun SmartTabLayout.setCustomTabView(
    @LayoutRes layoutResId: Int,
    isSelectedAnim: Boolean = true,
    isGoneTag: Boolean = true
) {
    setCustomTabView(ExTabProvider(this, layoutResId, isSelectedAnim, isGoneTag))
}

fun SmartTabLayout.updateTag(vararg tags: String) {
    tags.forEachIndexed { index, s ->
        updateTag(index, s)
    }
}

fun SmartTabLayout.updateTag(position: Int, tag: String) {
    getTabAt(position)?.findViewById<TextView>(R.id.tag)?.apply {
        text = tag
        visible(!TextUtils.isEmpty(tag))
    }
}

fun SmartTabLayout.goneTag(position: Int) {
    getTabAt(position)?.findViewById<TextView>(R.id.tag)?.apply {
        text = ""
        gone()
    }
}

private class ExTabProvider(
    private val tabLayout: SmartTabLayout,
    @LayoutRes val layoutResId: Int,
    val isSelectedAnim: Boolean = true,
    var isGoneTag: Boolean = true
) :
    SmartTabLayout.TabProvider, ViewPager.OnPageChangeListener {
    private val inflater: LayoutInflater = LayoutInflater.from(tabLayout.context)
    private val animListener = TabSelectedAnimListener(tabLayout)

    init {
        tabLayout.setOnPageChangeListener(this)
        tabLayout.setCustomTabView(this)
        //tab初始化完毕后选中0，但不会回调，所以这里需要手动回调下
        tabLayout.post {
            tabLayout.goneTag(0)
        }
    }

    override fun createTabView(container: ViewGroup?, position: Int, adapter: PagerAdapter?): View {
        val tabView = inflater.inflate(layoutResId, container, false)
        adapter?.let {
            if (it is FragPagerItemAdapter) {
                val textView = tabView.findViewById<TextView>(R.id.mTabTextView)
                val tagView = tabView.findViewById<TextView>(R.id.tag)
                textView.text = it.getPageTitle(position)
                tagView.text = it.getTag(position)
                tagView.visible(!TextUtils.isEmpty(tagView.text))
            }
        }
        return tabView
    }

    override fun onPageScrolled(position: Int, positionOffset: Float, positionOffsetPixels: Int) {
        if (isSelectedAnim) {
            animListener.onPageScrolled(position, positionOffset, positionOffsetPixels)
        }
    }

    override fun onPageScrollStateChanged(state: Int) {
        if (isSelectedAnim) {
            animListener.onPageScrollStateChanged(state)
        }
    }

    override fun onPageSelected(position: Int) {
        if (isGoneTag)
            tabLayout.goneTag(position)
        if (isSelectedAnim) {
            animListener.onPageSelected(position)
        }
    }

}