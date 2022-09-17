package com.kotlin.android.core.ext

import com.kotlin.android.core.CoreApp
import com.kotlin.android.ktx.ext.exitApp
import com.kotlin.android.ktx.ext.getAppInfo
import com.kotlin.android.ktx.ext.store.getSpValue
import com.kotlin.android.ktx.ext.store.putSpValue
import com.kotlin.android.ktx.ext.store.removeKey

/**
 *
 * Created on 2020/6/10.
 *
 * @author o.s
 */

val versionName
    get() = CoreApp.instance.getAppInfo().versionName

val versionCode
    get() = CoreApp.instance.getAppInfo().versionCode

fun <T> getSpValue(key: String, default: T): T {
    return CoreApp.instance.getSpValue(key, default)
}

fun <T> putSpValue(key: String, value: T) {
    CoreApp.instance.putSpValue(key, value)
}

fun removeSpKey(key: String) {
    CoreApp.instance.removeKey(key = key)
}

/**
 * 退出当前 app
 */
fun exitApp() {
    CoreApp.instance.exitApp()
}