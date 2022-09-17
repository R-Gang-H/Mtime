package com.kotlin.android.splash.ui

import android.os.Handler
import android.os.Looper
import android.os.Message
import android.text.Html
import android.view.View
import androidx.activity.viewModels
import androidx.lifecycle.observe
import com.alibaba.android.arouter.facade.annotation.Route
import com.kotlin.android.core.BaseVMActivity
import com.kotlin.android.app.data.ext.VariateExt
import com.kotlin.android.image.coil.ext.loadImage
import com.kotlin.android.ktx.ext.click.onClick
import com.kotlin.android.ktx.ext.core.setBackground
import com.kotlin.android.ktx.ext.dimension.dpF
import com.kotlin.android.ktx.ext.store.getSpValue
import com.kotlin.android.ktx.ext.store.putSpValue
import com.kotlin.android.ktx.ext.statusbar.handleArticleStatusBar
import com.kotlin.android.mtime.ktx.ext.progressdialog.dismissProgressDialog
import com.kotlin.android.mtime.ktx.ext.progressdialog.showProgressDialog
import com.kotlin.android.router.ext.getProvider
import com.kotlin.android.app.router.path.RouterActivityPath
import com.kotlin.android.app.router.path.RouterProviderPath
import com.kotlin.android.app.router.provider.main.IMainProvider
import com.kotlin.android.core.entity.PageFlag
import com.kotlin.android.splash.R
import com.kotlin.android.splash.databinding.ActSplashAdBinding
import kotlinx.android.synthetic.main.act_splash_ad.*

/**
 * @author ZhouSuQiang
 * @email zhousuqiang@126.com
 * @date 2020/11/23
 */
@Route(path = RouterActivityPath.Splash.PAGER_SPLASH_ACTIVITY)
class SplashActivity : BaseVMActivity<SplashViewModel, ActSplashAdBinding>() {

    override fun initVM(): SplashViewModel = viewModels<SplashViewModel>().value

    /**
     * 是否显示引导页
     */
    private val MORE_THAN_ONCE = "more_than_once_v5"
    private val MSG_WHAT = 1
    private var mCountDown: Int = 3
    private var mAppLink: String = ""
    private val mHandler = object : Handler(Looper.getMainLooper()) {

        override fun handleMessage(msg: Message) {
            if (msg.what == MSG_WHAT) {
                splashAdJumpTv.text =
                    Html.fromHtml(getString(R.string.splash_jump_ad_hint, mCountDown))
                if (mCountDown <= 0) {
                    // 广告计时结束，直接跳转到主页面
                    gotoNextPage()
                } else {
                    --mCountDown
                    sendEmptyMessageDelayed(MSG_WHAT, 1000)
                }
            }
        }
    }

    override fun initView() {
        this.handleArticleStatusBar(false)
        splashAdImgIv.onClick {
            gotoNextPage(true)
        }
        splashAdJumpTv.onClick {
            mHandler.removeMessages(MSG_WHAT)
            gotoNextPage()
        }
        splashAdJumpTv.visibility = View.GONE
        splashAdJumpTv.setBackground(
            colorRes = R.color.color_60000000,
            cornerRadius = 6.dpF
        )
    }

    override fun initData() {
        mViewModel?.loadSplashAd()
    }

    override fun startObserve() {
        mViewModel?.uiState?.observe(this) {
            it.apply {
                if (showLoading) {
                    showProgressDialog()
                } else {
                    dismissProgressDialog()
                }

                success?.apply {
                    splashAdImgIv.loadImage(
                        data = img,
                        useProxy = false
                    )

                    mCountDown = countDown
                    mAppLink = appLink
                    splashAdJumpTv.visibility = View.VISIBLE
                    mHandler.sendEmptyMessage(MSG_WHAT)

                    return@observe
                }

                // 无广告数据，直接跳转到主页面
                gotoNextPage()
            }
        }
    }

    fun gotoNextPage(addAppLink: Boolean = false) {
        if (!getSpValue(MORE_THAN_ONCE, false)) {
            putSpValue(MORE_THAN_ONCE, true)
            // 打开引导页面
            getProvider(IMainProvider::class.java)?.startGuideViewActivity(application)
        } else {
            // 打开主页面
            getProvider(IMainProvider::class.java)?.startMainActivity(
                flag = PageFlag(
                    position = VariateExt.initMainTabIndex.toInt()
                ),
                applink = if (addAppLink) mAppLink else ""
            )
        }
        finish()
    }

    override fun onDestroy() {
        super.onDestroy()
        mHandler.removeMessages(MSG_WHAT)
    }
}