package com.kotlin.android.api

import com.kotlin.android.api.config.ApiConfig
import com.kotlin.android.core.NetState
import com.kotlin.android.ktx.ext.log.e
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.withContext

/**
 * 预处理响应结果：
 * 输入 [ApiResult]
 * 执行预先设定的调用过程
 * 输出你想要的任意（∀）类型，当然可以是多种、多个
 *
 * Created on 2020/4/27.
 *
 * @author o.s
 */
open class CallResult<T : Any> {
    val default = Dispatchers.Default
    val main = Dispatchers.Main
    val io = Dispatchers.IO

    var needLogin: ((message: String?) -> Unit)? = null
    var success: ((data: T) -> Unit)? = null
    var error: ((message: String?) -> Unit)? = null
    var netError: ((message: String) -> Unit)? = null
    var empty: (() -> Unit)? = null
    var loading: (() -> Unit)? = null
    var refresh: (() -> Unit)? = null
    var handleData: (CallResult<T>.(T) -> Unit)? = null

    fun refresh(refresh: (() -> Unit)? = null): CallResult<T> = apply {
        this.refresh = refresh
    }

    fun loading(loading: (() -> Unit)? = null): CallResult<T> = apply {
        this.loading = loading
    }

    fun netError(netError: ((message: String) -> Unit)? = null): CallResult<T> = apply {
        this.netError = netError
    }

    fun error(error: ((message: String?) -> Unit)? = null): CallResult<T> = apply {
        this.error = error
    }

    fun empty(empty: (() -> Unit)? = null): CallResult<T> = apply {
        this.empty = empty
    }

    fun success(success: ((data: T) -> Unit)? = null): CallResult<T> = apply {
        this.success = success
    }

    fun needLogin(needLogin: ((message: String?) -> Unit)? = null): CallResult<T> = apply {
        this.needLogin = needLogin
    }

    fun handleData(handleData: (CallResult<T>.(T) -> Unit)? = null): CallResult<T> = apply {
        this.handleData = handleData
    }

    /**
     * [errorMessage]: 当请求发生错误（不能正常响应）时会用到该错误提示语（需用户自定义），一般伴有异常抛出。
     *                 相反的，用户获得一个业务错误时一般不会有异常发生。
     */
    open suspend fun call(
        errorMessage: String? = null,
        successBlock: (suspend CoroutineScope.(data: T) -> Unit)? = null,
        errorBlock: (suspend CoroutineScope.() -> Unit)? = null,
        call: suspend () -> ApiResponse<T>
    ) {
        loading?.invoke()
        val result = withContext(io) {
            safeCall(errorMessage, successBlock, errorBlock, call)
        }
        checkResult(result)
    }

    private suspend fun safeCall(
        errorMessage: String? = null,
        successBlock: (suspend CoroutineScope.(data: T) -> Unit)? = null,
        errorBlock: (suspend CoroutineScope.() -> Unit)? = null,
        call: suspend () -> ApiResponse<T>
    ): ApiResult<T> {
        if (!NetState.isOk()) {
            return ApiResult.Error(ApiResult.ErrorCode.NET_ERROR, ApiConfig.NET_ERROR)
        }
        return try {
            converter(successBlock, errorBlock, call())
        } catch (e: Throwable) {
            e.printStackTrace()
            e.e()
            if (NetState.isOk()) {
                ApiResult.Error(ApiResult.ErrorCode.ERROR, errorMessage, e)
            } else {
                ApiResult.Error(ApiResult.ErrorCode.NET_ERROR, ApiConfig.NET_ERROR, e)
            }
        }
    }

    private suspend fun converter(
        successBlock: (suspend CoroutineScope.(data: T) -> Unit)? = null,
        errorBlock: (suspend CoroutineScope.() -> Unit)? = null,
        response: ApiResponse<T>
    ): ApiResult<T> {
        return coroutineScope {
            val code = response.code
            val message = response.msg
            when (code) {
                ApiCode.RESULT_OK -> {
                    val data = response.data
                    if (data != null) {
                        successBlock?.let { it(data) }
                        ApiResult.Success(data)
                    } else {
                        errorBlock?.let { it() }
                        ApiResult.Error(ApiResult.ErrorCode.EMPTY, message)
                    }
                }
                ApiCode.RESULT_LOGIN -> {
                    errorBlock?.let { it() }
                    ApiResult.Error(ApiResult.ErrorCode.NEED_LOGIN, message)
                }
                ApiCode.RESULT_LIMIT -> {
                    errorBlock?.let { it() }
                    ApiResult.Error(ApiResult.ErrorCode.RESULT_LIMIT, message)
                }
                else -> {
                    errorBlock?.let { it() }
                    ApiResult.Error(ApiResult.ErrorCode.ERROR, message)
                }
            }
        }
    }

    private fun checkResult(result: ApiResult<T>) {
        result.e()
        when (result) {
            is ApiResult.Success -> {
                handleData?.apply {
                    handleData?.invoke(this@CallResult, result.data)
                } ?: handleData(result.data)
            }
            is ApiResult.Error -> {
                val code = result.code
                val message = result.message
                when (code) {
                    ApiResult.ErrorCode.ERROR -> {
                        error?.invoke(message)
                    }
                    ApiResult.ErrorCode.NET_ERROR -> {
                        netError?.invoke(message.orEmpty())
                    }
                    ApiResult.ErrorCode.EMPTY -> {
                        empty?.invoke()
                    }
                    ApiResult.ErrorCode.NEED_LOGIN -> {
                        // 需要跳转到登录页
                        needLogin?.invoke(message)
                    }
                    ApiResult.ErrorCode.RESULT_LIMIT -> {
                        error?.invoke(message)
//                        toast(CoreApp.instance, message)
                    }
                }
            }
        }
    }

    /**
     * API成功响应数据 [ApiCode.RESULT_OK] 的默认实现：即直接调用 [success] 方法。
     * 也可根据实际也去要求，自定义实现。如 [bziCode] 相关业务逻辑
     */
    open fun handleData(data: T) {
        success?.invoke(data)
    }

    /**
     * 判空条件
     */
    private fun isEmpty(data: String?): Boolean = data == null || "{}" == data
}