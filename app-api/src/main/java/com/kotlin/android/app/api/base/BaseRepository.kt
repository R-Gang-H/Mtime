package com.kotlin.android.app.api.base

import com.kotlin.android.api.ApiCode
import com.kotlin.android.api.ApiResponse
import com.kotlin.android.api.ApiResult
import com.kotlin.android.api.config.ApiConfig
import com.kotlin.android.app.api.ApiManager
import com.kotlin.android.app.api.api.*
import com.kotlin.android.core.NetState
import com.kotlin.android.ktx.ext.log.e
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.coroutineScope

/**
 *
 * Created on 2020-01-14.
 *
 * @author o.s
 */
open class BaseRepository {

    val api by lazy { ApiManager.api(Api::class.java) }
    val apiMisc by lazy { ApiManager.api(ApiMisc::class.java) }
    val apiCinema by lazy { ApiManager.api(ApiCinema::class.java) }
    val apiUser by lazy { ApiManager.api(ApiUser::class.java) }
    val apiTicket by lazy { ApiManager.api(ApiTicket::class.java) }
    val apiPayment by lazy { ApiManager.api(ApiPayment::class.java) }
    val apiCard by lazy { ApiManager.api(ApiCard::class.java) }
    val apiSnack by lazy { ApiManager.api(ApiSnack::class.java) }
    val apiCoupon by lazy { ApiManager.api(ApiCoupon::class.java) }
    val apiActivity by lazy { ApiManager.api(ApiActivity::class.java) }
    val apiMkt by lazy { ApiManager.api(ApiMkt::class.java) }
    val apiCount by lazy { ApiManager.api(ApiCount::class.java) }
    val apiMTime by lazy { ApiManager.api(ApiMTime::class.java) }
    val apiMTimeTicket by lazy { ApiManager.api(ApiMTimeTicket::class.java) }
    val apiMTimeLive by lazy { ApiManager.api(ApiMTimeLive::class.java) }

    /**
     * 发起请求接口服务
     * [converter]: 将成功的结果 [ApiResult.Success.data] 转换成任意（Any）目标 ViewBean
     * [errorMessage]: 自定义错误信息，当网络请求发生未知异常时，本地组装错误响应结果时使用。
     * [api]: Api调用返回原始数据 [ApiResponse]
     */
    suspend fun <R : Any, T : Any> request(
        converter: ((R) -> T?)? = null,
        errorMessage: String? = null,
        api: suspend () -> ApiResponse<R>
    ): ApiResult<T> {
        val apiResult = safeApiCall(errorMessage) {
            responseToResult(api())
        }
        return if (apiResult is ApiResult.Error) {
            apiResult
        } else {
            if (converter != null) {
                val result = converter((apiResult as ApiResult.Success<R>).data)
                if (null == result) {
                    ApiResult.Error(
                        ApiResult.ErrorCode.CONVERTER_ERROR,
                        ApiConfig.CONVERTER_CONTENT_ERROR
                    )
                } else {
                    ApiResult.Success(result)
                }
            } else {
                apiResult as? ApiResult.Success<T>
                    ?: ApiResult.Error(
                        ApiResult.ErrorCode.CONVERTER_ERROR,
                        ApiConfig.CONVERTER_TYPE_ERROR
                    )
            }
        }
    }

    /**
     * 安全的网络请求，捕获及处理相关异常
     */
    suspend fun <T : Any> safeApiCall(
        errorMessage: String? = null,
        call: suspend () -> ApiResult<T>
    ): ApiResult<T> {
        if (!NetState.isOk()) {
            return ApiResult.Error(ApiResult.ErrorCode.NET_ERROR, ApiConfig.NET_ERROR)
        }
        return try {
            call()
        } catch (e: Throwable) {
            e.printStackTrace()
            "safeApiCall $e".e()
            if (NetState.isOk()) {
                ApiResult.Error(ApiResult.ErrorCode.ERROR, errorMessage, e)
            } else {
                ApiResult.Error(ApiResult.ErrorCode.NET_ERROR, ApiConfig.NET_ERROR, e)
            }
        }
    }

    /**
     * 将接口返回的原始数据，进行第一次解析，剥离框架层
     */
    suspend fun <T : Any> responseToResult(
        response: ApiResponse<T>,
        successBlock: (suspend CoroutineScope.(data: T) -> Unit)? = null,
        errorBlock: (suspend CoroutineScope.() -> Unit)? = null
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

}