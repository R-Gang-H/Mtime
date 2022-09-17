package com.kotlin.android.publish.component.widget.dialog

import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentActivity
import com.kotlin.android.ktx.ext.core.getString
import com.kotlin.android.publish.component.R

/**
 * create by lushan on 2022/4/18
 * des:发布成功弹框
 **/

const val TAG_FRAGMENT_PUBLISH_SUCCESS_DIALOG = "tag_fragment_publish_success_dialog"

fun FragmentActivity.showPublishSuccessDialog(title:String = getString(R.string.publish_success), isCancelable: Boolean = false, clickListener:(()->Unit)? = null):PublishSuccessDialog{
    return getOrGeneratePublishSuccessDialog().apply {
        setCancelable(isCancelable)
        setTitle(title)
        initListener(clickListener)
    }
}

fun Fragment.showPublishSuccessDialog(title:String = getString(R.string.publish_success),isCancelable: Boolean = false,clickListener:(()->Unit)? = null):PublishSuccessDialog?{
    return activity?.showPublishSuccessDialog(title,isCancelable,clickListener)
}


fun FragmentActivity.dismissPublishSuccessDialog() {
    getPublishSuccessDialog()?.apply {
        dismissAllowingStateLoss()
    }
}

fun FragmentActivity.getOrGeneratePublishSuccessDialog(): PublishSuccessDialog {
    var fragment = getPublishSuccessDialog()
    if (fragment == null) {
        fragment = PublishSuccessDialog()
        fragment.showNow(supportFragmentManager, TAG_FRAGMENT_PUBLISH_SUCCESS_DIALOG)
    }
    return fragment
}

fun FragmentActivity.getPublishSuccessDialog(): PublishSuccessDialog? {
    return supportFragmentManager.findFragmentByTag(TAG_FRAGMENT_PUBLISH_SUCCESS_DIALOG) as? PublishSuccessDialog
}