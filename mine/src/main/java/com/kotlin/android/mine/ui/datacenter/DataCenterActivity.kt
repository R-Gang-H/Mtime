package com.kotlin.android.mine.ui.datacenter

import androidx.activity.viewModels
import com.alibaba.android.arouter.facade.annotation.Route
import com.kotlin.android.app.router.path.RouterActivityPath
import com.kotlin.android.core.BaseVMActivity
import com.kotlin.android.ktx.ext.immersive.immersive
import com.kotlin.android.mine.R
import com.kotlin.android.mine.databinding.MineActivityDataCenterBinding
import com.kotlin.android.widget.tablayout.FragPagerItemAdapter
import com.kotlin.android.widget.tablayout.FragPagerItems
import com.kotlin.android.widget.tablayout.setSelectedAnim
import com.kotlin.android.widget.titlebar.TitleBarManager
import com.kotlin.android.widget.titlebar.back

/**
 * @ProjectName:    MTime
 * @Package:        com.kotlin.android.mine.ui.datacenter
 * @ClassName:      haoruigang
 * @Description:    数据中心
 * @Author:         haoruigang
 * @CreateDate:     2022/3/11 09:15
 * @Version:
 */
@Route(path = RouterActivityPath.Mine.PAGE_DATA_CENTER)
class DataCenterBeanActivity :
    BaseVMActivity<DataCenterBeanViewModel, MineActivityDataCenterBinding>() {

    private lateinit var mTitleBar: TitleBarManager

    override fun initVM(): DataCenterBeanViewModel = viewModels<DataCenterBeanViewModel>().value

    override fun initTheme() {
        super.initTheme()
        immersive().statusBarColor(getColor(R.color.color_ffffff)).statusBarDarkFont(true)
    }

    override fun initCommonTitleView() {
        super.initCommonTitleView()
        mTitleBar = TitleBarManager.instance
        TitleBarManager.with(this)
            .setTitle(titleRes = R.string.mine_data_center)
            .back {
                finish()
            }

    }

    override fun initView() {

        mViewModel?.getPageItem(FragPagerItems(this))?.apply {
            val pagerAdapter = FragPagerItemAdapter(supportFragmentManager, this)
            mBinding?.apply {

                viewPager.apply {
                    adapter = pagerAdapter
                }
                tableLayout.apply {
                    setViewPager(viewPager)
                    setSelectedAnim()
                }
            }
        }
    }

    override fun initData() {

    }

    override fun startObserve() {

    }
}