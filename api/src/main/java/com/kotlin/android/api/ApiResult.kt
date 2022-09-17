package com.kotlin.android.api

/**
 * API响应目标对象
 *
 * Created on 2020-01-10.
 *
 * @author o.s
 */
sealed class ApiResult<out T : Any> {

    data class Success<out T : Any>(val data: T) : ApiResult<T>()

    data class Error(val code: ErrorCode, val message: String? = null, val error: Throwable? = null) : ApiResult<Nothing>()

    enum class ErrorCode {

        /**
         * 接口响应错误
         */
        ERROR,

        /**
         * 网络错误
         */
        NET_ERROR,

        /**
         * 接口响应数据为空
         */
        EMPTY,

        /**
         * 请求接口需要登录
         */
        NEED_LOGIN,

        /**
         * 请求受限的
         */
        RESULT_LIMIT,

        /**
         * 类型转换错误
         */
        CONVERTER_ERROR
    }
}