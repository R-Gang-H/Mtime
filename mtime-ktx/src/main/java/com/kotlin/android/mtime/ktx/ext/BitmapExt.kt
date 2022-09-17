package com.kotlin.android.mtime.ktx.ext

import android.graphics.Bitmap
import com.kotlin.android.ktx.ext.io.generateFileName
import com.kotlin.android.ktx.ext.io.safeClose
import com.kotlin.android.mtime.ktx.FileEnv
import java.io.File
import java.io.FileOutputStream

/**
 *
 * Created on 2020/11/6.
 *
 * @author o.s
 */

/**
 * 分享图片保存到缓存存储器中
 */
fun Bitmap.saveShareImage(
        imageFormat: String? = null,
        isRecycle: Boolean = false
): String? {
    return FileEnv.externalCacheShareImageDir?.run {
        if (!exists()) {
            mkdirs()
        }
        val file: File
        val compressFormat = if (imageFormat == "png") {
            file = File(this, "${generateFileName()}.png")
            Bitmap.CompressFormat.PNG
        } else {
            file = File(this, "${generateFileName()}.jpg")
            Bitmap.CompressFormat.JPEG
        }
        FileOutputStream(file).run {
            compress(compressFormat, 85, this)
            flush()
            safeClose()
            if (isRecycle && !isRecycled) {
                recycle()
            }
            file.path
        }
    }
}