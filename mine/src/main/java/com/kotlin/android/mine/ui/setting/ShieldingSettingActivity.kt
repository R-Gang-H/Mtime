package com.kotlin.android.mine.ui.setting

import com.alibaba.android.arouter.facade.annotation.Route
import com.kotlin.android.app.router.path.RouterActivityPath
import com.kotlin.android.app.router.provider.message_center.IMessageCenterProvider
import com.kotlin.android.core.BaseVMActivity
import com.kotlin.android.core.BaseViewModel
import com.kotlin.android.ktx.ext.immersive.immersive
import com.kotlin.android.mine.R
import com.kotlin.android.mine.databinding.ActivityShieldingSettingBinding
import com.kotlin.android.router.ext.getProvider
import com.kotlin.android.widget.titlebar.TitleBarManager

@Route(path = RouterActivityPath.Mine.PAGE_SHIELDING_SETTING_ACTIVITY)
class ShieldingSettingActivity : BaseVMActivity<BaseViewModel, ActivityShieldingSettingBinding>() {

    private val messageCenterProvider = getProvider(IMessageCenterProvider::class.java)

    override fun initCommonTitleView() {
        super.initCommonTitleView()
        TitleBarManager.with(this)
            .setTitle(titleRes = R.string.mine_shielding_setting)
            .allowShadow()
            .addItem(drawableRes = R.drawable.ic_title_bar_36_back) {
                onBackPressed()
            }
    }

    override fun initTheme() {
        super.initTheme()
        immersive()
            .statusBarColor(getColor(R.color.color_ffffff))
            .statusBarDarkFont(true)
    }

    override fun initView() {
        mBinding?.manageBlacklistsRl?.setOnClickListener {
            messageCenterProvider?.startBlackListActivity(this)
        }
    }

    override fun initData() {

    }

    override fun startObserve() {

    }
}