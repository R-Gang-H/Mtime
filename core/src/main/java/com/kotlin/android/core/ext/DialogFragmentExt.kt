package com.kotlin.android.core.ext

import android.view.View
import androidx.fragment.app.DialogFragment
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentActivity
import com.kotlin.android.core.annotation.DialogFragmentTag
import com.kotlin.android.ktx.ext.core.getActivity

/**
 * 通用的 DialogFragment 启动方式
 *
 * Created on 2022/4/15.
 *
 * @author o.s
 */

/**
 * 通用的显示指定的 DialogFragment
 */
fun <T : DialogFragment> FragmentActivity.showDialogFragment(
    clazz: Class<T>,
    def: (() -> T)? = null,
    isCancelable: Boolean = true
): T? {
    return getOrGenerateDialogFragment(clazz = clazz, def = def)?.apply {
        setCancelable(isCancelable)
    }
}

/**
 * 通用的显示指定的 DialogFragment
 */
fun <T : DialogFragment> Fragment.showDialogFragment(
    clazz: Class<T>,
    def: (() -> T)? = null,
    isCancelable: Boolean = true
): T? {
    return activity?.getOrGenerateDialogFragment(clazz = clazz, def = def)?.apply {
        setCancelable(isCancelable)
    }
}

/**
 * 通用的显示指定的 DialogFragment
 */
fun <T : DialogFragment> View.showDialogFragment(
    clazz: Class<T>,
    def: (() -> T)? = null,
    isCancelable: Boolean = true
): T? {
    return (getActivity() as? FragmentActivity)?.getOrGenerateDialogFragment(clazz = clazz, def = def)?.apply {
        setCancelable(isCancelable)
    }
}

/**
 * 获取指定的 DialogFragment，如果不存在，则新建一个并显示出来（默认新建方式 newInstance，如需自定义需要明确指定 [def]）
 */
fun <T : DialogFragment> FragmentActivity.getOrGenerateDialogFragment(
    clazz: Class<T>,
    def: (() -> T)? = null
): T? {
    val tag = clazz.getAnnotation(DialogFragmentTag::class.java)?.tag
        ?: throw IllegalArgumentException("${clazz.simpleName} DialogFragmentTag is null")

    var fragment = supportFragmentManager.findFragmentByTag(tag) as? T
    if (fragment == null) {
        fragment = def?.invoke() ?: clazz.newInstance()
        fragment?.showNow(supportFragmentManager, tag)
    }
    return fragment
}