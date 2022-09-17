package com.kotlin.android.ktx.ext.core

import android.app.Activity
import android.content.Intent
import android.net.Uri
import android.os.Build
import androidx.core.content.FileProvider
import java.io.File

/**
 * Activity相关扩展:
 * 大部分内容在 ContextExt 扩展中
 *
 * Created on 2021/3/09.
 *
 * @author o.s
 */


/**
 * Activity的亮度（0-1F）
 */
var Activity.brightness
    set(value) {
        window.attributes = window.attributes.apply {
            screenBrightness = value
        }
    }
    get() = window.attributes.screenBrightness

/**
 * 受保护的Dialog
 */
fun Activity?.showSafeDialog(method: () -> Unit) {
    if (this == null) return
    if (isFinishing || isDestroyed) {
        return
    }
    method()
}

/**
 * 打开图片选择器（本地图库）
 * 回调处理参见 [Activity.onActivityResultLocaleImageData]
 */
fun Activity.startPickLocaleImage(title: String = "本地图片") {
    val intent = Intent()
    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
        intent.action = Intent.ACTION_OPEN_DOCUMENT
    } else {
        intent.action = Intent.ACTION_GET_CONTENT
    }
    intent.addCategory(Intent.CATEGORY_OPENABLE)
    intent.type = "image/*"
    startActivityForResult(Intent.createChooser(intent, title), 10002)
}

/**
 * 选中图片回调处理
 * 需要在使用图片选择器的页面，在 [Activity.onActivityResult] 中调用一下该方法
 * 打开图片选择器参见 [Activity.startPickLocaleImage]
 */
fun Activity.onActivityResultLocaleImageData(requestCode: Int, resultCode: Int, data: Intent, complete: (path: String?) -> Unit) {
    if (resultCode == Activity.RESULT_OK) {
        if (requestCode == 10002) {
            val path = getContentUri(data)?.run {
                getPath(this)
            }
            complete.invoke(path)
        }
    }
}

fun Activity.getContentUri(intent: Intent?): Uri? {
    // android7.0以下不支持content://
    if (Build.VERSION.SDK_INT < Build.VERSION_CODES.N) {
        return intent?.data
    }
    return intent?.data?.run {
        // 先转存到沙盒目录下
        getPath(this)?.run {
            FileProvider.getUriForFile(this@getContentUri, "${application.packageName}.fileprovider", File(this))
        }
    }
}