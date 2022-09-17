package com.kotlin.android.ktx.ext.core

import android.os.Bundle
import android.os.Parcelable
import java.io.Serializable

/**
 * 创建者: zl
 * 创建时间: 2020/6/8 2:39 PM
 * 描述: Map数据转为Bundle,方便ARouter跳转传递
 */
fun Bundle.put(params: Map<String, Any>): Bundle {
    params.forEach {
        val key = it.key
        when (val value = it.value) {
            is Int -> putInt(key, value)
            is IntArray -> putIntArray(key, value)
            is Long -> putLong(key, value)
            is LongArray -> putLongArray(key, value)
            is CharSequence -> putCharSequence(key, value)
            is String -> putString(key, value)
            is Float -> putFloat(key, value)
            is FloatArray -> putFloatArray(key, value)
            is Double -> putDouble(key, value)
            is DoubleArray -> putDoubleArray(key, value)
            is Char -> putChar(key, value)
            is CharArray -> putCharArray(key, value)
            is Short -> putShort(key, value)
            is ShortArray -> putShortArray(key, value)
            is Boolean -> putBoolean(key, value)
            is BooleanArray -> putBooleanArray(key, value)
            is Serializable -> putSerializable(key, value)
            is Parcelable -> putParcelable(key, value)
            is Bundle -> putAll(value)
            is Array<*> -> when {
                value.isArrayOf<Parcelable>() ->
                    putParcelableArray(
                    key,
                    value as Array<out Parcelable>?)
            }
        }
    }
    return this
}

fun Bundle.put(key: String, value: Any?): Bundle {
    when (value) {
        is Int -> putInt(key, value)
        is IntArray -> putIntArray(key, value)
        is Long -> putLong(key, value)
        is LongArray -> putLongArray(key, value)
        is CharSequence -> putCharSequence(key, value)
        is String -> putString(key, value)
        is Float -> putFloat(key, value)
        is FloatArray -> putFloatArray(key, value)
        is Double -> putDouble(key, value)
        is DoubleArray -> putDoubleArray(key, value)
        is Char -> putChar(key, value)
        is CharArray -> putCharArray(key, value)
        is Short -> putShort(key, value)
        is ShortArray -> putShortArray(key, value)
        is Boolean -> putBoolean(key, value)
        is BooleanArray -> putBooleanArray(key, value)
        is Serializable -> putSerializable(key, value)
        is Parcelable -> putParcelable(key, value)
        is Bundle -> putAll(value)
        is Array<*> -> when {
            value.isArrayOf<Parcelable>() ->
                putParcelableArray(
                    key,
                    value as Array<out Parcelable>?)
        }
    }
    return this
}