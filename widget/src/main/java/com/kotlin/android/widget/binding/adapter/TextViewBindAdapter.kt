package com.kotlin.android.widget.binding.adapter

import android.graphics.Typeface
import android.widget.TextView
import androidx.databinding.BindingAdapter

/**
 * 创建者: zl
 * 创建时间: 2020/6/18 2:34 PM
 * 描述: TextView绑定事件
 */
@BindingAdapter("textStyle")
fun TextView.setTypeface(style: String) {
    when (style) {
        "bold" -> setTypeface(null, Typeface.BOLD)
        "italic" -> setTypeface(null, Typeface.ITALIC)
        "bold|italic" -> setTypeface(null, Typeface.BOLD_ITALIC)
        else -> setTypeface(null, Typeface.NORMAL)
    }
}