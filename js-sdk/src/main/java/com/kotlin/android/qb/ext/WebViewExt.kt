package com.kotlin.android.qb.ext

import android.Manifest
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.net.Uri
import android.os.Environment
import androidx.fragment.app.FragmentActivity
import com.kotlin.android.js.sdk.R
import com.kotlin.android.ktx.ext.core.savePic
import com.kotlin.android.ktx.ext.core.webData2Bitmap
import com.kotlin.android.ktx.ext.getSuffixName
import com.kotlin.android.ktx.ext.io.safeClose
import com.kotlin.android.ktx.ext.permission.permissions
import com.kotlin.android.ktx.utils.LogUtils
import com.kotlin.android.mtime.ktx.FileEnv
import com.kotlin.android.mtime.ktx.ext.showToast
import com.tencent.smtt.sdk.URLUtil
import com.tencent.smtt.sdk.WebView
import java.io.*
import java.net.HttpURLConnection
import java.net.URL
import java.net.URLConnection
import kotlin.concurrent.thread

/**
 * create by lushan on 2021/11/11
 * des:
 **/

fun WebView.saveBitmapFromWebView(context:FragmentActivity){
    val hitTestResult = this.hitTestResult
    // 如果是图片类型或者是带有图片链接的类型
    when (hitTestResult.type) {
        WebView.HitTestResult.IMAGE_TYPE,
        WebView.HitTestResult.SRC_ANCHOR_TYPE -> {
            context.checkStoragePermission(hitTestResult.extra)
        }
    }

}

private fun FragmentActivity.checkStoragePermission(extra: String?) {
    runOnUiThread {
        permissions(
            Manifest.permission.READ_EXTERNAL_STORAGE,
            Manifest.permission.WRITE_EXTERNAL_STORAGE,
        ) {
            onGranted {
                thread {
                    if (!extra.isNullOrEmpty()) {
                        if (URLUtil.isValidUrl(extra)) {
                            saveBitmapFromNet(extra)
                        } else {
                            saveImage(extra)
                        }
                    }

                }
            }
            onDenied {
                showToast(getString(R.string.js_sdk_save_fail))
            }
        }
    }

}

private fun FragmentActivity.saveImage(extra: String?) {
    extra?.apply {
        val extras = extra.split(",")
        if (extras.size >= 2) {
            val bitmap = webData2Bitmap(extras[1])
            bitmap.savePic(this@saveImage, Environment.getExternalStorageDirectory().path)
                .apply {
                    runOnUiThread {
                        if (this) {
                            showToast(getString(R.string.js_sdk_save_success))
                        } else {
                            showToast(getString(R.string.js_sdk_save_fail))
                        }
                    }
                }
        }
    }
}

/**
 * 保存图片到本地，url或者base64
 */
/***********************************/

private fun FragmentActivity.saveBitmapFromNet(url:String){
    val bm: Bitmap?
    var inputStream: InputStream? = null
    var bis: BufferedInputStream? = null
    try {
        val iconUrl = URL(url)
        val conn: URLConnection = iconUrl.openConnection()
        val http: HttpURLConnection = conn as HttpURLConnection
        val length: Int = http.contentLength
        conn.connect()
        // 获得图像的字符流
        inputStream = conn.getInputStream()
        bis = BufferedInputStream(inputStream, length)
        bm = BitmapFactory.decodeStream(bis)
        bis.close()
        inputStream.close()
        bm?.let { save2Album(it, getSuffixName(url)) }
    } catch (e: java.lang.Exception) {
        runOnUiThread { showToast("保存失败") }
        LogUtils.e("url2bitmap $e")
    } finally {
        bis.safeClose()
        inputStream.safeClose()
    }
}

private fun FragmentActivity.save2Album(bitmap: Bitmap, suffix: String) {
    val appDir = File(FileEnv.downloadImageDir)
    if (!appDir.exists()) appDir.mkdir()
    val fileName = "${System.currentTimeMillis()}$suffix"
    val file = File(appDir, fileName)
    var fos: FileOutputStream? = null
    try {
        fos = FileOutputStream(file)
        bitmap.compress(Bitmap.CompressFormat.JPEG, 100, fos)
        fos.flush()
        fos.close()
        /**
         * 给系统发广播，刷新多媒体库
         */
        runOnUiThread {
            sendBroadcast(Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE, Uri.fromFile(file)))
            showToast("保存成功")
        }
    } catch (e: IOException) {
        runOnUiThread {
            showToast("保存失败")
        }
        LogUtils.e("save2Album $e")
    } finally {
        fos.safeClose()
    }
}