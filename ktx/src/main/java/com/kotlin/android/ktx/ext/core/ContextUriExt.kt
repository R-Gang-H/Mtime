package com.kotlin.android.ktx.ext.core

import android.content.ContentUris
import android.content.Context
import android.content.Intent
import android.database.Cursor
import android.net.Uri
import android.os.Build
import android.os.Environment
import android.provider.MediaStore
import androidx.core.content.FileProvider
import com.kotlin.android.ktx.ext.io.safeClose
import com.kotlin.android.ktx.ext.log.e
import com.kotlin.android.ktx.ext.uri.*
import java.io.*

/**
 * Content Uri 相关扩展:
 *
 * Created on 2021/3/9.
 *
 * @author o.s
 */

/**
 * 授予 Content Uri 临时读访问权限
 */
fun Context.grantReadUriPermission(
    contentUri: Uri,
    modeFlags: Int = Intent.FLAG_GRANT_READ_URI_PERMISSION
) {
    grantUriPermission(packageName, contentUri, modeFlags)
}

/**
 * 授予 Content Uri 临时读写访问权限
 */
fun Context.grantUriPermission(
    contentUri: Uri,
    modeFlags: Int = Intent.FLAG_GRANT_READ_URI_PERMISSION or Intent.FLAG_GRANT_WRITE_URI_PERMISSION
) {
    grantUriPermission(packageName, contentUri, modeFlags)
}

/**
 * 生成指定文件的 Content Uri
 * Content URI方便与另一个APP应用程序共享同一个文件
 * [dir]: app环境的各种根目录
 * [path]: 自定义路径（如: Images/tmp）
 * [fileName]: 自定义文件名称（含格式，如: abc_2020-06-06.jpg）
 */
fun Context.getContentUri(dir: File? = getExternalFilesDir(Environment.DIRECTORY_PICTURES), path: String, fileName: String): Uri {
    return FileProvider.getUriForFile(this, "${packageName}.fileprovider", File(dir, "$path/$fileName"))
}

/**
 * 获取 Content Uri
 */
fun Context.getContentUri(intent: Intent?): Uri? {
    return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
        intent?.data?.let { uri ->
            // 先转存到沙盒目录下
            uri.getPath(this)?.let {
                FileProvider.getUriForFile(this, "${packageName}.fileprovider", File(it))
            }
        }
    } else {
        // android7.0以下不支持content://
        intent?.data
    }
}

/**
 * 生成指定文件的Uri
 */
fun Context.getUriForPath(path: String): Uri {
    return getUriForFile(File(path))
}

/**
 * 生成指定文件的Uri
 */
fun Context.getUriForFile(file: File): Uri {
    return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
        FileProvider.getUriForFile(this, "${packageName}.fileprovider", file)
    } else {
        Uri.fromFile(file)
    }
}

/**
 * 获取 Uri 资源描述的文件路径
 */
fun Context.getPath(uri: Uri): String? {
    val isKitKat = Build.VERSION.SDK_INT >= 19
    val isQ = Build.VERSION.SDK_INT >= 29

    if (isQ && uri.isDocumentUri) {
        // document
        return getPathForUriDocument(uri)
    }

    var path: String? = null
    if (isKitKat && uri.isDocumentUri) {
        when {
            uri.isExternalStorageDocument -> {
                // ExternalStorageProvider
                uri.documentId?.run {
                    val split = split(":")
                    if ("primary" == split[0]) {
                        path = "${Environment.getExternalStorageDirectory()}/${split[1]}"
                    }
                }
            }
            uri.isDownloadsDocument -> {
                // DownloadsProvider
                uri.documentId?.run {
                    val contentUri = ContentUris.withAppendedId(uri, toLong())
                    path = getDataColumn(contentUri, null, null)
                }
            }
            uri.isMediaDocument -> {
                // MediaProvider
                uri.documentId?.run {
                    val split = split(":")
                    val contentUri = when (split[0]) {
                        "image" -> MediaStore.Images.Media.EXTERNAL_CONTENT_URI
                        "video" -> MediaStore.Video.Media.EXTERNAL_CONTENT_URI
                        "audio" -> MediaStore.Audio.Media.EXTERNAL_CONTENT_URI
                        else -> null
                    }
                    contentUri?.run {
                        val selection = "_id=?"
                        val selectionArgs = arrayOf(split[1])
                        path = getDataColumn(this, selection, selectionArgs)
                    }
                }
            }
        }
    } else if (uri.isContentUri) {
        path = if (uri.isGooglePhotosUri) {
            uri.lastPathSegment
        } else {
            getDataColumn(uri, null, null)
        }
    } else if (uri.isFileUri) {
        path = uri.path
    }

    return path
}

/**
 * 解析文件路径
 */
fun Context.getDataColumn(uri: Uri, selection: String?, selectionArgs: Array<String>?): String? {
    var cursor: Cursor? = null
    val column = MediaStore.Images.ImageColumns.DATA //"_data"
    val projection = arrayOf(column)
    return try {
        cursor = contentResolver.query(uri, projection, selection, selectionArgs, null)
        cursor?.run {
            cursor.moveToFirst()
            val index = cursor.getColumnIndexOrThrow(column)
            cursor.getString(index)
        }
    } catch (e: Exception) {
        e.printStackTrace()
        null
    } finally {
        cursor?.close()
    }
}

/**
 * 获取 document 类型 Uri 文件路径
 */
fun Context.getPathForUriDocument(uri: Uri): String? {
    var fos: FileOutputStream? = null
    var ins: InputStream? = null
    try {
        ins = contentResolver.openInputStream(uri)
        if (ins == null) {
            "openInputStream from uri is null".e()
            return null
        }

        val fileName = uri.path?.run {
            substring(lastIndexOf("/") + 1, length)
        } ?: "${System.currentTimeMillis()}.jpg"

        val file = File(getExternalFilesDir("Images"), fileName)
        if (file.exists()) {
            "android Q ${file.absolutePath} exists".e()
            return file.absolutePath
        }
        if (file.parentFile?.exists() != true) {
            if (!file.mkdirs()) {
                "android Q create dirs fail".e()
            }
        }
        if (!file.createNewFile()) {
            "android Q create new file fail".e()
        }
        fos = FileOutputStream(file)

        var len = -1
        val buffer = ByteArray(4096)
        while ({ len = ins.read(buffer); len }() > 0) {
            fos.write(buffer, 0, len)
        }
        return file.absolutePath
    } catch (e: FileNotFoundException) {
        e.printStackTrace()
        return null
    } catch (e: IOException) {
        e.printStackTrace()
        return null
    } finally {
        fos.safeClose()
        ins.safeClose()
    }
}