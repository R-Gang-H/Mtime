package com.kotlin.android.image

import android.graphics.drawable.Drawable

/**
 * 创建者: zl
 * 创建时间: 2020/8/10 4:21 PM
 * 描述: 图片加载回调接口
 */
interface ImageCallback {
    fun onSuccess(drawable: Drawable?)
    fun onError(error: Drawable?) {}
    fun onStart(placeholder: Drawable?) {}
}