package com.kotlin.android.home.ui.toplist

import androidx.activity.viewModels
import com.alibaba.android.arouter.facade.annotation.Route
import com.kotlin.android.core.BaseVMActivity
import com.kotlin.android.core.BaseViewModel
import com.kotlin.android.home.R
import com.kotlin.android.home.databinding.ActToplistGameBinding
import com.kotlin.android.app.router.path.RouterActivityPath
import com.kotlin.android.ktx.ext.immersive.immersive
import com.kotlin.android.widget.titlebar.ThemeStyle
import com.kotlin.android.widget.titlebar.TitleBarManager
import com.kotlin.android.widget.titlebar.back

/**
 * @author vivian.wei
 * @date 2020/11/13
 * @desc  新增一个游戏榜单列表页Activity，与首页-榜单-游戏Tab共用fragment，给卡片大富翁右上角通用组件里面跳转使用
 */
@Route(path = RouterActivityPath.Home.PAGER_TOPLIST_GAME_ACTIVITY)
class TopListGameActivity: BaseVMActivity<BaseViewModel, ActToplistGameBinding>() {

    override fun initVM(): BaseViewModel = viewModels<BaseViewModel>().value

    override fun initTheme() {
        super.initTheme()
        immersive()
            .transparentStatusBar()
            .statusBarDarkFont(true)
    }

    override fun initCommonTitleView() {
        super.initCommonTitleView()
        TitleBarManager.with(activity = this, themeStyle = ThemeStyle.STANDARD_STATUS_BAR)
            .back {
                finish()
            }
            .setTitle(
                titleRes = R.string.home_game_top_list_title
            )
    }

    override fun initView() {
        val fragment = TopListGameFragment.newInstance()
        val fragmentTransaction = supportFragmentManager.beginTransaction()
        fragmentTransaction.replace(R.id.act_toplist_game_frame_layout, fragment)
        fragmentTransaction.commit()
    }

    override fun initData() {
    }

    override fun startObserve() {
    }

}