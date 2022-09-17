package com.kotlin.android.mine.ui.setting

import cn.jpush.android.api.JPushInterface
import com.alibaba.android.arouter.facade.annotation.Route
import com.kotlin.android.app.router.path.RouterActivityPath
import com.kotlin.android.core.BaseVMActivity
import com.kotlin.android.ktx.ext.core.gotoPushSet
import com.kotlin.android.ktx.ext.immersive.immersive
import com.kotlin.android.mine.R
import com.kotlin.android.mine.databinding.ActivityPushSettingBinding
import com.kotlin.android.mine.ui.setting.viewmodel.PushSettingViewModel
import com.kotlin.android.mtime.ktx.GlobalDimensionExt
import com.kotlin.android.widget.titlebar.TitleBarManager

@Route(path = RouterActivityPath.Mine.PAGE_PUSH_SETTING_ACTIVITY)
class PushSettingActivity : BaseVMActivity<PushSettingViewModel, ActivityPushSettingBinding>() {

    private var isPush: Boolean = false
    private var isMessagePush: Boolean = false

    override fun initCommonTitleView() {
        super.initCommonTitleView()
        TitleBarManager.with(this)
            .setTitle(titleRes = R.string.mine_push_setting)
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
            notificationSwitchRl.setOnClickListener {
                gotoPushSet()
            }
        }
    }

    override fun initData() {
        getCommonPush()
    }

    override fun startObserve() {
        mViewModel?.pushState?.observe(this) {
            it?.apply {
                success?.run {
                    isPush = isIMPush
                    isMessagePush = isMessage
                    mBinding?.apply {
                        imTimeSwitch.isChecked = isMessage
                        messageSwitch.isChecked = isIMPush
                        imTimeSwitch.setOnCheckedChangeListener { _, isChecked ->
                            isMessagePush = isChecked
                            imTimeSwitch.isChecked = isChecked
                            setCommonPush()
                        }
                        messageSwitch.setOnCheckedChangeListener { _, isChecked ->
                            isPush = isChecked
                            messageSwitch.isChecked = isChecked
                            setCommonPush()
                        }
                    }
                }
                netError?.run {

                }
                error?.run {

                }
            }
        }

        mViewModel?.setPushState?.observe(this) {
            it?.apply {
                success?.run {
                    if (isMessagePush) {
                        JPushInterface.resumePush(applicationContext)
                    } else {
                        JPushInterface.stopPush(applicationContext)
                    }
                }
                netError?.run {

                }
                error?.run {

                }
            }
        }
    }

    private fun getCommonPush() {
        mViewModel?.getCommonPush()
    }

    private fun setCommonPush() {
        mViewModel?.setCommonPush(
            GlobalDimensionExt.getDigitsCurrentCityId(), isMessagePush,
            isPush
        )
    }
}