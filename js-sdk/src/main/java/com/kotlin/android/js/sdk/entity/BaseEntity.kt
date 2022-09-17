package com.kotlin.android.js.sdk.entity

import com.kotlin.android.app.data.ProguardRule

/**
 * 创建者: zl
 * 创建时间: 2020/12/9 1:46 下午
 * 描述:
 */
open class BaseEntity(
        var code: Int = 0,
        var success: Boolean = true,
        var tokenKey: String? = null,
        var methodName: String? = null,
        var callBackMethod: String? = null,
        var data: Any? = null
) : ProguardRule