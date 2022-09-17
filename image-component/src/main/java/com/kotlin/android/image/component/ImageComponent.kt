package com.kotlin.android.image.component

import com.kotlin.android.app.data.entity.image.PhotoInfo
import com.kotlin.android.image.component.photo.PhotoData
import com.kotlin.android.image.component.scope.PhotoUploadScope
import com.kotlin.android.mtime.ktx.ext.showToast
import com.kotlin.android.mtime.ktx.getString

/**
 * 图片组件：
 *
 * Created on 2020/7/2.
 *
 * @author o.s
 */

const val PHOTO_RECENT = "最近项目"
const val PHOTO_CAMERA = "Camera"

/**
 * 上传照片
 */
internal fun uploadPhotos(
    photos: ArrayList<PhotoInfo>,
    progressBar: ((isShow: Boolean) -> Unit)? = null,
    error: (ArrayList<PhotoInfo>) -> Unit,
    success: (ArrayList<PhotoInfo>) -> Unit,
) {
    PhotoUploadScope.instance.upload(
        photos = photos,
        progressBar = progressBar,
        error = error,
        success = success,
    )
}

/**
 * 选中/取消照片，
 *
 * 返回true：操作成功；反之，错误toast提示
 */
internal fun selectPhoto(photo: PhotoInfo, count: Int, limitCount: Int): Boolean {
    return if (photo.isCheck) {
        // 取消选中
        photo.isCheck = false
        PhotoData.instance.syncPhoto(photo)
        true
    } else {
        if (checkPhoto(photo, count, limitCount)) {
            // 允许选中
            photo.isCheck = true
            PhotoData.instance.syncPhoto(photo)
            true
        } else {
            false
        }
    }
}

/**
 * 检查即将选中的图片是否合规
 */
internal fun checkPhoto(photo: PhotoInfo, count: Int, limitCount: Int): Boolean {
    if (count >= limitCount) {
        showToast(getString(R.string.select_photo_limit, limitCount))
        return false
    }
    if (photo.localSize > 10_000_000) {
        showToast(getString(R.string.select_photo_size))
        return false
    }
    if (photo.imageFormat != "jpg"
        && photo.imageFormat != "jpeg"
        && photo.imageFormat != "png"
    ) {
        showToast(getString(R.string.select_photo_format))
        return false
    }
    return true
}

/**
 * 选中原图大小
 */
internal fun selectOriginalTotalSize(list: ArrayList<PhotoInfo>): String {
    var size = 0L
    list.forEach {
        size += it.localSize
    }
    return when {
        size > 1000_000_000 -> {
            "(${size / 1000_000_000}G)"
        }
        size > 1000_000 -> {
            "(${size / 1000_000}M)"
        }
        size > 1000 -> {
            "(${size / 1000}K)"
        }
        size == 0L -> {
            ""
        }
        else -> {
            "(${size}B)"
        }
    }
}