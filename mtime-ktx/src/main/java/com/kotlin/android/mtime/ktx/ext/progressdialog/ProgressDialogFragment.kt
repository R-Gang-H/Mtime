package com.kotlin.android.mtime.ktx.ext.progressdialog

import android.content.DialogInterface
import android.os.Bundle
import android.view.*
import android.widget.ImageView
import android.widget.TextView
import androidx.annotation.StringRes
import androidx.fragment.app.DialogFragment
import androidx.fragment.app.FragmentActivity
import com.kotlin.android.image.coil.ext.loadImage
import com.kotlin.android.ktx.ext.log.d
import com.kotlin.android.ktx.ext.log.v
import com.kotlin.android.mtime.ktx.R
import org.jetbrains.anko.find


/**
 * 创建者: zl
 * 创建时间: 2020/6/11 8:39 PM
 * 描述: loading框
 * ProgressDialogFragment的构造函数必须是public的，避免现场保护数据恢复的时候创建fragment找不到构造函数
 * 如果定义成private的构造函数会报以下错误 androidx.fragment.app.Fragment$InstantiationException:
 * Unable to instantiate fragment com.kotlin.android.mtime.ktx.ext.progressdialog.ProgressDialogFragment: could not find Fragment constructor
 */
class ProgressDialogFragment: DialogFragment() {
    @StringRes
    private var tipsResId: Int = R.string.loading // 设置了默认值
    private var behavior: Behavior = Behavior.MULTIPART
    private var counter = 0 // 计数器

    val isShowing: Boolean
        get() = dialog?.isShowing ?: false

    fun show() {
        dialog?.show()
    }

    fun hide() {
        dialog?.hide()
    }

    companion object {
        val instance by lazy { ProgressDialogFragment() }
        const val TAG_PROGRESS_DIALOG_FRAGMENT = "tag_progress_dialog_fragment"
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setStyle(STYLE_NORMAL, R.style.progressBarStyle)
    }

    override fun onCreateView(
            inflater: LayoutInflater, container: ViewGroup?,
            savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.frag_progress_dialog, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val progressBarIv = getView()?.find<ImageView>(R.id.progressBarIv)
        progressBarIv?.loadImage(data = R.drawable.loading_progress)
        notifyUI(getString(tipsResId))
    }

    /**
     * 设置/更新加载对话框文案提示语，可多次调用，动态更新加载对话框提示语，并且只有在加载对话框显示的情况下生效，可放心使用。
     */
    fun setLoadingTips(@StringRes resId: Int, onTouchOutside: Boolean = false) {
        tipsResId = resId
        setCanceledOnTouchOutside(onTouchOutside)
        notifyUI(getString(tipsResId))
    }

    /**
     * 设置/更新加载对话框文案提示语，可多次调用，动态更新加载对话框提示语，并且只有在加载对话框显示的情况下生效，可放心使用。
     */
    fun setLoadingTips(tips: String?, onTouchOutside: Boolean = false) {
        setCanceledOnTouchOutside(onTouchOutside)
        notifyUI(tips ?: getString(tipsResId))
    }

    private fun setCanceledOnTouchOutside(onTouchOutside: Boolean) {
        dialog?.setCanceledOnTouchOutside(onTouchOutside)
    }

    /**
     * 通知UI更新
     */
    private fun notifyUI(tips: String) {
        view?.find<TextView>(R.id.messageTv)?.text = tips
    }

    /**
     * 在 [Behavior.MULTIPART] 行为模式下，需要进行计数器累加
     */
    fun setBehavior(behavior: Behavior) {
        this.behavior = behavior
        if (Behavior.MULTIPART == behavior) {
            counter++
        }
        "setBehavior :: counter = $counter".v()
    }

    /**
     * 需要关闭对话框？
     */
    private fun needDismiss(): Boolean {
        return when (behavior) {
            Behavior.SINGLE -> {
                true
            }
            Behavior.MULTIPART -> {
                counter--
                "ProgressDialogFragment needDismiss :: counter-- = $counter".v()
                counter < 1
            }
        }
    }

    /**
     * 建议调用该方法关闭加载对话框
     */
    override fun dismiss() {
        "dismiss :: counter = $counter".v()
        if (!needDismiss()) {
            return
        }
        "ProgressDialogFragment dismiss :: counter = $counter".d()
        super.dismiss()
    }

    override fun dismissAllowingStateLoss() {
        "dismissAllowingStateLoss :: counter = $counter".v()
        if (!needDismiss()) {
            return
        }
        "ProgressDialogFragment dismissAllowingStateLoss :: counter = $counter".d()
        super.dismissAllowingStateLoss()
    }

    override fun onDismiss(dialog: DialogInterface) {
        "ProgressDialogFragment onDismiss :: counter = $counter".d()
        super.onDismiss(dialog)
        resetCounter()
    }

    /**
     * 监听到加载对话框消失时，重置计数器
     */
    private fun resetCounter() {
        counter = 0
    }

    /**
     * [ProgressDialogFragment] 的行为模式
     */
    enum class Behavior {

        /**
         * 独立模式：【建议单任务】当多次调用 [FragmentActivity.showProgressDialog] 显示加载对话框时，
         * 如果加载对话框已经处于显示状态，则不会创建新的加载对话框，只是更新加载提示语。
         *
         * 注意：存在被其他异步任务提前关闭加载对话框的风险（即：多个异步任务同时显示对话框时，先返回的将终止加载对话框）
         */
        SINGLE,

        /**
         * 多重模式：【建议多任务】当多次调用 [FragmentActivity.showProgressDialog] 显示加载对话框时，
         * 如果加载对话框已经处于显示状态，则不会创建新的加载对话框，内部将进行显示计数累加，并更新加载提示语。
         *
         * 注意：多个异步任务同时显示加载对话框时，加载对话框始终只显示一次，直到所有的异步任务都响应时才会关闭加载对话框。
         */
        MULTIPART,
    }
}