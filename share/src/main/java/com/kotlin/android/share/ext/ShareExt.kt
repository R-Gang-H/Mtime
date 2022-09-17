package com.kotlin.android.share.ext

import android.app.Activity
import android.view.View
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentActivity
import com.kotlin.android.ktx.ext.core.getActivity
import com.kotlin.android.share.SharePlatform
import com.kotlin.android.share.entity.ShareEntity
import com.kotlin.android.share.ui.ShareFragment

/**
 *
 * Created on 2020/6/29.
 *
 * @author o.s
 */

/**
 * 分享 [ShareFragment] 的 tag 在 Activity 基类中会使用该 tag，
 * 来处理类似QQ分享回调的问题：[FragmentActivity.onActivityResult] 分发给 [ShareFragment]
 */
const val TAG_FRAGMENT_SHARE = "tag_fragment_share"

/**
 * 分享回调处理
 */
fun FragmentActivity.onActivityResultShareData() {

}

/**
 * 显示分享Dialog，并设置分享内容
 * [shareEntity]: 分享内容实体
 * [launchMode]: 启动模式
 * [isCancelable]: 是否可取消
 * [event]: 点击事件回调
 */
fun Fragment.showShareDialog(
        shareEntity: ShareEntity,
        launchMode: ShareFragment.LaunchMode = ShareFragment.LaunchMode.STANDARD,
        isCancelable: Boolean = true,
        event: ((platform: SharePlatform) -> Unit)? = null
) : ShareFragment? {
    return activity?.showShareDialog(
            shareEntity = shareEntity,
            launchMode = launchMode,
            isCancelable = isCancelable,
            event = event
    )
}

fun Fragment.dismissShareDialog() {
    activity?.dismissShareDialog()
}

/**
 * 显示分享Dialog，并设置分享内容
 * [shareEntity]: 分享内容实体
 * [launchMode]: 启动模式
 * [isCancelable]: 是否可取消
 * [event]: 点击事件回调
 */
fun FragmentActivity.showShareDialog(
        shareEntity: ShareEntity,
        launchMode: ShareFragment.LaunchMode = ShareFragment.LaunchMode.STANDARD,
        isCancelable: Boolean = true,
        event: ((platform: SharePlatform) -> Unit)? = null
) : ShareFragment {
    return getOrGenerateShareFragment().apply {
        setCancelable(isCancelable)
        setLaunchMode(launchMode)
        setData(shareEntity)
        setEvent(event)
    }
}


fun View.showShareDialog(
        shareEntity: ShareEntity,
        launchMode: ShareFragment.LaunchMode = ShareFragment.LaunchMode.STANDARD,
        isCancelable: Boolean = true,
        event: ((platform: SharePlatform) -> Unit)? = null
): ShareFragment? {
    val a = getActivity() as? FragmentActivity
    return a?.showShareDialog(shareEntity, launchMode, isCancelable, event)
}

/**
 * 显示分享Dialog，并设置分享内容
 * [shareEntity]: 分享内容实体
 * [launchMode]: 启动模式
 * [isCancelable]: 是否可取消
 * [event]: 点击事件回调
 */
fun FragmentActivity.showShareDialog(
        shareEntity: ShareEntity?,
        launchMode: ShareFragment.LaunchMode = ShareFragment.LaunchMode.STANDARD,
        vararg moreActionType:SharePlatform,
        isCancelable: Boolean = true,
        event: ((platform: SharePlatform) -> Unit)? = null
) : ShareFragment {
    return getOrGenerateShareFragment().apply {
        setCancelable(isCancelable)
        setLaunchMode(launchMode,*moreActionType)
        setData(shareEntity)
        setEvent(event)
    }
}

/**
 * 关闭分享Dialog
 */
fun FragmentActivity.dismissShareDialog() {
    getShareFragment()?.run {
        if (isVisible) {
            dismissAllowingStateLoss()
        }
    }
}

/**
 * 获取或生成 [ShareFragment] 分享组件。基于 [FragmentActivity] 方便操作
 */
fun FragmentActivity.getOrGenerateShareFragment(): ShareFragment {
    var fragment = getShareFragment()
    if (fragment == null) {
        fragment = ShareFragment()
        fragment.showNow(supportFragmentManager, TAG_FRAGMENT_SHARE)
    }
    return fragment
}

/**
 * 获取 [ShareFragment] 分享组件。基于 [FragmentActivity] 方便操作
 */
fun FragmentActivity.getShareFragment(): ShareFragment?
        = supportFragmentManager.findFragmentByTag(TAG_FRAGMENT_SHARE) as? ShareFragment
