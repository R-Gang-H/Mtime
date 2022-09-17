package com.kotlin.android.home.ui.toplist

import android.os.Bundle
import com.alibaba.android.arouter.facade.annotation.Route
import com.kotlin.android.app.router.path.RouterActivityPath
import com.kotlin.android.core.BaseVMActivity
import com.kotlin.android.core.BaseViewModel
import com.kotlin.android.home.R
import com.kotlin.android.home.databinding.FragToplistBinding
import com.kotlin.android.home.ui.toplist.constant.TopListConstant
import com.kotlin.android.ktx.ext.core.setBackground
import com.kotlin.android.ktx.ext.immersive.immersive
import com.kotlin.android.router.ext.put
import com.kotlin.android.widget.tablayout.FragPagerItemAdapter
import com.kotlin.android.widget.tablayout.FragPagerItems
import com.kotlin.android.widget.tablayout.setSelectedAnim
import com.kotlin.android.widget.titlebar.ThemeStyle
import com.kotlin.android.widget.titlebar.TitleBarManager

/**
 * 创建者: vivian.wei
 * 创建时间: 2022/5/12
 * 描述: 榜单页
 */
@Route(path = RouterActivityPath.Home.PAGER_TOPLIST_ACTIVITY)
class TopListActivity : BaseVMActivity<BaseViewModel, FragToplistBinding>() {

    override fun initTheme() {
        super.initTheme()
        immersive().transparentStatusBar(isFitsSystemWindows = false)
                .statusBarDarkFont(true)
    }

    override fun initCommonTitleView() {
        super.initCommonTitleView()
        TitleBarManager.with(this, ThemeStyle.STANDARD_STATUS_BAR)
                .setTitle(
                        titleRes = R.string.home_toplist_title,
                )
                .addItem(
                        drawableRes = R.drawable.ic_title_bar_36_back
                ) {
                    onBackPressed()
                }
    }

    override fun initView() {
        mBinding?.apply {
            // tab渐变
            mFragToplistTabLayout.setBackground(
                    colorRes = R.color.color_f9f9fb,
                    endColorRes = R.color.color_ffffff,
            )
            val adapter = FragPagerItemAdapter(
                    supportFragmentManager, FragPagerItems(this@TopListActivity)
                    .add(
                            title = "电影",
                            clazz = TopListTypeFragment::class.java,
                            args = Bundle().put(
                                    TopListConstant.KEY_TOPLIST_TYPE,
                                    TopListConstant.TOPLIST_TYPE_MOVIE
                            )
                    )
                    .add(
                            title = "电视剧", clazz = TopListTypeFragment::class.java,
                            args = Bundle().put(TopListConstant.KEY_TOPLIST_TYPE, TopListConstant.TOPLIST_TYPE_TV)
                    )
                    .add(
                            title = "影人", clazz = TopListTypeFragment::class.java,
                            args = Bundle().put(
                                    TopListConstant.KEY_TOPLIST_TYPE,
                                    TopListConstant.TOPLIST_TYPE_PERSON
                            )
                    )
                    .add(title = "游戏", clazz = TopListGameFragment::class.java)
            )
            mFragToplistViewPager.adapter = adapter
            mFragToplistTabLayout.setViewPager(mFragToplistViewPager)
            mFragToplistTabLayout.setSelectedAnim()
        }
    }

    override fun initData() {

    }

    override fun startObserve() {

    }

}