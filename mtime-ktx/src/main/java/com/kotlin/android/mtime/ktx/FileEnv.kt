package com.kotlin.android.mtime.ktx

import android.content.ContextWrapper
import android.os.Environment
import com.kotlin.android.core.CoreApp
import com.kotlin.android.ktx.ext.log.e
import com.kotlin.android.ktx.ext.io.photoDir
import com.kotlin.android.ktx.ext.io.picDir
import com.kotlin.android.ktx.ext.io.screenShotsDir
import java.io.File

/**
 * 时光网文件系统环境：
 *
 * 缓存文件：
 * /时光网/
 * /时光网/cache/
 * /时光网/cache_data/
 * /时光网/camera_pic/
 * /时光网/tem_pic/
 *
 * picDir = /storage/emulated/0/Android/data/com.mtime/files/Pictures
 * screenShotsDir = /storage/emulated/0/Android/data/com.mtime/files/Screenshots
 * cacheDir = /data/user/0/com.mtime/cache
 * externalCacheDir = /storage/emulated/0/Android/data/com.mtime/cache
 * cacheMTimeShareImageDir = /storage/emulated/0/Android/data/com.mtime/cache/时光网/cache/temp_share_image
 * downloadFilmDir = /storage/emulated/0/时光网/
 * cacheMTimeDir = /storage/emulated/0/Android/data/com.mtime/cache/时光网/cache
 * photoDir.size = 0
 * downloadImageDir = /storage/emulated/0/Pictures/MTime/MTime_Images
 * Created on 2020/7/24.
 *
 * @author o.s
 */
object FileEnv {
    val photoDir: File? = CoreApp.instance.photoDir
    val picDir: File? = CoreApp.instance.picDir
    val screenShotsDir: File? = CoreApp.instance.screenShotsDir
    val cacheDir: File? = CoreApp.instance.cacheDir
    val externalCacheDir: File? = CoreApp.instance.externalCacheDir
    val externalCacheShareImageDir: File? = CoreApp.instance.externalCacheDir?.run { File(this, "share_image") }
    /**Android11，下载的图片必须保存在系统专用子目录里，参考微信，放在Pictures文件夹下
     * https://developer.android.google.cn/training/data-storage/use-cases#migrate-legacy-storage
     * */
    private val downloadDir = Environment.getExternalStorageDirectory().path + File.separator + Environment.DIRECTORY_PICTURES + "/MTime/"
    val downloadImageDir = "${downloadDir}MTime_Images"
    val downDir = "${ContextWrapper(CoreApp.instance.applicationContext).getExternalFilesDir(Environment.DIRECTORY_DOWNLOADS)}" + File.separator//Android/data/com.mtime/files/Download/
    val headDownPic = "${ContextWrapper(CoreApp.instance.applicationContext).getExternalFilesDir(Environment.DIRECTORY_PICTURES)}/MTime/MTime_Images"
    fun log() {
        "photoDir = $photoDir\npicDir = $picDir\nscreenShotsDir = ${screenShotsDir}\ncacheDir = $cacheDir\nexternalCacheDir = $externalCacheDir\ndownloadFilmDir = $downloadImageDir\nexternalCacheShareImageDir = $externalCacheShareImageDir\nphotoDir.size = ${photoDir?.listFiles()?.size}".e()
    }

    val appDir = CoreApp.instance.filesDir.path + File.separator + "MTime" + File.separator
    val seatIconsDownloadDir = appDir + "SeatIcons" + File.separator
}