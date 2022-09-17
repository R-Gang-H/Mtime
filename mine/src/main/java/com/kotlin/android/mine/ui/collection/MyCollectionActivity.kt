package com.kotlin.android.mine.ui.collection

import androidx.activity.viewModels
import com.alibaba.android.arouter.facade.annotation.Route
import com.kotlin.android.core.BaseVMActivity
import com.kotlin.android.mine.R
import com.kotlin.android.mine.databinding.ActivityMyCollectionBinding
import com.kotlin.android.app.router.path.RouterActivityPath
import com.kotlin.android.widget.tablayout.FragPagerItemAdapter
import com.kotlin.android.widget.tablayout.FragPagerItems
import com.kotlin.android.widget.tablayout.setSelectedAnim
import com.kotlin.android.widget.titlebar.CommonTitleBar
import com.ogaclejapan.smarttablayout.utils.v4.FragmentPagerItems
import kotlinx.android.synthetic.main.activity_my_collection.*

/**
 * create by lushan on 2020/9/11
 * description:我的收藏页面
 */
@Route(path = RouterActivityPath.Mine.PAGE_MY_COLLECTION_ACTIVITY)
class MyCollectionActivity : BaseVMActivity<MyCollectionViewModel, ActivityMyCollectionBinding>() {

    override fun initVM(): MyCollectionViewModel  = viewModels<MyCollectionViewModel>().value

    override fun initCommonTitleView() {
        super.initCommonTitleView()
        CommonTitleBar().init(this).setTitle(R.string.mine_collection_title).create()

    }
    override fun initView() {
        mViewModel?.getPageItem(FragPagerItems(this))?.apply {
            val pagerAdapter = FragPagerItemAdapter(supportFragmentManager, this)
            viewPager?.apply {
                adapter = pagerAdapter
            }
            tableLayout?.apply {
                setViewPager(viewPager)
                setSelectedAnim()
            }
        }
    }

    override fun initData() {
    }

    override fun startObserve() {
    }
}