package com.kotlin.android.ktx.ext

/**
 * @author ZhouSuQiang
 * @email zhousuqiang@126.com
 * @date 2021/6/2
 */


public inline fun Double?.orZero(): Double = this ?: 0.0

public inline fun Long?.orZero(): Long = this ?: 0L

public inline fun Float?.orZero(): Float = this ?: 0.0f

public inline fun Int?.orZero(): Int = this ?: 0

public inline fun Boolean?.orFalse(): Boolean = this ?: false