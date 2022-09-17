package com.kotlin.android.ktx.ext.core

import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.os.Parcelable
import android.util.ArrayMap
import androidx.core.content.FileProvider
import com.kotlin.android.ktx.ext.uri.getPath
import java.io.File

/**
 * 创建者: zl
 * 创建时间: 2020/6/10 6:16 PM
 * 描述:Intent扩展，vararg为可变参数
 */
fun Intent.putExtras(vararg extras: Pair<String, Any>): Intent {
    extras.forEach { (key, value) ->
        handleIntent(key, value)
    }
    return this
}

fun Intent.putExtras(extras: ArrayMap<String, Any>): Intent {
    extras.forEach { (key, value) ->
        handleIntent(key, value)
    }
    return this
}

private fun Intent.handleIntent(key: String, value: Any?) {
    when (value) {
        is Int -> putExtra(key, value)
        is Byte -> putExtra(key, value)
        is Char -> putExtra(key, value)
        is Long -> putExtra(key, value)
        is Float -> putExtra(key, value)
        is Short -> putExtra(key, value)
        is Double -> putExtra(key, value)
        is Boolean -> putExtra(key, value)
        is Bundle -> putExtra(key, value)
        is String -> putExtra(key, value)
        is IntArray -> putExtra(key, value)
        is ByteArray -> putExtra(key, value)
        is CharArray -> putExtra(key, value)
        is LongArray -> putExtra(key, value)
        is FloatArray -> putExtra(key, value)
        is ShortArray -> putExtra(key, value)
        is DoubleArray -> putExtra(key, value)
        is BooleanArray -> putExtra(key, value)
        is CharSequence -> putExtra(key, value)
        is Parcelable -> putExtra(key, value)
        is Array<*> -> {
            when {
                value.isArrayOf<String>() ->
                    putExtra(key, value as Array<String?>)
                value.isArrayOf<Parcelable>() ->
                    putExtra(key, value as Array<Parcelable?>)
                value.isArrayOf<CharSequence>() ->
                    putExtra(key, value as Array<CharSequence?>)
                else -> putExtra(key, value)
            }
        }
    }
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
