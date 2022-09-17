package com.kotlin.android.ktx.ext

import android.content.Context
import android.content.pm.PackageManager
import android.os.Build
import android.os.Environment
import android.os.Process
import com.kotlin.android.ktx.bean.AppInfo
import com.kotlin.android.ktx.bean.AppState
import com.kotlin.android.ktx.ext.core.activityManager
import com.kotlin.android.ktx.lifecycle.KtxActivityManager
import java.io.File
import java.lang.Exception
import java.util.*
import kotlin.collections.ArrayList
import kotlin.system.exitProcess

/**
 *
 * Created on 2020/4/20.
 *
 * @author o.s
 */

var appState: AppState = AppState.NOT_RUNNING
var appInfo: AppInfo? = null

fun getUUID(): String {
    return UUID.randomUUID().toString().replace(regex = "-".toRegex(), replacement = "")
        .toLowerCase(
            Locale.getDefault()
        )
}

val Context.versionName: String
    get() = if (appInfo != null) {
        appInfo?.versionName ?: "1.0"
    } else {
        appInfo = getAppInfo()
        appInfo?.versionName ?: "1.0"
    }

val Context.versionCode: Long
    get() = if (appInfo != null) {
        appInfo?.versionCode ?: 1L
    } else {
        appInfo = getAppInfo()
        appInfo?.versionCode ?: 1L
    }

/**
 * 当前进程名称
 */
val Context.processName: String
    get() {
        activityManager?.runningAppProcesses?.forEach {
            if (it.pid == Process.myPid()) {
                return it.processName
            }
        }
        return ""
    }

/**
 * 检查是否安装了sd卡
 */
val sdcardMounted: Boolean
    get() {
        val state = Environment.getExternalStorageState()
        return state == Environment.MEDIA_MOUNTED && state != Environment.MEDIA_MOUNTED_READ_ONLY
    }

/**
 * 获取app信息
 */
fun Context.getAppInfo(pkgName: String = packageName): AppInfo {
    val pkgInfo = packageManager.getPackageInfo(packageName, PackageManager.GET_META_DATA)
    pkgInfo.apply {
        val apkPath = applicationInfo.sourceDir
        val versionCode = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.P) {
            longVersionCode
        } else {
            versionCode.toLong()
        }
        val appName = packageManager.getApplicationLabel(applicationInfo).toString()
        val appIcon = packageManager.getApplicationIcon(applicationInfo)
        return AppInfo(apkPath, packageName, versionName, versionCode, appName, appIcon)
    }
}

/**
 * 获取app信息（根据apk文件路径）
 */
fun Context.getAppInfoByApkPath(apkPath: String): AppInfo? {
    val pkgInfo = packageManager.getPackageArchiveInfo(apkPath, PackageManager.GET_META_DATA)
    if (pkgInfo != null) {
        pkgInfo.apply {
            applicationInfo.sourceDir = apkPath
            applicationInfo.publicSourceDir = apkPath
            val versionCode = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.P) {
                longVersionCode
            } else {
                versionCode.toLong()
            }
            val appName = packageManager.getApplicationLabel(applicationInfo).toString()
            val appIcon = packageManager.getApplicationIcon(applicationInfo)
            return AppInfo(apkPath, packageName, versionName, versionCode, appName, appIcon)
        }
    } else {
        return null
    }
}

/**
 * 获取app安装目录下的所有app安装包信息（指定apk安装目录）
 */
fun Context.getAppInfoList(apkFolderPath: String): List<AppInfo?> {
    val list = ArrayList<AppInfo?>()
    File(apkFolderPath).listFiles()?.forEach {
        list.add(getAppInfoByApkPath(it.path))
    }
    return list
}

/**
 * 获取app签名
 */
fun Context.getAppSignature(pkfName: String = packageName): ByteArray? {
    val pkgInfo = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.P) {
        packageManager.getPackageInfo(pkfName, PackageManager.GET_SIGNING_CERTIFICATES)
    } else {
        packageManager.getPackageInfo(pkfName, PackageManager.GET_SIGNATURES)
    }
    val signatures = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.P) {
        val signing = pkgInfo.signingInfo
        signing.apkContentsSigners
    } else {
        pkgInfo.signatures
    }
    return if (signatures.isNotEmpty()) {
        signatures[0].toByteArray()
    } else {
        null
    }
}

/**
 * 判断application是否安装
 */
fun Context.getAppInstalled(pkgName: String): Boolean {
    return try {
        packageManager.getPackageInfo(pkgName, 0)
        true
    } catch (e: Exception) {
        false
    }
}

fun Context.appState(): AppState {
    // TODO 实现方式？
    return appState
}

/**
 * 退出app（当前Context所在的app）
 */
fun Context.exitApp() {
    KtxActivityManager.exitApp()
//    try {
//        activityManager?.killBackgroundProcesses(packageName)
//        exitProcess(0)
//    } catch (e: Throwable) {
//        exitProcess(1)
//    } finally {
//        System.gc()
//    }
}