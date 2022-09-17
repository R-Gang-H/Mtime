package com.kotlin.android.ktx.ext.uri

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
import java.io.*

/**
 * Uri相关扩展:
 *
 * Created on 2021/3/9.
 *
 * @author o.s
 */

const val PATH_DOCUMENT = "document"
const val PATH_CONTENT = "content"
const val PATH_FILE = "file"
const val PATH_EXTERNAL_STORAGE_DOCUMENT = "com.android.externalstorage.documents"
const val PATH_DOWNLOADS_DOCUMENT = "com.android.providers.downloads.documents"
const val PATH_MEDIA_DOCUMENT = "com.android.providers.media.documents"
const val PATH_GOOGLE_PHOTOS = "com.google.android.apps.photos.content"

/**
 * Uri是否是文档资源
 */
val Uri.isDocumentUri: Boolean
    get() {
        val paths = pathSegments
        if ((paths?.size ?: 0) < 2) {
            return false
        }
        if (PATH_DOCUMENT != paths[0]) {
            return false
        }
        return true
    }

val Uri.documentId: String?
    get() = if (isDocumentUri) {
        pathSegments[1]
    } else {
        null
    }

/**
 * Uri是否是Content资源
 */
val Uri.isContentUri: Boolean
    get() = PATH_CONTENT.equals(scheme, true)

/**
 * Uri是否是文件资源
 */
val Uri.isFileUri: Boolean
    get() = PATH_FILE.equals(scheme, true)

val Uri.isExternalStorageDocument: Boolean
    get() = authority == PATH_EXTERNAL_STORAGE_DOCUMENT
val Uri.isDownloadsDocument: Boolean
    get() = authority == PATH_DOWNLOADS_DOCUMENT
val Uri.isMediaDocument: Boolean
    get() = authority == PATH_MEDIA_DOCUMENT
val Uri.isGooglePhotosUri: Boolean
    get() = authority == PATH_GOOGLE_PHOTOS

/**
 * 授予 Content Uri 临时读访问权限
 */
fun Uri.grantReadUriPermission(
    context: Context,
    modeFlags: Int = Intent.FLAG_GRANT_READ_URI_PERMISSION
) {
    context.grantUriPermission(context.packageName, this, modeFlags)
}
/**
 * 授予 Content Uri 临时读写访问权限
 */
fun Uri.grantUriPermission(
    context: Context,
    modeFlags: Int = Intent.FLAG_GRANT_READ_URI_PERMISSION or Intent.FLAG_GRANT_WRITE_URI_PERMISSION
) {
    context.grantUriPermission(context.packageName, this, modeFlags)
}

/**
 * 获取 Content Uri
 */
fun Intent?.getContentUri(context: Context): Uri? {
    return this?.run {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            data?.getPath(context)?.run {
                FileProvider.getUriForFile(context, "${context.applicationContext.packageName}.fileprovider", File(this))
            }
        } else {
            // android7.0以下不支持content://
            data
        }
    }
}

/**
 * 获取 Uri 资源描述的文件路径
 */
fun Uri.getPath(context: Context): String? {
    val isKitKat = Build.VERSION.SDK_INT >= 19
    val isQ = Build.VERSION.SDK_INT >= 29

    if (isQ && isDocumentUri) {
        // document
        return getPathForUriDocument(context)
    }

    var path: String? = null
    if (isKitKat && isDocumentUri) {
        when {
            isExternalStorageDocument -> {
                // ExternalStorageProvider
                documentId?.run {
                    val split = split(":")
                    if ("primary" == split[0]) {
                        path = "${Environment.getExternalStorageDirectory()}/${split[1]}"
                    }
                }
            }
            isDownloadsDocument -> {
                // DownloadsProvider
                documentId?.run {
                    val contentUri = ContentUris.withAppendedId(this@getPath, toLong())
                    path = contentUri.getDataColumn(context, null, null)
                }
            }
            isMediaDocument -> {
                // MediaProvider
                documentId?.run {
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
                        path = getDataColumn(context, selection, selectionArgs)
                    }
                }
            }
        }
    } else if (isContentUri) {
        path = if (isGooglePhotosUri) {
            lastPathSegment
        } else {
            getDataColumn(context, null, null)
        }
    } else if (isFileUri) {
        path = this.path
    }

    return path
}

/**
 * 解析文件路径
 */
fun Uri.getDataColumn(context: Context, selection: String?, selectionArgs: Array<String>?): String? {
    var cursor: Cursor? = null
    val column = MediaStore.Images.ImageColumns.DATA //"_data"
    val projection = arrayOf(column)
    return try {
        cursor = context.contentResolver.query(this, projection, selection, selectionArgs, null)
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
fun Uri.getPathForUriDocument(context: Context): String? {
    var fos: FileOutputStream? = null
    var ins: InputStream? = null
    try {
        ins = context.contentResolver.openInputStream(this)
        if (ins == null) {
            "openInputStream from uri is null".e()
            return null
        }

        val fileName = path?.run {
            substring(lastIndexOf("/") + 1, length)
        } ?: "${System.currentTimeMillis()}.jpg"

        val file = File(context.getExternalFilesDir("Images"), fileName)
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
