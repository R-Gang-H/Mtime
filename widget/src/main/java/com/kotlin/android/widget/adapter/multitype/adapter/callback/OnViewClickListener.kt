package com.kotlin.android.widget.adapter.multitype.adapter.callback

import android.view.View

/**
 * 创建者: zl
 * 创建时间: 2020/7/1 10:06 AM
 * 描述:
 */
interface OnViewClickListener {
    // 不需要额外参数事件时，默认转发给带额外参数事件
    fun onClick(view: View) {
        onClick(view, null)
    }

    fun onClick(view: View, any: Any?) {

    }
}