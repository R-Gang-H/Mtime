package com.kotlin.android.mine.ui.setting

import com.alibaba.android.arouter.facade.annotation.Route
import com.kotlin.android.app.router.path.RouterActivityPath
import com.kotlin.android.app.router.provider.main.IMainProvider
import com.kotlin.android.app.router.provider.mine.IMineProvider
import com.kotlin.android.core.BaseVMActivity
import com.kotlin.android.core.BaseViewModel
import com.kotlin.android.ktx.ext.immersive.immersive
import com.kotlin.android.mine.R
import com.kotlin.android.mine.databinding.ActivityAccountSettingBinding
import com.kotlin.android.router.ext.getProvider
import com.kotlin.android.user.UserManager.Companion.instance
import com.kotlin.android.widget.titlebar.TitleBarManager

@Route(path = RouterActivityPath.Mine.PAGE_ACCOUNT_SETTING_ACTIVITY)
class AccountSettingActivity : BaseVMActivity<BaseViewModel, ActivityAccountSettingBinding>() {

    private val mineProvider = getProvider(IMineProvider::class.java)
    private val mainProvider = getProvider(IMainProvider::class.java)

    override fun initCommonTitleView() {
        super.initCommonTitleView()
        TitleBarManager.with(this)
            .setTitle(titleRes = R.string.mine_account_setting)
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
        val mobile = instance.bindMobile
        mBinding?.apply {
            phoneNumTv.text = if (mobile != null && mobile.startsWith("1")) "${
                mobile.substring(
                    0,
                    3
                )
            }****${mobile.substring(7)}" else getString(R.string.mine_unbind)
            changeMobileRl.setOnClickListener {
                mainProvider?.startChangePhoneNum(this@AccountSettingActivity)
            }
            modifyPwdRl.setOnClickListener {
                mainProvider?.modifyPwd(this@AccountSettingActivity)
            }
            thirdAccountBindRl.setOnClickListener {
                mineProvider?.startThirdAccountBind(this@AccountSettingActivity)
            }
        }
    }

    override fun onResume() {
        super.onResume()
        val mobile = instance.bindMobile
        mBinding?.apply {
            phoneNumTv.text = if (mobile != null && mobile.startsWith("1")) "${
                mobile.substring(
                    0,
                    3
                )
            }****${mobile.substring(7)}" else getString(R.string.mine_unbind)
        }
    }

    override fun initData() {

    }

    override fun startObserve() {

    }
}