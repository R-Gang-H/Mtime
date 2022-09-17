package com.kotlin.android.home.ui.original

import android.util.SparseArray
import android.widget.TextView
import androidx.core.util.containsKey
import androidx.core.util.getOrElse
import androidx.fragment.app.Fragment
import androidx.fragment.app.commit
import com.google.android.material.tabs.TabLayout
import com.kotlin.android.core.BaseVMFragment
import com.kotlin.android.home.R
import com.kotlin.android.home.databinding.FagOriginalBinding

/**
 * @author ZhouSuQiang
 * @email zhousuqiang@126.com
 * @date 2020/7/9
 * 
 * 首页-时光原创
 */
class OriginalFragment : BaseVMFragment<OriginalViewModel, FagOriginalBinding>(),
    TabLayout.OnTabSelectedListener {
    
    private val fragments = SparseArray<Fragment>()
    private var curFragment: Fragment? = null
    private val titleTags by lazy { resources.getStringArray(R.array.home_original_title_tags) }
    
    override fun initView() {
        mBinding?.apply {
            val titles = resources.getStringArray(R.array.home_original_titles)
            titles.forEachIndexed { index, it ->
                mTabLayout.addTab(mTabLayout.newTab().apply {
                    (layoutInflater
                        .inflate(R.layout.layout_original_sub_tab, null)
                            as TextView).apply {
                        text = it
                        customView = this
                    }
                }, index == 0)
            }
            mTabLayout.addOnTabSelectedListener(this@OriginalFragment)
        }
    }

    override fun startObserve() {

    }

    override fun initData() {
        select(0)
    }
    
    private fun select(position: Int) {
        childFragmentManager.commit {
            curFragment?.apply { 
                hide(this)
            }

            curFragment = fragments.getOrElse(position) {
                OriginalSubPageFragment.newInstance(titleTags[position]).apply {
                    fragments.put(position, this)
                }
            }
            
            curFragment?.let { 
                if (it.isAdded) {
                    show(it)
                } else {
                    add(R.id.fragmentFl, it, "original_sub_fragment_$position")
                }
            }
        }
    }

    override fun onTabSelected(tab: TabLayout.Tab?) {
        tab?.let { 
            select(it.position)
        }
    }

    override fun onTabUnselected(tab: TabLayout.Tab?) {

    }

    override fun onTabReselected(tab: TabLayout.Tab?) {

    }
}