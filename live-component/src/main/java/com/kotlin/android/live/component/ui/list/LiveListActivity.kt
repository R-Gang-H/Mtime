package com.kotlin.android.live.component.ui.list

import com.alibaba.android.arouter.facade.annotation.Route
import com.kotlin.android.app.router.path.RouterActivityPath
import com.kotlin.android.core.BaseVMActivity
import com.kotlin.android.ktx.ext.immersive.immersive
import com.kotlin.android.live.component.R
import com.kotlin.android.live.component.databinding.ActivityLiveListBinding
import com.kotlin.android.widget.titlebar.TitleBarManager
import com.kotlin.android.widget.titlebar.back

/**
 * Created by suq on 2022/4/18
 * des:
 */
@Route(path = RouterActivityPath.Live.PAGE_LIVE_LIST_ACTIVITY)
class LiveListActivity : BaseVMActivity<LiveListActViewModel, ActivityLiveListBinding>() {

    override fun initTheme() {
        super.initTheme()
        immersive()
            .statusBarColor(getColor(R.color.color_ffffff))
            .statusBarDarkFont(true)
    }

    override fun initCommonTitleView() {
        super.initCommonTitleView()
        TitleBarManager.with(this)
            .setTitle(
                titleRes = R.string.live_component_live_list_title,
            )
            .back {
                finish()
            }
    }

    override fun initView() {

    }

    override fun initData() {

    }

    override fun startObserve() {

    }
}