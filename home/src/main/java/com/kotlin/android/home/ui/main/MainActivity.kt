package com.kotlin.android.home.ui.main

import androidx.activity.viewModels
import com.alibaba.android.arouter.facade.annotation.Route
import com.kotlin.android.core.BaseVMActivity
import com.kotlin.android.home.R
import com.kotlin.android.home.databinding.ActHomeMainBinding
import com.kotlin.android.home.ui.home.HomeFragment

import com.kotlin.android.app.router.path.RouterActivityPath
import com.kotlin.android.app.router.path.RouterProviderPath
import com.kotlin.android.app.router.provider.home.IHomeProvider
import com.kotlin.android.ktx.ext.immersive.immersive
import com.kotlin.android.router.ext.getProvider

/**
 *
 * Created on 2020/6/15.
 *
 * @author o.s
 */
@Route(path = RouterActivityPath.Home.PAGER_HOME_MAIN_ACTIVITY)
class MainActivity : BaseVMActivity<MainViewModel, ActHomeMainBinding>() {

    override fun initVM(): MainViewModel = viewModels<MainViewModel>().value

    override fun initTheme() {
        super.initTheme()
        immersive()
            .transparentStatusBar(isFitsSystemWindows = false)
    }

    override fun initView() {
//        mBinding?.setVariable(BR.vm, mViewModel)
//        mBinding?.setVariable(BR.provider, mProvider)

        val beginTransaction = supportFragmentManager.beginTransaction()
        beginTransaction.add(R.id.home_fragment_tag_id, HomeFragment.newInstance()).commit()
    }

    override fun initData() {

    }

    override fun startObserve() {
    }
}