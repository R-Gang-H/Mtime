package com.kotlin.android.core

import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.FragmentActivity
import androidx.fragment.app.FragmentPagerAdapter
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleObserver
import androidx.lifecycle.OnLifecycleEvent
import androidx.viewbinding.ViewBinding
import androidx.viewpager.widget.ViewPager
import com.kotlin.android.core.entity.PageFlag
import com.kotlin.android.core.ext.initViewBinding
import com.kotlin.android.core.ext.initViewModel
import com.kotlin.android.ktx.ext.log.d
import com.kotlin.android.ktx.ext.log.e
import com.kotlin.android.ktx.ext.immersive.ImmersiveManager
import com.kotlin.android.ktx.ext.core.removeFromParent

/**
 * 基于 [BaseViewModel] 和 [ViewBinding] 管理的 Fragment 基类
 *
 * Created on 2020/4/20.
 *
 * @author o.s
 */
abstract class BaseVMFragment<VM : BaseViewModel, VB : ViewBinding> : BaseFragment() {
    private var _binding: VB? = null
    private var _viewModel: VM? = null
    private var _titleBar: ITitleBar? = null
    private var _pageFlag: PageFlag? = null
    private var isNotifyRefresh = false // 是否通知fragment刷新
    private var refreshData: Any? = null // 刷新时数据，可以是任何类型数据（含null）
    private var isInitData = false // 是否初始化数据

    protected var isReady = false // fragment是否就绪

    protected val mBinding: VB?
        get() = _binding

    protected val mViewModel: VM?
        get() = _viewModel

    protected val mTitleBar: ITitleBar?
        get() = _titleBar

    protected val mPageFlag: PageFlag?
        get() = _pageFlag

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = initVB(container, false) // initViewBinding(container, false)
        val titleBar = initTitleBar()
        _titleBar = titleBar as? ITitleBar
        return titleBar?.createViewOfFragment(_binding?.root) ?: _binding?.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        _viewModel = initVM()
        initTheme()
        initView()
        startObserve()
        view.post {
            isReady = true // fragment已就绪
            mPageFlag?.apply {
                onPageFlag(this)
            }
            if (isNotifyRefresh) {
                onRefresh(refreshData)
            }
        }
        super.onViewCreated(view, savedInstanceState)
    }

    override fun onResume() {
        super.onResume()
        // 只有在当前Fragment显示可交互时，做数据初始化操作。
        if (!isInitData) {
            isInitData = true
            initData()
        }
    }

//    /**
//     * 通知 Fragment view图层已保存到 Activity
//     */
//    override fun onViewStateRestored(savedInstanceState: Bundle?) {
//        super.onViewStateRestored(savedInstanceState)
//        initTitleBar()
//    }

    override fun onDestroyView() {
        super.onDestroyView()
        destroyView()
        _binding = null
    }

    override fun onDestroy() {
        super.onDestroy()
        isInitData = false
        // 销毁时注销
        ImmersiveManager.instance.remove(this)
    }

    override fun onHiddenChanged(hidden: Boolean) {
        super.onHiddenChanged(hidden)
        if (hidden) {
            "hide ${this::class.java.simpleName}".d()
            hide()
        } else {
            "show ${this::class.java.simpleName}".d()
            initTheme()
            _titleBar?.syncStatusBar()
            show()
        }
    }

    /**
     * 外部调用页面标示。
     * 子类需要重写 [onPageFlag] 方法实现目标页面跳转。
     */
    fun setPageFlag(flag: PageFlag) {
        _pageFlag = flag
        "${javaClass.simpleName} setPageFlag, isReady=$isReady, flag=$flag".e()
        if (isReady) {
            onPageFlag(flag)
        }
    }

    /**
     * 子类重写，根据 [mPageFlag] 完成目标页面跳转。
     * [onPageFlag] 回调受 [setPageFlag] 控制：
     * 即：外部每次调用 [setPageFlag] 都会在恰当的时机触发一次 [onPageFlag] 的回调
     */
    open fun onPageFlag(flag: PageFlag) {
        "${javaClass.simpleName} onPageFlag flag=$flag".e()
    }

    /**
     * 外部调用通知fragment在恰当的时机刷新。可以指定任何参数 [any] 将回调给 [onRefresh]。
     * 子类需要重写 [onRefresh] 方法实现刷新逻辑。
     */
    fun notifyRefresh(any: Any? = null) {
        isNotifyRefresh = true
        this.refreshData = any
        if (isReady) {
            onRefresh(any)
        }
    }

    /**
     * 子类重写，刷新Fragment，可以指定任何参数 [any]，默认实现调用 [show] 方法。
     * [onRefresh] 回调受 [notifyRefresh] 控制：
     * 即：外部每次调用 [notifyRefresh] 都会在恰当的时机触发一次 [onRefresh] 的回调
     */
    open fun onRefresh(any: Any? = null) {
        show()
    }

    /**
     * Fragment隐藏，在切换Fragment时才会触发 [hide] [show]
     */
    open fun hide() {}

    /**
     * Fragment显示，在切换Fragment时才会触发 [show] [hide]
     * 注意：Fragment初始化生命周期过程中不会调用 [show]
     */
    open fun show() {}

    open fun initVM(): VM? {
        return initViewModel()
    }
    open fun initVB(
        container: ViewGroup? = null,
        attachToRoot: Boolean = false
    ): VB? {
        return initViewBinding(container, false)
    }
    open fun initTheme() {}
    open fun initTitleBar(): ITitleBarOfFragment? {
        return null
    }
    abstract fun initView()
    abstract fun startObserve()
    abstract fun initData()
    open fun activityCreate() {}
    open fun destroyView() {}

    /**
     * [onActivityCreated] 替代方案
     * 注意：子类需要实现 [activityCreate] 时，请在 [onAttach] 中调用此方法，基类（本类）中不做统一处理。
     */
    protected fun addLifecycleObserverWithAttach(context: Context) {
        (context as? FragmentActivity)?.apply {
            lifecycle.addObserver(
                object : LifecycleObserver {

                    @OnLifecycleEvent(Lifecycle.Event.ON_CREATE)
                    fun onCreate() {
                        if(lifecycle.currentState.isAtLeast(Lifecycle.State.CREATED)) {
                            lifecycle.removeObserver(this)
                            activityCreate()
                        }
                    }
                }
            )
        }
    }

    /**
     * 获取当前子Fragment
     */
    fun getSubCurrentFragment(fragmentAdapter: FragmentPagerAdapter, viewPager: ViewPager?): BaseVMFragment<*, *>? {
        return if (fragmentAdapter.count > 0) {
            fragmentAdapter.getItem(viewPager?.currentItem ?: 0) as? BaseVMFragment<*, *>
        } else {
            null
        }
    }
}