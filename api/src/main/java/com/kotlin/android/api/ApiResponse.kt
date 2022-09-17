package com.kotlin.android.api

/**
 * API响应标准对象
 *
 * Created on 2020-01-10.
 *
 * @author o.s
 */
data class ApiResponse<T>(
        var code: Int = -0,
        var msg: String? = null,
        var data: T? = null
//        var errorMessage: String? = null
)