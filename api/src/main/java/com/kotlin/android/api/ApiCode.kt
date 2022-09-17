package com.kotlin.android.api

/**
 *
 * Created on 2020-01-10.
 *
 * @author o.s
 */
object ApiCode {
    const val RESULT_OK = 0 // 正常状态 0
    const val RESULT_LOGIN = -1 // 未登录状态
    const val RESULT_LIMIT = -100 // 限流状态
    const val RESULT_SERVER_ERROR = 1 // 服务器错误状态
}