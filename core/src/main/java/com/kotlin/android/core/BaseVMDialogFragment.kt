package com.kotlin.android.core

import android.app.Dialog
import android.content.Context
import android.content.DialogInterface
import android.os.Bundle
import android.view.*
import androidx.annotation.StyleRes
import androidx.fragment.app.DialogFragment
import androidx.fragment.app.FragmentActivity
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleObserver
import androidx.lifecycle.OnLifecycleEvent
import androidx.viewbinding.ViewBinding
import com.kotlin.android.core.ext.initViewBinding
import com.kotlin.android.core.ext.initViewModel
import com.kotlin.android.ktx.ext.immersive.Immersive
import com.kotlin.android.ktx.ext.immersive.ImmersiveManager

/**
 * DialogFragment 样式
 */
enum class DialogStyle(val height: Int, val gravity: Int) {
    /**
     * 充满屏幕，可配合 [Immersive] 支持沉浸式
     */
    FULL(height = WindowManager.LayoutParams.MATCH_PARENT, gravity = Gravity.CENTER),

    /**
     * 对话框顶部对齐
     */
    TOP(height = WindowManager.LayoutParams.WRAP_CONTENT, gravity = Gravity.TOP),

    /**
     * 对话框剧中
     */
    CENTER(height = WindowManager.LayoutParams.WRAP_CONTENT, gravity = Gravity.CENTER),

    /**
     * 对话框底部对齐
     */
    BOTTOM(height = WindowManager.LayoutParams.WRAP_CONTENT, gravity = Gravity.BOTTOM),
}

/**
 * 基于 [BaseViewModel] 和 [ViewBinding] 管理的 DialogFragment 基类
 *
 * Created on 2020/11/10.
 *
 * @author o.s
 */
abstract class BaseVMDialogFragment<VM : BaseViewModel, VB : ViewBinding> : DialogFragment() {
    private var _binding: VB? = null
    private var _viewModel: VM? = null
    private var _titleBar: ITitleBar? = null

    protected val mBinding: VB?
        get() = _binding

    protected val mViewModel: VM?
        get() = _viewModel


    var dismiss: (() -> Unit)? = null

    protected var theme: (() -> Unit)? = null
    protected var immersive: (() -> Unit)? = null
    protected var window: (Window.() -> Unit)? = null
    protected var dialogStyle: DialogStyle? = null // def DialogStyle.CENTER，兼容老版本，暂时不设置默认值。
    @StyleRes
    protected var animation: Int? = null // 动画

    val isShowing: Boolean
        get() = dialog?.isShowing ?: false

    override fun onAttach(context: Context) {
        super.onAttach(context)
        initEnv()
        addLifecycleObserverWithAttach(context)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        theme?.invoke()
    }

    open fun activityCreate() {
//        dialog?.window?.apply {
//            window?.invoke(this)
//        }
    }

//    override fun onStart() {
//        super.onStart()
//        dialog?.window?.apply {
//            window?.invoke(this)
//        }
//    }

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        val dialog = super.onCreateDialog(savedInstanceState)
        dialog.window?.apply {
            dialogStyle?.apply {
                decorView.setPadding(0, 0, 0, 0)
                setLayout(WindowManager.LayoutParams.MATCH_PARENT,  height)
                setGravity(gravity)
                setBackgroundDrawable(null)
            }
            animation?.apply {
                setWindowAnimations(this)
            }
            window?.invoke(this)
        }
        return dialog
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        _binding = initViewBinding(container, false)
        val titleBar = initTitleBar()
        _titleBar = titleBar as? ITitleBar
        return titleBar?.createViewOfFragment(_binding?.root) ?: _binding?.root
//        return _binding?.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        _viewModel = initVM()
        immersive?.invoke()
        initView()
        initData()
        startObserve()
        super.onViewCreated(view, savedInstanceState)
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    override fun onDestroy() {
        super.onDestroy()
        // 销毁时注销
        ImmersiveManager.instance.remove(this)
    }

    abstract fun initEnv()
    open fun initVM(): VM? {
        return initViewModel()
    }
    open fun initTitleBar(): ITitleBarOfFragment? {
        return null
    }
    abstract fun initView()
    abstract fun initData()
    abstract fun startObserve()

    open fun show() {
        dialog?.show()
    }

    open fun hide() {
        dialog?.hide()
    }

    override fun setCancelable(cancelable: Boolean) {
        super.setCancelable(cancelable)
        dialog?.setCanceledOnTouchOutside(cancelable)
    }

    override fun onDismiss(dialog: DialogInterface) {
        super.onDismiss(dialog)
        dismiss?.invoke()
    }

    /**
     * [onActivityCreated] 替代方案
     * 注意：子类需要实现 [activityCreate]
     */
    private fun addLifecycleObserverWithAttach(context: Context) {
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
}