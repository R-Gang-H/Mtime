package com.kotlin.android.ktx.bean

import android.graphics.drawable.Drawable

/**
 *
 * Created on 2020/6/4.
 *
 * @author o.s
 */
data class AppInfo(
    val apkPath: String,
    val packageName: String,
    val versionName: String,
    val versionCode: Long,
    val appName: String,
    val appIcon: Drawable
)