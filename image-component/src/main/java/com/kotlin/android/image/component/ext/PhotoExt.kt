package com.kotlin.android.image.component.ext

import android.Manifest
import android.content.Context
import android.database.Cursor
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.Matrix
import android.os.Build
import android.provider.BaseColumns
import android.provider.MediaStore
import android.util.Size
import androidx.fragment.app.DialogFragment
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentActivity
import com.kotlin.android.app.data.entity.image.PhotoInfo
import com.kotlin.android.image.component.PHOTO_RECENT
import com.kotlin.android.image.component.photo.PhotoBucket
import com.kotlin.android.ktx.ext.core.getUriForPath
import com.kotlin.android.ktx.ext.dimension.dp
import com.kotlin.android.ktx.ext.log.e
import com.kotlin.android.ktx.ext.log.i
import com.kotlin.android.ktx.ext.permission.permissions
import kotlinx.coroutines.*
import java.util.*
import kotlin.collections.ArrayList
import kotlin.collections.LinkedHashMap

/**
 * 手机相册扩展
 *
 * Created on 2022/5/12.
 *
 * @author o.s
 */

fun DialogFragment.getAllPhotoMap(
    groupBy: String = MediaStore.Images.ImageColumns.BUCKET_ID,
    completed: (data: LinkedHashMap<PhotoBucket, ArrayList<PhotoInfo>>) -> Unit
) {
    (context as? FragmentActivity)?.getAllPhotoMap(groupBy = groupBy, completed = completed)
}

fun Fragment.getAllPhotoMap(
    groupBy: String = MediaStore.Images.ImageColumns.BUCKET_ID,
    completed: (data: LinkedHashMap<PhotoBucket, ArrayList<PhotoInfo>>) -> Unit
) {
    (context as? FragmentActivity)?.getAllPhotoMap(groupBy = groupBy, completed = completed)
}

/**
 * 权限安全的获取所有照片的 Map 集合：
 * 在子线程中读取照片，完成后回调UI线程。
 *
 * [groupBy] 分组字段:
 *  [MediaStore.Images.ImageColumns.BUCKET_ID]
 *  [MediaStore.Images.ImageColumns.BUCKET_DISPLAY_NAME]
 *  默认使用 [MediaStore.Images.ImageColumns.BUCKET_ID]
 */
fun FragmentActivity.getAllPhotoMap(
    groupBy: String = MediaStore.Images.ImageColumns.BUCKET_ID,
    completed: (data: LinkedHashMap<PhotoBucket, ArrayList<PhotoInfo>>) -> Unit
) {
    permissions(
        Manifest.permission.READ_EXTERNAL_STORAGE,
        Manifest.permission.WRITE_EXTERNAL_STORAGE
    ) {
        onGranted {
            if (it.size == 2) {
                MainScope().let { main ->
                    main.launch(Dispatchers.Main) {
                        val deferred = async(Dispatchers.IO) {
                            getInternalAllPhotoMap(groupBy = groupBy)
                        }

                        val result = deferred.await()

                        completed(result)

                        // 取消协程
                        main.cancel()
                    }
                }
            }
        }
    }
}

/**
 * 获取所有照片的 Map 集合：
 * 根据相册ID分组 [MediaStore.Images.ImageColumns.BUCKET_ID]
 */
internal fun Context.getInternalAllPhotoMap(
    groupBy: String = MediaStore.Images.ImageColumns.BUCKET_ID,
): LinkedHashMap<PhotoBucket, ArrayList<PhotoInfo>> {
    val isID = groupBy == MediaStore.Images.ImageColumns.BUCKET_ID
    val ca = Calendar.getInstance()
    ca.time = Date()
    ca.add(Calendar.YEAR, -1)
    val endDate = ca.time.time / 1000 // (s) 近一年的照片"最近项目"

    val map = LinkedHashMap<PhotoBucket, ArrayList<PhotoInfo>>()

//    val allBucket = PhotoBucket(
//        id = -2,
//        name = "所有照片",
//    )
//    map[allBucket] = ArrayList()

    val lastBucket = PhotoBucket(
        id = -1,
        name = PHOTO_RECENT,
    )
    map[lastBucket] = ArrayList()

    val projection = ArrayList<String>().apply {
        add(MediaStore.Images.ImageColumns.BUCKET_ID)
        add(MediaStore.Images.ImageColumns.BUCKET_DISPLAY_NAME)
        add(MediaStore.Images.ImageColumns._ID)
        add(MediaStore.Images.ImageColumns.DATA)
        add(MediaStore.Images.ImageColumns.ORIENTATION)
        add(MediaStore.Images.ImageColumns.DISPLAY_NAME)
        add(MediaStore.Images.ImageColumns.WIDTH)
        add(MediaStore.Images.ImageColumns.HEIGHT)
        add(MediaStore.Images.ImageColumns.SIZE)
        add(MediaStore.Images.ImageColumns.DATE_ADDED)
        add(MediaStore.Images.ImageColumns.DATE_MODIFIED)
    }
    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
        projection.add(MediaStore.Images.ImageColumns.DURATION)
        projection.add(MediaStore.Images.ImageColumns.ORIGINAL_DOCUMENT_ID)
        projection.add(MediaStore.Images.ImageColumns.OWNER_PACKAGE_NAME)
        projection.add(MediaStore.Images.ImageColumns.VOLUME_NAME)
    }

    val orderBy = "${MediaStore.Images.ImageColumns.DATE_ADDED} DESC"

    var cursor: Cursor? = null
    try {
        cursor = contentResolver.query(
            MediaStore.Images.Media.EXTERNAL_CONTENT_URI,
            projection.toTypedArray(),
            null,
            null,
            orderBy
        )?.apply {
            while (moveToNext()) {
                val bucketIdColumn = getColumnIndexOrThrow(MediaStore.Images.ImageColumns.BUCKET_ID)
                val bucketNameColumn = getColumnIndexOrThrow(MediaStore.Images.ImageColumns.BUCKET_DISPLAY_NAME)
                val idColumn = getColumnIndexOrThrow(BaseColumns._ID)
                val dataColumn = getColumnIndexOrThrow(MediaStore.Images.Media.DATA)
                val orientationColumn = getColumnIndexOrThrow(MediaStore.Images.ImageColumns.ORIENTATION)
                val displayNameColumn = getColumnIndexOrThrow(MediaStore.Images.ImageColumns.DISPLAY_NAME)
                val widthColumn = getColumnIndexOrThrow(MediaStore.Images.ImageColumns.WIDTH)
                val heightColumn = getColumnIndexOrThrow(MediaStore.Images.ImageColumns.HEIGHT)
                val sizeColumn = getColumnIndexOrThrow(MediaStore.Images.ImageColumns.SIZE)
                val dateAddedColumn = getColumnIndexOrThrow(MediaStore.Images.ImageColumns.DATE_ADDED)
                val dateModifiedColumn = getColumnIndexOrThrow(MediaStore.Images.ImageColumns.DATE_MODIFIED)

                val bucketId = getLong(bucketIdColumn)
                val bucketName = getString(bucketNameColumn)
                val id = getLong(idColumn)
                val url = getString(dataColumn)
                val orientation = getInt(orientationColumn)
                val displayName = getString(displayNameColumn)
                val width = getInt(widthColumn)
                val height = getInt(heightColumn)
                val size = getLong(sizeColumn)
                val dateAdded = getLong(dateAddedColumn)
                val dateModified = getLong(dateModifiedColumn)
                var duration = 0
                var original: String? = null
                var ownerPkgName: String? = null
                var volumeName: String? = null
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
                    val durationColumn = getColumnIndexOrThrow(MediaStore.Images.ImageColumns.DURATION)
                    val originalColumn = getColumnIndexOrThrow(MediaStore.Images.ImageColumns.ORIGINAL_DOCUMENT_ID)
                    val ownerPkgNameColumn = getColumnIndexOrThrow(MediaStore.Images.ImageColumns.OWNER_PACKAGE_NAME)
                    val volumeNameColumn = getColumnIndexOrThrow(MediaStore.Images.ImageColumns.VOLUME_NAME)
                    duration = getInt(durationColumn)
                    original = getString(originalColumn)
                    ownerPkgName = getString(ownerPkgNameColumn)
                    volumeName = getString(volumeNameColumn)
                }

                if (width > 0 && height > 0) {
                    var w = width
                    var h = height
                    if (orientation == 90 || orientation == 270) {
                        w = height
                        h = width
                    }
                    val uri = getUriForPath(url)
                    val imageFormat = uri.lastPathSegment?.run {
                        substring(lastIndexOf(".") + 1)
                    } ?: ""
                    val photo = PhotoInfo(
                        id = id,
                        bucketId = bucketId,
                        bucketName = bucketName,
                        name = displayName,
                        uri = uri,
                        path = url,
                        uploadPath = url,
                        orientation = orientation,
                        localWidth = w,
                        localHeight = h,
                        localSize = size,
                        localDateAdded = dateAdded,
                        localDateModified = dateModified,
                        localDuration = duration,
                        localOriginal = original,
                        localPkgName = ownerPkgName,
                        localVolumeName = volumeName,
                        imageFormat = imageFormat
                    )
                    photo.i()
                    "endDate=$endDate".e()

                    if (dateModified >= endDate) {
                        map[lastBucket]?.add(photo)
                    }
                    val key = map.keys.find {
                        if (isID) {
                            it.id == bucketId
                        } else {
                            it.name == bucketName
                        }
                    }
                    if (key == null) {
                        val photoBucket = PhotoBucket(
                            id = bucketId,
                            name = bucketName,
                        )
                        map[photoBucket] = ArrayList<PhotoInfo>().apply {
                            add(photo)
                        }
                    } else {
                        map[key]?.add(photo)
                    }
                }
            }
        }
        map.entries.forEach {
            it.key.count = it.value.size
            it.key.photoInfo = it.value.firstOrNull()
        }
        map.forEach { (key, _) ->
            "bID=${key.id} bName=${key.name} count=${key.count}".e()
        }
        return map
    } catch (e: Exception) {
        e.printStackTrace()
        return map
    } finally {
        cursor?.close()
    }
}

/**
 * 加载缩略图，子线程处理（协程）
 */
fun Context.loadThumbnail(
    data: PhotoInfo,
    width: Int = 80.dp,
    height: Int = 80.dp,
    completed: (Bitmap?) -> Unit
) {
    MainScope().let { main ->
        main.launch(Dispatchers.Main) {
            val deferred = async(Dispatchers.IO) {
                loadThumbnail(
                    data = data,
                    width = width,
                    height = height,
                )
            }

            val result = deferred.await()

            completed(result)

            // 取消协程
            main.cancel()
        }
    }
}

/**
 * 内部加载缩略图，耗时操作
 */
internal fun Context.loadThumbnail(
    data: PhotoInfo,
    width: Int,
    height: Int,
): Bitmap? {
    val uri = data.uri ?: return null
    val bitmap = try {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            contentResolver.loadThumbnail(uri, Size(width, height), null)
        } else {
            val option = BitmapFactory.Options().apply {
                outWidth = width
                outHeight = height
            }
            MediaStore.Images.Thumbnails.getThumbnail(
                contentResolver,
                data.id,
                MediaStore.Images.Thumbnails.MINI_KIND,
                option
            )
        }
    } catch (e: Exception) {
        data.e()
        e.printStackTrace()
        null
    }
    return bitmap?.rotating(data.orientation)
}

/**
 * 旋转图片：
 * [angle] 角度
 */
fun Bitmap.rotating(angle: Int): Bitmap {
    if (angle == 0) {
        return this
    }
    //旋转图片 动作
    val matrix = Matrix()
    matrix.postRotate(angle.toFloat())
    "选择图片：angle = $angle".e()
    // 创建新的图片
    val b = Bitmap.createBitmap(this, 0, 0, width, height, matrix, true)
    recycle()
    return b
}