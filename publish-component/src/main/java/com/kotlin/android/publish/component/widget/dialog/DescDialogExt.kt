package com.kotlin.android.publish.component.widget.dialog

import android.view.View
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentActivity
import com.kotlin.android.ktx.ext.core.getActivity

/**
 * 描述信息输入对话框
 *
 * Created on 2020/10/21.
 *
 * @author o.s
 */

const val TAG_FRAGMENT_DESC_DIALOG = "tag_fragment_desc_dialog"

fun FragmentActivity.showDescDialog(
        data: CharSequence,
        maxLength: Int = 20,
        isCancelable: Boolean = false,
        dismiss: (() -> Unit)? = null,
        event: ((desc: CharSequence) -> Unit)? = null
): DescDialog {
    return getOrGenerateDescDialog().apply {
        setCancelable(isCancelable)
        this.desc = data
        this.event = event
        this.dismiss = dismiss
        this.maxLength = maxLength
    }
}

fun Fragment.showDescDialog(
        data: String,
        maxLength: Int = 20,
        isCancelable: Boolean = false,
        dismiss: (() -> Unit)? = null,
        event: ((desc: CharSequence) -> Unit)? = null
): DescDialog? {
    return activity?.showDescDialog(data, maxLength, isCancelable, dismiss, event)
}

fun View.showDescDialog(
        data: CharSequence,
        maxLength: Int = 20,
        isCancelable: Boolean = false,
        dismiss: (() -> Unit)? = null,
        event: ((desc: CharSequence) -> Unit)? = null
): DescDialog? {
    return (getActivity() as? FragmentActivity)?.showDescDialog(data, maxLength, isCancelable, dismiss, event)
}

fun FragmentActivity.dismissDescDialog() {
    getDescDialog()?.apply {
        dismissAllowingStateLoss()
    }
}

fun FragmentActivity.getOrGenerateDescDialog(): DescDialog {
    var fragment = getDescDialog()
    if (fragment == null) {
        fragment = DescDialog()
        fragment.showNow(supportFragmentManager, TAG_FRAGMENT_DESC_DIALOG)
    }
    return fragment
}

fun FragmentActivity.getDescDialog(): DescDialog? {
    return supportFragmentManager.findFragmentByTag(TAG_FRAGMENT_DESC_DIALOG) as? DescDialog
}