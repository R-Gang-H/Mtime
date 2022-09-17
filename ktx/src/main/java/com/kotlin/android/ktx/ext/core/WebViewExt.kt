package com.kotlin.android.ktx.ext.core

import android.content.Context
import android.os.Build
import android.webkit.WebView
import com.kotlin.android.ktx.ext.processName

/**
 * WebView相关扩展：
 *
 * Created on 2021/3/23.
 *
 * @author o.s
 */

/**
 * 如果在api设置为28后，多进程使用同一个目录 WebView，
 * 为不同的进程设置不同的 WebView 数据目录。
 *
 * https://www.jianshu.com/p/7279e36d932b
 */
fun Context.setWebViewDataDirectory() {
    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.P) {
        val pName = processName
        if (packageName != pName) {
            WebView.setDataDirectorySuffix(pName)
        }
    }
}
