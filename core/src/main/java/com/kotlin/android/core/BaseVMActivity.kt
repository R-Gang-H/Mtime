package com.kotlin.android.core

import android.content.Intent
import android.os.Bundle
import androidx.viewbinding.ViewBinding
import com.kotlin.android.core.ext.initViewBinding
import com.kotlin.android.core.ext.initViewModel
import com.kotlin.android.ktx.ext.immersive.ImmersiveManager

/**
 * 基于 [BaseViewModel] 和 [ViewBinding] 管理的 Activity 基类
 *
 * Created on 2020/4/20.
 *
 * @author o.s
 */
abstract class BaseVMActivity<VM : BaseViewModel, VB : ViewBinding> : BaseActivity() {
    protected var mBinding: VB? = null
    protected var mViewModel: VM? = null

    override fun onNewIntent(intent: Intent?) {
        super.onNewIntent(intent)
        setIntent(intent)
        getIntentData(intent)
        initNewData()
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        savedInstanceState?.apply {
            initRecover(this)
        }
        mBinding = initVB()
        mViewModel = initVM()
        getIntentData(intent)
        initTheme()
        initCommonTitleView()
        startObserve()
        initView()
        initData()
    }

    override fun onDestroy() {
        super.onDestroy()
        // 销毁时注销
        ImmersiveManager.instance.remove(this)
    }

    open fun initRecover(savedInstanceState: Bundle) {}
    //如果想使用CommonTitleBar，请重写此方法
    open fun initCommonTitleView() {}
    open fun initTheme() {}
    open fun initVM(): VM? {
        return initViewModel()
    }
    open fun initVB(): VB? {
        return initViewBinding()
    }
    abstract fun initView()
    abstract fun initData()
    abstract fun startObserve()

    open fun getIntentData(intent: Intent?) {}
    open fun initNewData() {}
}