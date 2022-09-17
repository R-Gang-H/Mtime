package com.kotlin.android.card.monopoly.ext

import androidx.fragment.app.FragmentActivity
import com.kotlin.android.card.monopoly.ui.menu.FunctionMenuFragment
import com.kotlin.android.card.monopoly.widget.menu.FunctionMenuView

/**
 * 顶部功能菜单栏 Dialog 扩展
 *
 * Created on 2020/9/8.
 *
 * @author o.s
 */


const val TAG_FRAGMENT_FUNCTION_MENU = "tag_fragment_function_menu"

fun FragmentActivity.showFunctionMenuDialog(
        isCancelable: Boolean = true,
        dismiss: (() -> Unit)? = null,
        event: ((item: FunctionMenuView.Item) -> Unit)? = null
) {
    getOrGenerateFunctionMenuDialog().run {
        setCancelable(isCancelable)
        this.event = event
        this.dismiss = dismiss
    }
}

fun FragmentActivity.dismissFunctionMenuDialog() {
    getFunctionMenuDialog()?.apply {
        dismissAllowingStateLoss()
    }
}

fun FragmentActivity.getOrGenerateFunctionMenuDialog(): FunctionMenuFragment {
    var fragment = getFunctionMenuDialog()
    if (fragment == null) {
        fragment = FunctionMenuFragment()
        fragment.showNow(supportFragmentManager, TAG_FRAGMENT_FUNCTION_MENU)
    }
    return fragment
}

fun FragmentActivity.getFunctionMenuDialog(): FunctionMenuFragment? {
    return supportFragmentManager.findFragmentByTag(TAG_FRAGMENT_FUNCTION_MENU) as? FunctionMenuFragment
}