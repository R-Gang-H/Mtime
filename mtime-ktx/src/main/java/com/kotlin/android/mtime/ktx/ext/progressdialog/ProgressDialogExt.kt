package com.kotlin.android.mtime.ktx.ext.progressdialog

import androidx.annotation.StringRes
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentActivity
import com.kotlin.android.mtime.ktx.R
import com.kotlin.android.mtime.ktx.ext.progressdialog.ProgressDialogFragment.Companion.TAG_PROGRESS_DIALOG_FRAGMENT

/**
 * 加载对话框扩展：
 * 为 FragmentActivity 和 Fragment 提供了加载对话框的简单调用功能。
 * 支持两种显示行为模式：
 * ProgressDialogFragment.Behavior.SINGLE 默认，单异步任务模式
 * ProgressDialogFragment.Behavior.MULTIPART 多异步任务模式
 *
 * Created on 2020/6/12.
 *
 * @author o.s
 */

/**
 * 显示加载对话框。
 * 两种行为模式：
 * [ProgressDialogFragment.Behavior.SINGLE] 默认，单异步任务模式
 * [ProgressDialogFragment.Behavior.MULTIPART] 多异步任务模式
 */
fun FragmentActivity.showProgressDialog(
    @StringRes resId: Int = R.string.loading,
    behavior: ProgressDialogFragment.Behavior = ProgressDialogFragment.Behavior.MULTIPART,
    isCancelable: Boolean = true
) {
    showProgressDialog(getString(resId), behavior, isCancelable)
}

/**
 * 显示/隐藏加载对话框。
 * 两种行为模式：
 * [ProgressDialogFragment.Behavior.SINGLE] 默认，单异步任务模式
 * [ProgressDialogFragment.Behavior.MULTIPART] 多异步任务模式
 */
fun FragmentActivity.showProgressDialog(
    isShow: Boolean = true,
    @StringRes resId: Int = R.string.loading,
    behavior: ProgressDialogFragment.Behavior = ProgressDialogFragment.Behavior.MULTIPART,
    isCancelable: Boolean = true
) {
    if (isShow) {
        showProgressDialog(getString(resId), behavior, isCancelable)
    } else {
        dismissProgressDialog()
    }
}

/**
 * 显示加载对话框。
 * 两种行为模式：
 * [ProgressDialogFragment.Behavior.SINGLE] 默认，单异步任务模式
 * [ProgressDialogFragment.Behavior.MULTIPART] 多异步任务模式
 */
fun FragmentActivity.showProgressDialog(
    tips: String?, // 此处不要默认值，在调用时会和上面的方法会冲突（默认参数）
    behavior: ProgressDialogFragment.Behavior = ProgressDialogFragment.Behavior.MULTIPART,
    isCancelable: Boolean = true
) {
    getOrShowNowProgressDialogFragment()?.run {
        setCancelable(isCancelable)
        setBehavior(behavior)
        setLoadingTips(tips)
    }
}

/**
 * 更新加载对话框文案提示语，可多次调用，动态更新加载对话框提示语，并且只有在加载对话框显示的情况下生效，可放心使用。
 */
fun FragmentActivity.updateLoadingTips(@StringRes resId: Int = R.string.loading) {
    updateLoadingTips(getString(resId))
}

/**
 * 更新加载对话框文案提示语，可多次调用，动态更新加载对话框提示语，并且只有在加载对话框显示的情况下生效，可放心使用。
 */
fun FragmentActivity.updateLoadingTips(tips: String) {
    getProgressDialogFragment()?.run {
        setLoadingTips(tips)
    }
}

/**
 * 关闭加载对话框
 */
fun FragmentActivity.dismissProgressDialog() {
    getProgressDialogFragment()?.run {
        dismissAllowingStateLoss()
    }
}

/**
 * 获取/创建加载对话框 [ProgressDialogFragment]
 */
private fun FragmentActivity.getOrShowNowProgressDialogFragment(): ProgressDialogFragment? {
    var fragment = getProgressDialogFragment()
    if (fragment == null || fragment.isHidden || !fragment.isShowing) {
        synchronized(ProgressDialogFragment::class.java) {
            fragment = getProgressDialogFragment()
            when {
                fragment?.isHidden == true -> {
                    fragment?.show()
                }
                fragment?.isAdded == true -> {
                    fragment?.show()
                }
                fragment == null || fragment?.isShowing == false -> {
                    fragment = ProgressDialogFragment.instance
                    try {
                        fragment?.showNow(supportFragmentManager, TAG_PROGRESS_DIALOG_FRAGMENT)
                    } catch (e: Exception) {
                        e.printStackTrace()
                    }
                }
                else -> {

                }
            }
        }
    }
    return fragment
}

/**
 * 获取/创建加载对话框 [ProgressDialogFragment]
 */
private fun Fragment.getOrShowNowProgressDialogFragment(): ProgressDialogFragment? {
    var fragment = getProgressDialogFragment()
    if (fragment == null || fragment.isHidden || !fragment.isShowing) {
        synchronized(ProgressDialogFragment::class.java) {
            fragment = getProgressDialogFragment()
            when {
                fragment?.isHidden == true -> {
                    fragment?.show()
                }
                fragment?.isAdded == true -> {
                    fragment?.show()
                }
                fragment == null || fragment?.isShowing == false -> {
                    fragment = ProgressDialogFragment.instance
                    try {
                        fragment?.showNow(childFragmentManager, TAG_PROGRESS_DIALOG_FRAGMENT)
                    } catch (e: Exception) {
                        e.printStackTrace()
                    }
                }
                else -> {

                }
            }
        }
    }
    return fragment
}

/**
 * 获取现有加载进度条对话框 [ProgressDialogFragment]，如果没有则返回 null
 */
private fun Fragment.getProgressDialogFragment(): ProgressDialogFragment? {
    return childFragmentManager.findFragmentByTag(TAG_PROGRESS_DIALOG_FRAGMENT) as? ProgressDialogFragment
}

/**
 * 获取现有加载进度条对话框 [ProgressDialogFragment]，如果没有则返回 null
 */
private fun FragmentActivity.getProgressDialogFragment(): ProgressDialogFragment? {
    return supportFragmentManager.findFragmentByTag(TAG_PROGRESS_DIALOG_FRAGMENT) as? ProgressDialogFragment
}

/**
 * 显示加载对话框。
 * 两种行为模式：
 * [ProgressDialogFragment.Behavior.SINGLE]
 * [ProgressDialogFragment.Behavior.MULTIPART]
 */
fun Fragment.showProgressDialog(
    isShow: Boolean = true,
    @StringRes resId: Int = R.string.loading,
    behavior: ProgressDialogFragment.Behavior = ProgressDialogFragment.Behavior.MULTIPART,
    isCancelable: Boolean = true
) {
    if (isShow) {
        showProgressDialog(getString(resId), behavior, isCancelable)
    } else {
        dismissProgressDialog()
    }
}

/**
 * 显示加载对话框。
 * 两种行为模式：
 * [ProgressDialogFragment.Behavior.SINGLE]
 * [ProgressDialogFragment.Behavior.MULTIPART]
 */
fun Fragment.showProgressDialog(
    @StringRes resId: Int = R.string.loading,
    behavior: ProgressDialogFragment.Behavior = ProgressDialogFragment.Behavior.MULTIPART,
    isCancelable: Boolean = true
) {
    showProgressDialog(getString(resId), behavior, isCancelable)
}

/**
 * 显示加载对话框。
 * 两种行为模式：
 * [ProgressDialogFragment.Behavior.SINGLE]
 * [ProgressDialogFragment.Behavior.MULTIPART]
 */
fun Fragment.showProgressDialog(
    tips: String,
    behavior: ProgressDialogFragment.Behavior = ProgressDialogFragment.Behavior.MULTIPART,
    isCancelable: Boolean = true
) {
    getOrShowNowProgressDialogFragment()?.run {
        setCancelable(isCancelable)
        setBehavior(behavior)
        setLoadingTips(tips)
    }
}

/**
 * 更新加载对话框文案提示语，可多次调用，动态更新加载对话框提示语，并且只有在加载对话框显示的情况下生效，可放心使用。
 */
fun Fragment.updateLoadingTips(@StringRes resId: Int = R.string.loading) {
    updateLoadingTips(getString(resId))
}

/**
 * 更新加载对话框文案提示语，可多次调用，动态更新加载对话框提示语，并且只有在加载对话框显示的情况下生效，可放心使用。
 */
fun Fragment.updateLoadingTips(tips: String) {
    getProgressDialogFragment()?.run {
        setLoadingTips(tips)
    }
}

/**
 * 关闭加载对话框
 */
fun Fragment.dismissProgressDialog() {
    getProgressDialogFragment()?.run {
        dismissAllowingStateLoss()
    }
}

fun FragmentActivity.showOrHideProgressDialog(showLoading:Boolean){
    if (showLoading) {
        showProgressDialog()
    } else {
        dismissProgressDialog()
    }
}

fun Fragment.showOrHideProgressDialog(showLoading:Boolean){
    if (showLoading) {
        showProgressDialog()
    } else {
        dismissProgressDialog()
    }
}