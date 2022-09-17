package com.kotlin.android.live.component.ui.widget

import androidx.fragment.app.FragmentManager
import com.kotlin.android.widget.tablayout.FragPagerItemAdapter
import com.kotlin.android.widget.tablayout.FragPagerItems
import com.ogaclejapan.smarttablayout.utils.v4.FragmentPagerItems

/**
 * create by lushan on 2021/4/22
 * description:
 */
class LiveFragmentPageAdapter(fm: FragmentManager, private val pages: FragPagerItems): FragPagerItemAdapter(fm,pages) {

//    override fun getItemId(position: Int): Long {
//        return  getPagerItem(position).hashCode().toLong()
//    }
}