package com.kotlin.android.image.component.photo

import android.Manifest
import android.content.Context
import android.database.Cursor
import android.graphics.*
import android.media.ExifInterface
import android.media.MediaMetadataRetriever
import android.media.ThumbnailUtils
import android.os.Build
import android.provider.BaseColumns
import android.provider.MediaStore
import android.util.Size
import android.view.View
import android.view.animation.AlphaAnimation
import android.widget.ImageView
import androidx.collection.ArrayMap
import androidx.fragment.app.FragmentActivity
import com.kotlin.android.app.data.entity.image.PhotoInfo
import com.kotlin.android.ktx.ext.core.getUriForPath
import com.kotlin.android.ktx.ext.dimension.dp
import com.kotlin.android.ktx.ext.log.e
import com.kotlin.android.ktx.ext.log.i
import com.kotlin.android.ktx.ext.permission.permissions
import com.kotlin.android.mtime.ktx.ext.saveShareImage
import kotlinx.coroutines.*
import java.io.File
import java.io.IOException


/**
 * 图库图集相关扩展
 *
 * Created on 2020/7/26.
 *
 * @author o.s
 */

/**
 * 获取所有照片的 Map 集合：
 * 根据相册ID分组 [MediaStore.Images.ImageColumns.BUCKET_ID]
 */
internal fun FragmentActivity.getInternalAllPhotoMap(
    completed: (data: ArrayMap<PhotoBucket, ArrayList<PhotoInfo>>) -> Unit
) {
    val map = ArrayMap<PhotoBucket, ArrayList<PhotoInfo>>()
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
    }
    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
        projection.add(MediaStore.Images.ImageColumns.DURATION)
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


                val bucketId = getLong(bucketIdColumn)
                val bucketName = getString(bucketNameColumn)
                val id = getLong(idColumn)
                val url = getString(dataColumn)
                val orientation = getInt(orientationColumn)
                val displayName = getString(displayNameColumn)
                val width = getInt(widthColumn)
                val height = getInt(heightColumn)
                val size = getLong(sizeColumn)
                var duration = 0
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
                    val durationColumn = getColumnIndexOrThrow(MediaStore.Images.ImageColumns.DURATION)
                    duration = getInt(durationColumn)
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
                    localWidth = width,
                    localHeight = height,
                    localSize = size,
                    localDuration = duration,
                    imageFormat = imageFormat
                )
                val key = map.keys.find { it.id == bucketId }
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
                photo.i()
            }
        }
    } catch (e: Exception) {
        e.printStackTrace()
    } finally {
        cursor?.close()
    }
}

val defWhiteNames: ArrayList<String> = arrayListOf("DCIM", "Camera", "Pictures", "Screenshots", "Browser", "WeiXin", "weibo", "QQ_Images", "news_article", "MTime_Images", "Wanda_Images", "小红书", "wv_save_image", "camera_pic", "images")

/**
 * 权限安全的获取手机图库
 * [whiteNames] 图库白名单
 * [completed] 权限授权完成后加载图库回调
 */
fun FragmentActivity.getAllPhotoBucketsWithPermissions(
        whiteNames: ArrayList<String> = defWhiteNames,
        completed: (data: ArrayList<PhotoBucket>) -> Unit
) {
    permissions(
            Manifest.permission.READ_EXTERNAL_STORAGE,
            Manifest.permission.WRITE_EXTERNAL_STORAGE
    ) {
        onGranted {
            if (it.size == 2) {
                completed(getAllPhotoBuckets(whiteNames))
            }
        }
    }
}

/**
 * 权限安全的获取手机指定白名单图库的图集
 * [whiteNames] 图库白名单
 * [completed] 权限授权完成后加载图库回调
 */
fun FragmentActivity.getAllPhotosWithPermissions(
        whiteNames: ArrayList<String> = defWhiteNames,
        completed: (data: ArrayList<PhotoInfo>) -> Unit
) {
    permissions(
            Manifest.permission.READ_EXTERNAL_STORAGE,
            Manifest.permission.WRITE_EXTERNAL_STORAGE
    ) {
        onGranted {
            if (it.size == 2) {
                getAllPhotos(whiteNames, completed)
            }
        }
    }
}

/**
 * 获取手机指定白名单图库的图集
 * [whiteNames] 图库白名单
 * [completed] 权限授权完成后加载图库回调
 */
fun FragmentActivity.getAllPhotos(
        whiteNames: ArrayList<String> = defWhiteNames,
        completed: (data: ArrayList<PhotoInfo>) -> Unit
) {
    GlobalScope.launch {
        val result = withContext(Dispatchers.IO) {
            val data = ArrayList<PhotoInfo>()
            val photoBuckets = getAllPhotoBuckets(whiteNames)
            photoBuckets.i()
            photoBuckets.forEach { photoBucket ->
                val photos = getPhotos(photoBucket)
                data.addAll(photos)
            }
            data
        }
        withContext(Dispatchers.Main) {
            completed(result)
        }
    }
}

/**
 * 获取手机内图库
 * [whiteNames] 图库白名单
 */
fun Context.getAllPhotoBuckets(
        whiteNames: ArrayList<String> = defWhiteNames
): ArrayList<PhotoBucket> {
    val list = ArrayList<PhotoBucket>()

    val projection = arrayOf(
            MediaStore.Images.ImageColumns.BUCKET_ID,
            MediaStore.Images.ImageColumns.BUCKET_DISPLAY_NAME,
            MediaStore.Images.ImageColumns._ID,
            MediaStore.Images.ImageColumns.MIME_TYPE,
            MediaStore.Images.ImageColumns.DATA
//            MediaStore.Images.ImageColumns.DATE_TAKEN,
//            MediaStore.Images.ImageColumns.MINI_THUMB_MAGIC
    )

    val orderBy = String.format("%s DESC", MediaStore.Images.ImageColumns.DATE_ADDED)

    var cursor: Cursor? = null
    try {
        cursor = contentResolver.query(
                MediaStore.Images.Media.EXTERNAL_CONTENT_URI,
                projection,
                null,
                null,
                orderBy
        )?.apply {
            while (moveToNext()) {
                val idColumn = getColumnIndexOrThrow(BaseColumns._ID)
                val bucketIdColumn = getColumnIndexOrThrow(MediaStore.Images.Media.BUCKET_ID)
                val bucketNameColumn = getColumnIndexOrThrow(MediaStore.Images.Media.BUCKET_DISPLAY_NAME)
//                val dataColumn = getColumnIndexOrThrow(MediaStore.Images.Media.DATA)

                val id = getLong(idColumn)
                val bucketId = getLong(bucketIdColumn)
                val bucketName = getString(bucketNameColumn)
//                val data = getString(dataColumn)
//                "id=$id, bucketId=$bucketId, 图片文件夹=$bucketName".e()
                if (whiteNames.contains(bucketName)) {
//                    "bucketName = $bucketName :: bucketId = $bucketId :: id = $id :: data = $data :: thumbnailData = $thumbnailData".d()
                    var photoBucket: PhotoBucket? = null
                    list.forEach {
                        if (it.id == bucketId) {
                            photoBucket = it
                            it.count++
                        }
                    }
                    if (photoBucket == null) {
                        photoBucket = PhotoBucket(
                                id = bucketId,
                                name = bucketName,
                                count = 1,
                                thumbnailUri = MediaStore.Images.Media.EXTERNAL_CONTENT_URI.buildUpon().appendPath(id.toString()).build()
                        ).apply {
                            list.add(this)
                        }
                    }
                }
            }
        }
    } catch (e: Exception) {
        e.printStackTrace()
    } finally {
        cursor?.close()
    }
//    list.e()
    return list
}

/**
 * 获取指定图库的照片集
 * [photoBucket] 图库
 */
fun Context.getPhotos(photoBucket: PhotoBucket): ArrayList<PhotoInfo> {
    val list = ArrayList<PhotoInfo>()

    val projection = arrayOf(
        MediaStore.Images.ImageColumns.BUCKET_ID,
        MediaStore.Images.ImageColumns.BUCKET_DISPLAY_NAME,
        MediaStore.Images.ImageColumns._ID,
        MediaStore.Images.ImageColumns.DATA,
        MediaStore.Images.ImageColumns.ORIENTATION,
        MediaStore.Images.ImageColumns.DISPLAY_NAME,
        MediaStore.Images.ImageColumns.WIDTH,
        MediaStore.Images.ImageColumns.HEIGHT,
        MediaStore.Images.ImageColumns.SIZE,
//        MediaStore.Images.ImageColumns.DURATION,
    )
    val selection = "${MediaStore.Images.ImageColumns.BUCKET_ID} = ?"
    val selectionArgs = arrayOf(photoBucket.id.toString())
    val orderBy = "${MediaStore.Images.ImageColumns.DATE_ADDED} DESC"


    var cursor: Cursor? = null
    try {
        cursor = contentResolver.query(
                MediaStore.Images.Media.EXTERNAL_CONTENT_URI, //.buildUpon().build(),
                projection,
                selection,
                selectionArgs,
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
//                val durationColumn = getColumnIndexOrThrow(MediaStore.Images.ImageColumns.DURATION)

                val id = getLong(idColumn)
                val url = getString(dataColumn)
                val orientation = getInt(orientationColumn)
                val displayName = getString(displayNameColumn)
                val width = getInt(widthColumn)
                val height = getInt(heightColumn)
                val size = getLong(sizeColumn)
                val bucketId = getLong(bucketIdColumn)
                val bucketName = getString(bucketNameColumn)
//                val duration = getInt(durationColumn)

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
                    localWidth = width,
                    localHeight = height,
                    localSize = size,
//                    localDuration = duration,
                    imageFormat = imageFormat
                )
                list.add(photo)
                photo.i()
            }
        }
    } catch (e: Exception) {
        e.printStackTrace()
    } finally {
        cursor?.close()
    }

    return list
}

/**
 * 获取缩略图
 */
fun Context.getThumbnail(data: PhotoInfo, width: Int = 80.dp, height: Int = 80.dp): Bitmap? {
    val uri = data.uri ?: return null
    val bitmap = try {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            contentResolver.loadThumbnail(uri, Size(width, height), null)
        } else {
            val option = BitmapFactory.Options()
            option.outWidth = width
            option.outHeight = height
            MediaStore.Images.Thumbnails.getThumbnail(contentResolver, data.id, MediaStore.Images.Thumbnails.MINI_KIND, option)
        }
    } catch (e: Exception) {
        data.e()
        e.printStackTrace()
        null
    }
    return bitmap?.rotatingImageView(data.orientation)
}

/**
 * 通过 Uri 加载本地图片 Bitmap，并旋转角度
 */
fun Context.getBitmapWithLocalUri(data: PhotoInfo?): Bitmap? {
    val uri = data?.uri ?: return null
    val bitmap = try {
        BitmapFactory.decodeStream(contentResolver.openInputStream(uri))
    } catch (e: Exception) {
        data.e()
        e.printStackTrace()
        null
    }
    return bitmap?.rotatingImageView(data.orientation)
}

/**
 * 给ImageView设置缩略图
 */
fun ImageView.setThumbnail(data: PhotoInfo, width: Int = 80.dp, height: Int = 80.dp, playAnim: Boolean = false) {
    GlobalScope.launch {
        val bitmap  = withContext(Dispatchers.IO) {
            context.getThumbnail(data, width, height)?.cropRoundCornersTransformation(width, height)
        }
        withContext(Dispatchers.Main) {
//            "Thumbnail size = ${bitmap.getSize()}".w()
            if (playAnim) {
                alphaAnimationStart()
            }
            if (data == tag) {
                setImageBitmap(bitmap)
            }
        }
    }
}

/**
 * Bitmap裁剪圆角矩形
 */
fun Bitmap.cropRoundCornersTransformation(
        width: Int = 80.dp,
        height: Int = 80.dp,
        radius: Int = 4.dp,
        config: Bitmap.Config = Bitmap.Config.ARGB_8888
): Bitmap {
    val bitmap = Bitmap.createBitmap(width, height, config)
    val canvas = Canvas(bitmap)
    val paint = Paint()
    paint.isAntiAlias = true
    val srcScale = this.width / this.height.toFloat()
    val dstScale = width / height.toFloat()
    var w = this.width
    var h = this.height
    if (srcScale > dstScale) {
        w = (h * dstScale).toInt()
    } else {
        h = (w / dstScale).toInt()
    }
    val l = (this.width - w) / 2
    val r = l + w
    val t = (this.height - h) / 2
    val b = t + h
    val rect = Rect(l, t, r, b)
    val rectF = RectF(0F, 0F, width.toFloat(), height.toFloat())
//    "width = ${this.width} :: height = ${this.height} :: rect = $rect :: rectF = $rectF".e()
    canvas.drawRoundRect(rectF, radius.toFloat(), radius.toFloat(), paint)
    paint.xfermode = PorterDuffXfermode(PorterDuff.Mode.SRC_IN)
    canvas.drawBitmap(this, rect, rectF, paint)
    recycle()
    return bitmap
}

/**
 * alpha动画
 */
fun View.alphaAnimationStart() {
    val alphaAnimation = AlphaAnimation(0.7F, 1.0F)
    alphaAnimation.duration = 300
    startAnimation(alphaAnimation)
}

/**
 * 旋转图片：
 * [angle] 角度
 */
fun Bitmap.rotatingImageView(angle: Int): Bitmap {
    if (angle == 0) {
        return this
    }
    //旋转图片 动作
    val matrix = Matrix()
    matrix.postRotate(angle.toFloat())
    "选择图片：angle = $angle".e()
    // 创建新的图片
    return Bitmap.createBitmap(this, 0, 0,
            this.width, this.height, matrix, true)
}

/**
 * 读取图片属性：旋转的角度
 * @param path 图片绝对路径
 * @return degree旋转的角度
 */
fun readPictureDegree(path: String): Int {
    var degree = 0
    try {
        val exifInterface = ExifInterface(path)
        val orientation = exifInterface.getAttributeInt(ExifInterface.TAG_ORIENTATION, ExifInterface.ORIENTATION_NORMAL)
        degree = when (orientation) {
            ExifInterface.ORIENTATION_ROTATE_90 -> 90
            ExifInterface.ORIENTATION_ROTATE_180 -> 180
            ExifInterface.ORIENTATION_ROTATE_270 -> 270
            else -> 0
        }
    } catch (e: IOException) {
        e.printStackTrace()
    }
    return degree
}

/**
 * 获取视频缩略图
 */
fun getThumbnailOfVideo(path:String):String{
    val file = File(path)
    return if (file.exists()){
        val bitmap = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            ThumbnailUtils.createVideoThumbnail(file, Size(300,300), null)
        }else{
            ThumbnailUtils.createVideoThumbnail(path,MediaStore.Images.Thumbnails.MINI_KIND)
        }
        val thumbPath  = bitmap?.saveShareImage()
        "缩略图路径：$thumbPath".e()
        thumbPath.orEmpty()
    }else{
        ""
    }

}

/**
 * 获取视频第一帧
 */
fun getVideoFrameAtTime(videoPath:String):String{
    val mediaMetadataRetriever = MediaMetadataRetriever()
    mediaMetadataRetriever.setDataSource(videoPath)
    val frameAtTime = mediaMetadataRetriever.frameAtTime
    val thumbPath  = frameAtTime?.saveShareImage()
    "缩略图路径：$thumbPath".e()
    return thumbPath.orEmpty()
}
