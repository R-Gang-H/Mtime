package com.kotlin.android.image.component

import androidx.fragment.app.FragmentActivity
import com.kotlin.android.app.data.annotation.UploadImageType
import com.kotlin.android.app.data.constant.CommConstant
import com.kotlin.android.app.data.entity.image.PhotoInfo
import com.kotlin.android.image.component.ui.PhotoAlbumFragment

/**
 *
 * Created on 2020/7/23.
 *
 * @author o.s
 */

const val TAG_FRAGMENT_PHOTO_ALBUM = "tag_fragment_photo_album"

//fun FragmentActivity.showPhotoAlbumFragment(@IdRes containerViewId: Int): PhotoAlbumFragment {
//    var fragment = getPhotoAlbumFragment()
//    if (fragment == null) {
//        fragment = PhotoAlbumFragment()
//        supportFragmentManager
//                .beginTransaction()
//                .add(containerViewId, fragment, TAG_FRAGMENT_PHOTO_ALBUM)
//                .commit()
//    }
//    return fragment
//}

/**
 * @param   isUploadImageInComponent    是否在PhotoAlbumFragment中上传图片 true点击确认后直接上传图片，false在具体的业务中上传图片
 * @param   imageFileType       上传图片类型
 * @param   limitedCount        最多可以上传几张照片 默认最多上传10张照片
 */
fun FragmentActivity.showPhotoAlbumFragment(
        isUploadImageInComponent:Boolean = false,
        @UploadImageType imageFileType:Long = CommConstant.IMAGE_UPLOAD_COMMON,
        limitedCount:Long = PhotoAlbumFragment.MAX_LIMITED
): PhotoAlbumFragment {
    var fragment = getPhotoAlbumFragment()
    if (fragment == null) {
        fragment = PhotoAlbumFragment(isUploadImageInComponent,imageFileType,limitedCount)
        fragment.showNow(supportFragmentManager, TAG_FRAGMENT_PHOTO_ALBUM)
    }
    return fragment
}

fun FragmentActivity.getPhotoAlbumFragment(): PhotoAlbumFragment? =
        supportFragmentManager.findFragmentByTag(TAG_FRAGMENT_PHOTO_ALBUM) as? PhotoAlbumFragment

fun FragmentActivity.takePhotos(limit: Int = 20, completed: (ArrayList<PhotoInfo>) -> Unit) {
    showPhotoAlbumFragment(
        isUploadImageInComponent = true,
        limitedCount = limit.toLong()
    ).apply {
        actionSelectPhotos = { photos ->
            completed.invoke(photos)
        }
    }
}