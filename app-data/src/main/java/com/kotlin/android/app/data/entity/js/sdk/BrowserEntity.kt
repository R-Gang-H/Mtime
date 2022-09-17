package com.kotlin.android.app.data.entity.js.sdk

import com.kotlin.android.app.data.ProguardRule
import java.io.Serializable

/**
 * 创建者: zl
 * 创建时间: 2020/8/3 12:48 PM
 * 描述:标题，url……
 */
data class BrowserEntity(
        var title: String? = "",
        var url: String,
        var showCustomTitle:Boolean = false
) : Serializable, ProguardRule
