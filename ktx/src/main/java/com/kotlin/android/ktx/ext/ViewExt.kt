package com.kotlin.android.ktx.ext

import android.graphics.Outline
import android.view.View
import android.view.ViewOutlineProvider

/**
 * 设置view的圆角
 */
fun View?.setRoundCorners(radius: Float) {
    this?.apply {
        outlineProvider = object : ViewOutlineProvider() {
            override fun getOutline(view: View, outline: Outline) {
                outline.setRoundRect(0, 0, view.width, view.height, radius)
            }
        }
        clipToOutline = true
    }
}