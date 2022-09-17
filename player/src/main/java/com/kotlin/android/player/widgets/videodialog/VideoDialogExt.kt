package com.kotlin.android.player.widgets.videodialog

import androidx.fragment.app.FragmentActivity

/**
 * create by lushan on 2022/3/21
 * des:
 **/

const val TAG_FRAGMENT_VIDEO = "tab_fragment_video"

fun FragmentActivity.showVideoDialog(
) : VideoDialogFragment {
    return getOrGenerateVideoDialogFragment().apply {
        updateVideo()
    }
}

fun FragmentActivity.dismissVideoDialog() {
    getVideoDialogFragment()?.run {
        if (isVisible) {
            dismissAllowingStateLoss()
        }
    }
}


fun FragmentActivity.getOrGenerateVideoDialogFragment(): VideoDialogFragment {
    var fragment = getVideoDialogFragment()
    if (fragment == null) {
        fragment = VideoDialogFragment()
        fragment.showNow(supportFragmentManager, TAG_FRAGMENT_VIDEO)
    }
    return fragment
}

fun FragmentActivity.getVideoDialogFragment(): VideoDialogFragment?
        = supportFragmentManager.findFragmentByTag(TAG_FRAGMENT_VIDEO) as? VideoDialogFragment

