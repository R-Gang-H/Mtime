package com.kotlin.android.publish.component.ui.selectedvideo

import android.Manifest
import android.content.Intent
import androidx.fragment.app.FragmentActivity
import com.kotlin.android.ktx.ext.permission.permissions
import com.kotlin.android.publish.component.widget.selector.LocalMedia

/**
 * create by lushan on 2022/4/6
 * des:
 **/

const val TAG_FRAGMENT_VIDEO_SELECT = "tag_fragment_video_select"
const val KEY_LOCAL_VIDEO_PATH = "key_local_video_path"
const val REQUEST_VIDEO  = 1005

/**
 * 如果不传参数点击下一步跳转到视频发布页， 如果需要在当前页面获取视频，传toVideoPublish 和listener并在onActivityResult中调用onSelectVideoResult
 */
fun FragmentActivity.showVideoListDialog(toVideoPublish:Boolean = true,listener:((LocalMedia)->Unit)? = null){
    permissions(Manifest.permission.WRITE_EXTERNAL_STORAGE,Manifest.permission.READ_EXTERNAL_STORAGE){
        onDenied {
            dismissVideoListDialog()
        }
        onGranted {
            showVideoDialogAfterPermission(toVideoPublish,listener)
        }
        onNeverAskAgain {
            showVideoDialogAfterPermission(toVideoPublish,listener)
        }
    }
}

private fun FragmentActivity.showVideoDialogAfterPermission(toVideoPublish:Boolean = true,listener:((LocalMedia)->Unit)? = null){
    getOrGenerateSelectedVideoFragment().apply {
        this.toVideoPublish = toVideoPublish
        this.listener = listener
    }
}

fun FragmentActivity.dismissVideoListDialog() {
    getVideoSelectedFragment()?.run {
        if (isVisible){
            dismissAllowingStateLoss()
        }
    }
}

fun FragmentActivity.getOrGenerateSelectedVideoFragment():SelectedVideoFragment{
    var videoFragment = getVideoSelectedFragment()
    if (videoFragment == null){
        videoFragment = SelectedVideoFragment()
        videoFragment?.showNow(supportFragmentManager, TAG_FRAGMENT_VIDEO_SELECT)
    }
    return videoFragment

}
fun FragmentActivity.onSelectVideoResult(requestCode: Int, resultCode: Int, data: Intent?){
    supportFragmentManager?.fragments?.forEach {
        it?.onActivityResult(requestCode, resultCode, data)
    }
}

fun FragmentActivity.getVideoSelectedFragment():SelectedVideoFragment? = supportFragmentManager.findFragmentByTag(
    TAG_FRAGMENT_VIDEO_SELECT) as? SelectedVideoFragment