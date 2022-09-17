package com.kotlin.android.bind.event

import android.view.View

/**
 * 点击事件对象（包含View 和 数据）
 *
 * Created on 2021/7/20.
 *
 * @author o.s
 */
data class Action<VH>(
    val view: View,
    val holder: VH, // ViewHolder: (binder, binding, data, mPosition)
)
