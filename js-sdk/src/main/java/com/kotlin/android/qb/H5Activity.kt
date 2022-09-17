package com.kotlin.android.qb

import android.content.Intent
import androidx.activity.viewModels
import com.alibaba.android.arouter.facade.annotation.Route
import com.kotlin.android.app.data.entity.js.sdk.BrowserEntity
import com.kotlin.android.js.sdk.R
import com.kotlin.android.ktx.ext.KEY_DATA
import com.kotlin.android.qb.base.BrowserViewModel
import com.kotlin.android.app.router.path.RouterActivityPath
import com.kotlin.android.core.BaseVMActivity
import com.kotlin.android.js.sdk.databinding.ActH5Binding
import com.kotlin.android.ktx.ext.immersive.immersive
import com.kotlin.android.ktx.ext.keyboard.hideSoftInput
import com.kotlin.android.qb.common.H5Fragment
import com.kotlin.android.widget.titlebar.CommonTitleBar

/**
 * 通用H5跳转页面，已经实现公共的js交互，定制方法由各子类自行实现。
 *
 * Created on 2020/11/3.
 *
 * @author zl
 */
@Route(path = RouterActivityPath.JsSDK.PAGE_H5_ACTIVITY)
class H5Activity : BaseVMActivity<BrowserViewModel, ActH5Binding>() {
    private var titleBar: CommonTitleBar? = null
    protected var mEntity: BrowserEntity? = null
    private var h5Fragment: H5Fragment? = null

    override fun initVM(): BrowserViewModel = viewModels<BrowserViewModel>().value

    override fun startObserve() {

    }

    override fun initTheme() {
        super.initTheme()
        immersive().statusBarColor(getColor(R.color.white)).statusBarDarkFont(true)
    }


    override fun getIntentData(intent: Intent?) {
        super.getIntentData(intent)
        mEntity = intent?.getSerializableExtra(KEY_DATA) as? BrowserEntity

    }

    override fun initCommonTitleView() {
        super.initCommonTitleView()
        titleBar = CommonTitleBar().init(this, isSystemSync = false).apply {
            setTitle(mEntity?.title ?: "")
            setLeftIconAndClick(R.drawable.icon_back) {
                onBackPressed()
            }
            setLeftSecondClickListener {
                finish()
            }
            create()
        }

    }

    override fun initView() {
        mEntity?.apply {
            h5Fragment = H5Fragment.newInstance(this).apply {
                supportFragmentManager.beginTransaction().add(R.id.container, this)
                    .commitAllowingStateLoss()
            }
        }

    }

    override fun initData() {
    }

    override fun finish() {
        hideSoftInput()
        super.finish()
    }

    override fun onBackPressed() {
        if (h5Fragment?.backward() == true) {
            return
        }
        super.onBackPressed()
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        supportFragmentManager?.fragments?.forEach {
            it?.onActivityResult(requestCode, resultCode, data)
        }
    }

}