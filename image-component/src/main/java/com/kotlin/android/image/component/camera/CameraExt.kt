package com.kotlin.android.image.component.camera

import android.Manifest
import android.content.ContentValues
import android.content.Context
import android.content.Intent
import android.graphics.Bitmap
import android.media.MediaScannerConnection
import android.net.Uri
import android.os.Build
import android.os.Environment
import android.provider.MediaStore
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentActivity
import com.kotlin.android.ktx.ext.core.getPathForUriDocument
import com.kotlin.android.ktx.ext.core.getUriForPath
import com.kotlin.android.ktx.ext.io.generateFileName
import com.kotlin.android.ktx.ext.io.safeClose
import com.kotlin.android.ktx.ext.log.e
import com.kotlin.android.ktx.ext.log.i
import com.kotlin.android.ktx.ext.log.w
import com.kotlin.android.ktx.ext.permission.permissions
import java.io.File
import java.io.FileOutputStream

/**
 * 相机拍照相关扩展
 *
 * Created on 2020/7/24.
 *
 * @author o.s
 */

/**
 * 打开相机请求Code
 */
const val REQUEST_OPEN_CAMERA = 10023 // Can only use lower 16 bits for requestCode (65536)
const val REQUEST_OPEN_PHOTO = 10024 //

/**
 * 申请权限安全打开相机
 */
fun FragmentActivity.openCameraWithPermissions() {
    permissions(
            Manifest.permission.READ_EXTERNAL_STORAGE,
            Manifest.permission.WRITE_EXTERNAL_STORAGE,
            Manifest.permission.CAMERA
    ) {
        onGranted {
            if (it.size == 3) {
                openCamera()
            }
        }
    }
}

var cameraOutPath: String? = null
var cameraUri: Uri? = null

/**
 * 打开相机
 */
fun FragmentActivity.openCamera() {
    if (Environment.getExternalStorageState() != Environment.MEDIA_MOUNTED) {
        return
    }
    val intent = Intent(MediaStore.ACTION_IMAGE_CAPTURE)
    intent.flags = Intent.FLAG_GRANT_READ_URI_PERMISSION or Intent.FLAG_GRANT_WRITE_URI_PERMISSION
//    val values = ContentValues()
//    values.put(MediaStore.Images.Media.MIME_TYPE, "image/jpeg")
//    values.put(MediaStore.Images.Media.DATA, cameraOutputPath())
//    cameraUri = contentResolver.insert(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, values)?.apply {
//        intent.putExtra(MediaStore.EXTRA_OUTPUT, this)
//        intent.putExtra(MediaStore.EXTRA_VIDEO_QUALITY, 1)
//    }

    cameraOutPath = generateCameraOutputPath().apply {
        cameraUri = getUriForPath(this)
        intent.putExtra(MediaStore.EXTRA_OUTPUT, cameraUri)
        intent.putExtra(MediaStore.EXTRA_VIDEO_QUALITY, 1)
        intent.putExtra(MediaStore.Images.Media.MIME_TYPE, "image/jpeg")
        startActivityForResult(intent, REQUEST_OPEN_CAMERA)
    }
}

fun Fragment.openCamera() {
    activity?.openCamera()
}

fun Fragment.openCameraWithPermissions() {
    activity?.openCameraWithPermissions()
}

/**
 * 处理相机拍照回调
 */
fun FragmentActivity.onActivityResultCameraData(requestCode: Int, resultCode: Int, data: Intent?, completed: ((path: String, data: Uri) -> Unit)? = null) {
    // resultCode == -1 表示相机拍照点击保存（成功）；0 表示取消（失败），可参照API处理
    if (REQUEST_OPEN_CAMERA == requestCode && resultCode == -1) {
        "onActivityResultCameraData -> requestCode = $requestCode, resultCode = $resultCode, data = ${data?.extras}".i()
        cameraOutPath?.apply {
            scanFile(this, cameraUri) { path, uri ->
                cameraOutPath = null
                cameraUri = null
                completed?.invoke(path, uri)
            }
        }
    }
    if (REQUEST_OPEN_PHOTO == requestCode && resultCode == -1) {
        val uri = data?.data
        if (uri != null) {
            getPathForUriDocument(uri)?.apply {
                completed?.invoke(this, uri)
            }
        }
    }
}

/**
 * 处理相机拍照回调
 */
fun Fragment.onActivityResultCameraData(requestCode: Int, resultCode: Int, data: Intent?, completed: ((path: String, data: Uri) -> Unit)? = null) {
    activity?.onActivityResultCameraData(requestCode, resultCode, data, completed)
}

/**
 * 扫描更新指定路径的图片文件
 */
fun Context.scanFile(path: String, uri: Uri? = null, completed: (path: String, uri: Uri) -> Unit) {
    MediaScannerConnection.scanFile(
            this,
            arrayOf(path),
            arrayOf("image/jpeg")
    ) { p, photoUri ->
        val newUri = if (photoUri == null) {
//            getUri(p)
            sendBroadcast(Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE, uri))
            uri
        } else {
            photoUri
        }
        newUri?.apply {
            completed.invoke(p, this)
        }
    }
}

/**
 * 相机拍照时先生成一张照片路径，包含照片生成的名称。
 * 注意：该路径在保存成功 [FragmentActivity.onActivityResultCameraData]（即：拍照保存成功回调），[Context.scanFile] 扫描系统文件时需要
 * [fileName]: 生成的照片名称
 */
fun generateCameraOutputPath(fileName: String = generateFileName()): String {
    return "${Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DCIM).path}${File.separator}$DIRECTORY_CAMERA$fileName.jpg"
}

/**
 * 将文件保存到公共的媒体文件夹
 * [bitmap]: 需要保存的 bitmap
 * [fileName]: 单纯的指文件名，不包含路径
 */
fun Context.saveBitmapToGallery(bitmap: Bitmap, fileName: String, completed: ((data: Uri) -> Unit)? = null) {
    if (Build.VERSION.SDK_INT < Build.VERSION_CODES.Q) {
        saveBitmapToGalleryLowerQ(bitmap, fileName, completed)
        return
    }
    val values = ContentValues()
    values.put(MediaStore.Images.Media.DISPLAY_NAME, fileName)
    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
        /**
         * android Q 中不再使用DATA字段，而用RELATIVE_PATH代替
         * RELATIVE_PATH 是相对路径不是绝对路径
         * DCIM 是系统文件夹，关于系统文件夹可以到系统自带的文件管理器中查看，不可以写没存在的名字
         */
        values.put(MediaStore.Images.Media.RELATIVE_PATH, DIRECTORY_DCIM)
    } else {
        values.put(MediaStore.Images.Media.DATA, Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES).path)
    }
    val f1 = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES).path
    val filePath = "${Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DCIM).path}${File.separator}$DIRECTORY_CAMERA$fileName.jpeg"
    f1.w()
    filePath.e()
    // 设置文件类型
    values.put(MediaStore.Images.Media.MIME_TYPE, "image/jpeg")
    "saveBitmapToGallery :: fileName = $fileName".w()
    // 执行insert操作，向系统文件夹中添加文件。[EXTERNAL_CONTENT_URI] 代表外部存储器，该值不变
    contentResolver.insert(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, values)?.apply {
        contentResolver.openOutputStream(this)?.apply {
            bitmap.compress(Bitmap.CompressFormat.JPEG, 100, this)
            flush()
            safeClose()
        }
        "uri = $this".e()
        completed?.invoke(this)
    }
}

/**
 * Api < 29
 * 将文件保存到公共的媒体文件夹
 * [bitmap]: 需要保存的 bitmap
 * [fileName]: 单纯的指文件名，不包含路径
 */
fun Context.saveBitmapToGalleryLowerQ(bitmap: Bitmap, fileName: String, completed: ((data: Uri) -> Unit)? = null) {
    "saveBitmapToGalleryLowerQ :: fileName = $fileName".w()
    val brand = Build.BRAND ?: ""
    val filePath = "${Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DCIM).path}${File.separator}$DIRECTORY_CAMERA$fileName.jpeg"
    val file = File(filePath).apply {
        if (exists()) {
            delete()
        }
    }
    var fos: FileOutputStream? = null
    try {
        fos = FileOutputStream(file)
        if (bitmap.compress(Bitmap.CompressFormat.JPEG, 100, fos)) {
            fos.flush()
            MediaStore.Images.Media.insertImage(contentResolver, file.absolutePath, "$fileName.jpeg", null).apply {
                "<29 path = $this".w()
                completed?.invoke(getUriForPath(this))
            }
        }

        scanFile(filePath) { path, uri ->
            "saveBitmapToGallery : path = $path, uri = $uri".e()
        }
    } catch (e: Exception) {
    } finally {
        fos.safeClose()
    }
}

/**
 * 本地图片路径转Uri
 */
fun Context.getUri(path: String?): Uri? {
    if (path == null) {
        return null
    }
    val selection = StringBuffer()
    selection.append("(")
        .append(MediaStore.Images.ImageColumns.DATA)
        .append("=")
        .append("'${Uri.decode(path)}'")
        .append(")")
    val cursor = contentResolver.query(
        MediaStore.Images.Media.EXTERNAL_CONTENT_URI,
        arrayOf(MediaStore.Images.ImageColumns._ID),
        selection.toString(),
        null,
        null
    ) ?: return null

    var index = 0
    cursor.moveToFirst()
    while (!cursor.isAfterLast) {
        index = cursor.getColumnIndex(MediaStore.Images.ImageColumns._ID)
        // set _id value
        index = cursor.getInt(index)
        cursor.moveToNext()
    }
    return if (index != 0) {
        Uri.parse("content://media/external/images/media/$index")
    } else {
        null
    }
}

/**
 * 获取相册文件根目录（相对路径SD）
 */
val DIRECTORY_DCIM: String
    get() {
        val brand = Build.BRAND ?: ""
        return if (brand.equals("xiaomi", true) || brand.equals("Huawei", true)) {
            "DCIM/Camera/"
        } else {
            "DCIM/"
        }
    }

/**
 * 获取相册文件根目录（相对路径SD）
 */
val DIRECTORY_CAMERA: String
    get() {
        val brand = Build.BRAND ?: ""
        return if (brand.equals("xiaomi", true) || brand.equals("Huawei", true)) {
            "Camera/"
        } else {
            ""
        }
    }