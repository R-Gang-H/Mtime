package com.kotlin.android.mine.ui.setting

import android.view.View
import com.alibaba.android.arouter.facade.annotation.Route
import com.kotlin.android.app.router.path.RouterActivityPath
import com.kotlin.android.app.router.provider.main.IMainProvider
import com.kotlin.android.app.router.provider.mine.IMineProvider
import com.kotlin.android.core.BaseVMActivity
import com.kotlin.android.core.BaseViewModel
import com.kotlin.android.ktx.ext.immersive.immersive
import com.kotlin.android.mine.R
import com.kotlin.android.mine.databinding.ActivitySettingBinding
import com.kotlin.android.router.ext.getProvider
import com.kotlin.android.widget.titlebar.TitleBarManager

@Route(path = RouterActivityPath.Mine.PAGE_SETTING_ACTIVITY)
class SettingActivity : BaseVMActivity<BaseViewModel, ActivitySettingBinding>() {

    private val mineProvider = getProvider(IMineProvider::class.java)
    private val mainProvider = getProvider(IMainProvider::class.java)

    override fun initCommonTitleView() {
        super.initCommonTitleView()
        TitleBarManager.with(this)
            .setTitle(titleRes = R.string.mine_setting)
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
        mBinding?.apply {
            personalDataRl.setOnClickListener {
                mineProvider?.startPersonalData(this@SettingActivity)
            }
            accountSettingRl.setOnClickListener {
                mineProvider?.startAccountSetting(this@SettingActivity)
            }
            pushSettingRl.setOnClickListener {
                mineProvider?.startPushSettingBind(this@SettingActivity)
            }
            shieldingSettingRl.setOnClickListener {
                mineProvider?.startShieldingSettingBind(this@SettingActivity)
            }
            cancelAccountRl.setOnClickListener {
                mainProvider?.callNumber(
                    this@SettingActivity,
                    "4006059500"
                )
            }
            exitTv.setOnClickListener {
                mainProvider?.showCommonAlertDlg(
                    this@SettingActivity,
                    getString(R.string.mine_logout_confirm),
                    finishListener = {
                        exitTv.visibility = View.GONE
                        finish()
                    }
                )
            }
        }
    }

    override fun initData() {

    }

    override fun startObserve() {

    }
}