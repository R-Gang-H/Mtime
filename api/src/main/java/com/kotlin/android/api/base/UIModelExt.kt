package com.kotlin.android.api.base

import com.kotlin.android.api.ApiResult
import com.kotlin.android.mtime.ktx.ext.showToast
import com.kotlin.android.router.RouterManager
import com.kotlin.android.router.provider.IUserProvider

/**
 * UIModel扩展
 *
 * Created on 2021/12/2.
 *
 * @author o.s
 */

/**
 * 【并行/串行】合并多个接口，检查API结果并通知UI刷新
 * [isRefresh]: 默认为 true 刷新接口，加载更多时需要明确指定为 false
 * [isEmpty]: 自定义判空条件，返回true表示结果为空。默认非空处理。
 * 设置 [hasMore] 将启动分页管理：[BaseUIModel] 内置自增 [BaseUIModel.pageIndex]，需要时根据 [pageStamp] 同步 [BaseUIModel.pageStamp]。
 * 外部接口调用时，直接使用分页相关字段即可。可通过 [BaseUIModel.initPageSize] 设置分页大小（建议在声明uiModel时执行）
 */
fun <T : Any> BaseUIModel<T>.mergeEmitUIState(
    mainApiResult: ApiResult<*>? = null,
    successData: T? = null,
    isRefresh: Boolean = true,
    isEmpty: ((success: T) -> Boolean)? = null,
    hasMore: ((success: T) -> Boolean)? = null,
    pageStamp: ((success: T) -> String?)? = null,
) {
    mergeCheckResult(
        mainApiResult = mainApiResult,
        successData = successData,
        isEmpty = isEmpty,
        empty = {
            emitUIState(isRefresh = isRefresh, noMoreData = true, isEmpty = true)
        },
        error = {
            emitUIState(isRefresh = isRefresh, noMoreData = isRefresh, error = it)
        },
        netError = {
            emitUIState(isRefresh = isRefresh, noMoreData = isRefresh, netError = it)
        },
        needLogin = {
            emitUIState(isRefresh = isRefresh, needLogin = true)
        },
        success = {
            if (hasMore != null) {
                val isMore = hasMore(it)
                nextPage(pageStamp?.invoke(it))
                emitUIState(
                    isRefresh = isRefresh,
                    loadMore = isMore,
                    noMoreData = !isMore,
                    success = it,
                )
            } else {
                emitUIState(
                    success = it,
                    isRefresh = isRefresh
                )
            }
        }
    )
}

/**
 * 【并行/串行】合并多个接口，检查API结果并通知UI刷新
 * [isRefresh]: 默认为 true 刷新接口，加载更多时需要明确指定为 false
 * [isEmpty]: 自定义判空条件，返回true表示结果为空。默认非空处理。
 * 设置 [hasMore] 将启动分页管理：[BaseUIModel] 内置自增 [BaseUIModel.pageIndex]，需要时根据 [pageStamp] 同步 [BaseUIModel.pageStamp]。
 * 外部接口调用时，直接使用分页相关字段即可。可通过 [BaseUIModel.initPageSize] 设置分页大小（建议在声明uiModel时执行）
 */
fun <T : Any, B : Any> BinderUIModel<T, B>.mergeEmitUIState(
    mainApiResult: ApiResult<*>? = null,
    successData: T? = null,
    isRefresh: Boolean = true,
    isEmpty: ((success: T) -> Boolean)? = null,
    hasMore: ((success: T) -> Boolean)? = null,
    pageStamp: ((success: T) -> String?)? = null,
    binders: B? = null, // 子线程调用转换方法
) {
    mergeCheckResult(
        mainApiResult = mainApiResult,
        successData = successData,
        isEmpty = isEmpty,
        empty = {
            emitUIState(isRefresh = isRefresh, noMoreData = true, isEmpty = true, binders = null)
        },
        error = {
            emitUIState(isRefresh = isRefresh, noMoreData = isRefresh, error = it, binders = null)
        },
        netError = {
            emitUIState(isRefresh = isRefresh, noMoreData = isRefresh, netError = it, binders = null)
        },
        needLogin = {
            emitUIState(isRefresh = isRefresh, needLogin = true, binders = null)
        },
        success = {
            this.binders = binders
            if (hasMore != null) {
                val isMore = hasMore(it)
                nextPage(pageStamp?.invoke(it))
                emitUIState(
                    isRefresh = isRefresh,
                    loadMore = isMore,
                    noMoreData = !isMore,
                    success = it,
                    binders = binders,
                )
            } else {
                emitUIState(
                    success = it,
                    binders = binders,
                    isRefresh = isRefresh
                )
            }
        }
    )
}

/**
 * 【并行/串行】合并多个接口，检查API结果
 */
fun <T : Any> mergeCheckResult(
    mainApiResult: ApiResult<*>? = null,
    successData: T? = null,
    isEmpty: ((success: T) -> Boolean)? = null,
    empty: (() -> Unit)? = null,
    error: ((message: String?) -> Unit)? = null,
    netError: ((message: String) -> Unit)? = null,
    needLogin: ((message: String?) -> Unit)? = null,
    success: ((data: T) -> Unit)? = null,
) {
    if (successData != null) {
        // 如果有成功数据的接口
        if (isEmpty?.invoke(successData) == true) {
            empty?.invoke()
        } else {
            success?.invoke(successData)
        }
    } else {
        // 没有成功数据。随机的处理一个错误情况。
        val result = mainApiResult as? ApiResult.Error
        val code = result?.code
        val message = result?.message
        when (code) {
            ApiResult.ErrorCode.ERROR -> {
                error?.invoke(message ?: "请求失败")
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
                RouterManager.instance.getProvider(IUserProvider::class.java)?.apply {
                    startLoginPage()
                    clearUser()
                }
            }
            ApiResult.ErrorCode.RESULT_LIMIT -> {
                error?.invoke(message)
                // TODO: 2020/9/14 临时用一下，不确定时光api是否有限流一说
                showToast(message)
            }
            else -> {
                error?.invoke(message ?: "请求失败")
            }
        }
    }
}