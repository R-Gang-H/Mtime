package com.kotlin.tablet.ui.mine

import android.content.Intent
import com.alibaba.android.arouter.facade.annotation.Route
import com.kotlin.android.app.router.path.RouterActivityPath
import com.kotlin.android.app.router.provider.tablet.ITabletProvider
import com.kotlin.android.core.BaseVMActivity
import com.kotlin.android.ktx.ext.immersive.immersive
import com.kotlin.android.router.ext.getProvider
import com.kotlin.android.widget.tablayout.FragPagerItemAdapter
import com.kotlin.android.widget.tablayout.FragPagerItems
import com.kotlin.android.widget.tablayout.setSelectedAnim
import com.kotlin.android.widget.titlebar.TitleBarManager
import com.kotlin.android.widget.titlebar.back
import com.kotlin.tablet.KEY_FILM_LIST_MINE_TAB
import com.kotlin.tablet.R
import com.kotlin.tablet.databinding.ActivityFilmListMineBinding

/**
 * 创建者: SunHao
 * 创建时间: 2022/3/28
 * 描述:个人中心-我的片单
 **/
@Route(path = RouterActivityPath.TABLET.FILM_LIST_OF_MINE)
class FilmListMineActivity : BaseVMActivity<FilmListMineViewModel, ActivityFilmListMineBinding>() {
    private var mTab = 1
    override fun getIntentData(intent: Intent?) {
        mTab = intent?.getIntExtra(KEY_FILM_LIST_MINE_TAB, 0) ?: 0
    }

    override fun initTheme() {
        immersive()
            .statusBarColor(getColor(R.color.color_ffffff))
            .statusBarDarkFont(true)
    }

    override fun initCommonTitleView() {
        TitleBarManager.with(this)
            .setTitle(titleRes = R.string.tablet_film_list_of_mine)
            .back { finish() }
            .addItem(
                isReversed = true,
                titleRes = R.string.tablet_film_list_create,
                colorRes = R.color.color_20a0da
            ) {
                getProvider(ITabletProvider::class.java)?.startFilmListCreateActivity(false)
            }
    }

    override fun initView() {
        mBinding?.apply {
            FragPagerItemAdapter(
                supportFragmentManager,
                FragPagerItems(this@FilmListMineActivity).apply {
                    add(
                        titleRes = R.string.tablet_film_list_create_by_mine,
                        clazz = FilmListCreateFragment::class.java
                    )
                    add(
                        titleRes = R.string.tablet_film_list_collection_by_mine,
                        clazz = FilmListCollectionFragment::class.java
                    )
                }).apply {
                mViewPager.adapter = this
            }
            mTabLayout.setViewPager(mViewPager)
            mTabLayout.setSelectedAnim()
            mViewPager.currentItem = mTab
        }
    }

    override fun initData() {
    }

    override fun startObserve() {
    }
}