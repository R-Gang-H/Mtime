package com.kotlin.android.image.component

import android.view.View
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentActivity
import com.kotlin.android.app.data.entity.image.PhotoInfo
import com.kotlin.android.image.component.ui.PhotoCropFragment
import com.kotlin.android.ktx.ext.core.getActivity

/**
 * 图片裁剪扩展：
 *
 * Created on 2020/12/21.
 *
 * @author o.s
 */
const val TAG_FRAGMENT_PHOTO_CROP = "tag_fragment_photo_crop"
const val CROP_TYPE_1_1 = "h,1:1"
const val CROP_TYPE_16_9 = "h,16:9"
const val CROP_TYPE_3_4 = "h,3:4"

/**
 * 显示图片裁剪视图：
 *
 * [photo] 原始照片信息。
 * [imageType] 设置正确的上传图片类型 imageType 可以自动上传，并回调 [callback] 包含上传完成的图片网络地址 [PhotoInfo.url]；
 * 否则（如：imageType = -1L）直接回调 [callback] 包含保存的裁剪图片地址 [PhotoInfo.uploadPath]，可以自定义上传内容。
 * [avatar] 头像更换回调。
 */
fun FragmentActivity.showPhotoCropDialog(
        photo: PhotoInfo,
        imageType: Long,
        cropType:String = CROP_TYPE_1_1,
        avatar: ((avatarUrl: String) -> Unit)? = null,
        callback: ((photo: PhotoInfo) -> Unit)? = null
): PhotoCropFragment {
    return getOrGeneratePhotoCropDialog().apply {
        this.photo = photo
        this.cropType = cropType
        this.imageType = imageType
        this.avatar = avatar
        this.callback = callback
    }
}

/**
 * 显示图片裁剪视图：
 */
fun Fragment.showPhotoCropDialog(
        photo: PhotoInfo,
        imageType: Long,
        cropType:String = CROP_TYPE_1_1,
        avatar: ((avatarUrl: String) -> Unit)? = null,
        callback: (photo: PhotoInfo) -> Unit
): PhotoCropFragment? {
    return activity?.showPhotoCropDialog(photo, imageType,cropType, avatar, callback)
}

/**
 * 显示图片裁剪视图：
 */
fun View.showPhotoCropDialog(
        photo: PhotoInfo,
        imageType: Long,
        cropType:String = CROP_TYPE_1_1,
        avatar: ((avatarUrl: String) -> Unit)? = null,
        callback: (photo: PhotoInfo) -> Unit
): PhotoCropFragment? {
    return (getActivity() as? FragmentActivity)?.showPhotoCropDialog(photo, imageType,cropType, avatar, callback)
}

fun FragmentActivity.getOrGeneratePhotoCropDialog(): PhotoCropFragment {
    var fragment = getPhotoCropFragment()
    if (fragment == null) {
        fragment = PhotoCropFragment()
        fragment.showNow(supportFragmentManager, TAG_FRAGMENT_PHOTO_CROP)
    }
    return fragment
}

fun FragmentActivity.dismissPhotoCropFragment() {
    getPhotoCropFragment()?.dismissAllowingStateLoss()
}

fun Fragment.dismissPhotoCropFragment() {
    activity?.dismissPhotoCropFragment()
}

fun View.dismissPhotoCropFragment() {
    (getActivity() as? FragmentActivity)?.dismissPhotoCropFragment()
}

fun FragmentActivity.getPhotoCropFragment(): PhotoCropFragment? =
        supportFragmentManager.findFragmentByTag(TAG_FRAGMENT_PHOTO_CROP) as? PhotoCropFragment