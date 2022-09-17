package com.kotlin.android.api.base

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.kotlin.android.api.ApiResult
import com.kotlin.android.api.BuildConfig
import com.kotlin.android.ktx.ext.log.w
import com.kotlin.android.mtime.ktx.ext.showToast
import com.kotlin.android.router.RouterManager
import com.kotlin.android.router.provider.IUserProvider
import kotlinx.coroutines.*

/**
 * BaseViewModel的扩展：
 *
 * Created on 2020/4/27.
 *
 * @author o.s
 */

/**
 * 可扩展多种类型的结果处理方案
 */
fun <T : Any> checkResult(
    result: ApiResult<T>,
    isEmpty: ((success: T) -> Boolean)? = null,
    empty: (() -> Unit)? = null,
    error: ((message: String?) -> Unit)? = null,
    netError: ((message: String) -> Unit)? = null,
    needLogin: ((message: String?) -> Unit)? = null,
    success: ((data: T) -> Unit)? = null
) {
    result.w()
    when (result) {
        is ApiResult.Success -> {
            if (isEmpty?.invoke(result.data) == true) {
                empty?.invoke()
            } else {
                success?.invoke(result.data)
            }
        }
        is ApiResult.Error -> {
            val code = result.code
            val message = result.message
            when (code) {
                ApiResult.ErrorCode.ERROR -> {
                    error?.invoke(message ?: "请求失败，请重试")
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
                    error?.invoke(message)
                }
            }
        }
    }
}

/**
 * 通过协程调用 [api] 并将结果传递给 [uiModel] 进行通用处理。如果有 [pageStamp] 分页处理，则 [api] 会回传分页标示
 * [isShowLoading] 指定是否需要显示加载视图。默认true，显示加载视图。
 * [isRefresh]: 默认为 true 刷新接口，加载更多时需要明确指定为 false。
 * [isEmpty] 对成功响应的数据实体，进行业务空数据验证。默认null，数据非空。
 * [hasMore] 对成功响应的数据实体，进行业务加载更多验证。默认null，无加载跟多业务。
 * 设置 [hasMore] 将启动分页管理：[uiModel] 内置自增 [BaseUIModel.pageIndex]，需要时根据 [pageStamp] 同步 [BaseUIModel.pageStamp]。
 * 外部接口调用时，直接使用分页相关字段即可。可通过 [BaseUIModel.initPageSize] 设置分页大小（建议在声明uiModel时执行）
 */
fun <T : Any> ViewModel.call(
    uiModel: BaseUIModel<T>,
    isShowLoading: Boolean = true,
    isRefresh: Boolean = true,
    isEmpty: ((success: T) -> Boolean)? = null,
    hasMore: ((success: T) -> Boolean)? = null,
    pageStamp: ((success: T) -> String?)? = null,
    api: suspend CoroutineScope.(String?) -> ApiResult<T>
) {
    viewModelScope.launch(Dispatchers.Main) {
        if (isRefresh) {
            uiModel.resetPage() // 刷新重置页戳
        }
        if (isShowLoading) {
            uiModel.emitUIState(showLoading = true, isRefresh = isRefresh)
        }
        val result = withContext(Dispatchers.IO) {
            api(uiModel.pageStamp)
        }
        uiModel.checkResultAndEmitUIState(
            result = result,
            isRefresh = isRefresh,
            isEmpty = isEmpty,
            hasMore = hasMore,
            pageStamp = pageStamp,
        )
    }
}

/**
 * 通过协程调用 [api] 并将结果传递给 [uiModel] 进行通用处理。如果有 [pageStamp] 分页处理，则 [api] 会回传分页标示
 * [isShowLoading] 指定是否需要显示加载视图。默认true，显示加载视图。
 * [isRefresh]: 默认为 true 刷新接口，加载更多时需要明确指定为 false。
 * [isEmpty] 对成功响应的数据实体，进行业务空数据验证。默认null，数据非空。
 * [hasMore] 对成功响应的数据实体，进行业务加载更多验证。默认null，无加载跟多业务。
 * 设置 [hasMore] 将启动分页管理：[uiModel] 内置自增 [BaseUIModel.pageIndex]，需要时根据 [pageStamp] 同步 [BaseUIModel.pageStamp]。
 * 外部接口调用时，直接使用分页相关字段即可。可通过 [BaseUIModel.initPageSize] 设置分页大小（建议在声明uiModel时执行）
 * [converter] 在子线程中，对成功相应的数据实体，进行业务转换，如：转换成 [MutableList<MultiTypeBinder<*>>]
 */
fun <T : Any, B : Any> ViewModel.call(
    uiModel: BinderUIModel<T, B>,
    isShowLoading: Boolean = true,
    isRefresh: Boolean = true,
    isEmpty: ((success: T) -> Boolean)? = null,
    hasMore: ((success: T) -> Boolean)? = null,
    pageStamp: ((success: T) -> String?)? = null,
    converter: ((T) -> B),
    api: suspend CoroutineScope.(String?) -> ApiResult<T>
) {
    viewModelScope.launch(Dispatchers.Main) {
        if (isRefresh) {
            uiModel.resetPage() // 刷新重置页戳
        }
        if (isShowLoading) {
            uiModel.emitUIState(showLoading = true, isRefresh = isRefresh, binders = null)
        }
        var binders: B? = null
        val result = withContext(Dispatchers.IO) {
            api(uiModel.pageStamp).apply {
                if (this is ApiResult.Success && isEmpty?.invoke(data) != true) {
                    // 如果需要，则在子线程中完成转换操作
                    binders = converter.invoke(data)
                }
            }
        }
        uiModel.checkResultAndEmitUIState(
            result = result,
            isRefresh = isRefresh,
            isEmpty = isEmpty,
            hasMore = hasMore,
            pageStamp = pageStamp,
            binders = binders
        )
    }
}

/**
 * 并行调用。
 * [api] 可变参数API调用，注意API调用的顺序就是API成功结果的接收顺序。
 * [mainApiIndex] 主API调用顺序下标，默认从 0 开始。作用于当全部接口错误时，应用主API错误逻辑回调。
 * [uiModel] 数据模型，其中 [T] 为多个并行接口标准数据类型的目标整合对象（数据向）。
 * [isRefresh]: 默认为 true 刷新接口，加载更多时需要明确指定为 false。
 * [merge] 接口成功的结果合并成列表，由调用层实现目标结果。
 */
fun <T : Any> ViewModel.callParallel(
    vararg api: suspend CoroutineScope.() -> ApiResult<*>,
    mainApiIndex: Int = 0,
    uiModel: BaseUIModel<T>,
    isShowLoading: Boolean = true,
    isRefresh: Boolean = true,
    isEmpty: ((success: T) -> Boolean)? = null,
    hasMore: ((success: T) -> Boolean)? = null,
    pageStamp: ((success: T) -> String?)? = null,
    merge: ((Map<Int, *>) -> T)? = null,
) {
    viewModelScope.launch(Dispatchers.Main) {
        if (isRefresh) {
            uiModel.resetPage() // 刷新重置页戳
        }
        if (isShowLoading) {
            uiModel.emitUIState(showLoading = true, isRefresh = isRefresh)
        }

        var mainApiResult: ApiResult<*>? = null
        var successData: T? = null

        withContext(Dispatchers.IO) {
            var sTime = 0L
            if (BuildConfig.DEBUG) {
                sTime = System.currentTimeMillis()
            }
            val deferredList = ArrayList<Deferred<ApiResult<*>>>()
            val resultList = ArrayList<ApiResult<*>>()
            var mainDeferredResult: Deferred<ApiResult<*>>? = null
            api.forEachIndexed { index, func ->
                if (index == mainApiIndex) {
                    mainDeferredResult = async { func() }
                } else {
                    deferredList.add( async { func() } )
                }
            }

            deferredList.forEach {
                val r = it.await()
                resultList.add(r)
            }
            mainApiResult = mainDeferredResult?.await()
            mainApiResult?.apply {
                resultList.add(mainApiIndex, this)
            }

            val successMap = HashMap<Int, Any?>()
            var isSuccess = false
            // 遍历成功数据的情况
            resultList.forEachIndexed { index, apiResult ->
                val data = apiResult as? ApiResult.Success
                if (data != null) {
                    isSuccess = true
                }
                successMap[index] = data?.data
            }
            if (isSuccess) {
                successData = merge?.invoke(successMap) ?: successMap as? T
            }

            if (BuildConfig.DEBUG) {
                val endTime = System.currentTimeMillis()
                val log = StringBuilder()
                log.append("${this@callParallel.javaClass.simpleName}  callParallel time >>>${endTime - sTime}<<<")
                successMap.entries.forEach {
                    log.append("\nHashMap[${it.key}] -> ${it.value?.javaClass?.simpleName}")
                }
                log.w()
            }
        }

        uiModel.mergeEmitUIState(
            mainApiResult = mainApiResult,
            successData = successData,
            isRefresh = isRefresh,
            isEmpty = isEmpty,
            hasMore = hasMore,
            pageStamp = pageStamp,
        )
    }
}

/**
 * 并行调用，转换Binder
 * [api] 可变参数API调用，注意API调用的顺序就是API成功结果的接收顺序。
 * [mainApiIndex] 主API调用顺序下标，默认从 0 开始。作用于当全部接口错误时，应用主API错误逻辑回调。
 * [uiModel] 数据模型，其中 [T] 为多个并行接口标准数据类型的目标整合对象（数据向）；[B] 是对 [T] 在转换即：（数据向）->（数据绑定UIBinder向）【看上去多此一举】。
 * [isRefresh]: 默认为 true 刷新接口，加载更多时需要明确指定为 false。
 * [merge] 接口成功的结果合并成列表，由调用层实现目标结果（并列接口成功数据的整合）。
 * [converter] 将 [merge] 处理的结果进行转换成最终与UI绑定相关数据。如：多个 MutableList<MultiTypeBinder<*>> 列表等。即：（数据向）->（数据绑定UIBinder向）【看上去多此一举】。
 */
fun <T : Any, B : Any> ViewModel.callParallel(
    vararg api: suspend CoroutineScope.() -> ApiResult<*>,
    mainApiIndex: Int = 0,
    uiModel: BinderUIModel<T, B>,
    isShowLoading: Boolean = true,
    isRefresh: Boolean = true,
    isEmpty: ((success: T) -> Boolean)? = null,
    hasMore: ((success: T) -> Boolean)? = null,
    pageStamp: ((success: T) -> String?)? = null,
    converter: ((T) -> B),
    merge: ((Map<Int, *>) -> T)? = null,
) {
    viewModelScope.launch(Dispatchers.Main) {
        if (isRefresh) {
            uiModel.resetPage() // 刷新重置页戳
        }
        if (isShowLoading) {
            uiModel.emitUIState(showLoading = true, isRefresh = isRefresh, binders = null)
        }

        var mainApiResult: ApiResult<*>? = null
        var successData: T? = null
        var binders: B? = null

        withContext(Dispatchers.IO) {
            var sTime = 0L
            if (BuildConfig.DEBUG) {
                sTime = System.currentTimeMillis()
            }
            val deferredList = ArrayList<Deferred<ApiResult<*>>>()
            val resultList = ArrayList<ApiResult<*>>()
            var mainDeferredResult: Deferred<ApiResult<*>>? = null
            api.forEachIndexed { index, func ->
                if (index == mainApiIndex) {
                    mainDeferredResult = async { func() }
                } else {
                    deferredList.add( async { func() } )
                }
            }

            deferredList.forEach {
                val r = it.await()
                resultList.add(r)
            }
            mainApiResult = mainDeferredResult?.await()
            mainApiResult?.apply {
                resultList.add(mainApiIndex, this)
            }

            val successMap = HashMap<Int, Any?>()
            var isSuccess = false
            // 遍历成功数据的情况
            resultList.forEachIndexed { index, apiResult ->
                val data = apiResult as? ApiResult.Success
                if (data != null) {
                    isSuccess = true
                }
                successMap[index] = data?.data
            }
            if (isSuccess) {
                successData = merge?.invoke(successMap) ?: successMap as? T
                successData?.apply {
                    binders = converter.invoke(this)
                }
            }

            if (BuildConfig.DEBUG) {
                val endTime = System.currentTimeMillis()
                val log = StringBuilder()
                log.append("${this@callParallel.javaClass.simpleName} callParallel time >>>${endTime - sTime}<<<")
                successMap.entries.forEach {
                    log.append("\nHashMap[${it.key}] -> ${it.value?.javaClass?.simpleName}")
                }
                log.w()
            }
        }

        uiModel.mergeEmitUIState(
            mainApiResult = mainApiResult,
            successData = successData,
            isRefresh = isRefresh,
            isEmpty = isEmpty,
            hasMore = hasMore,
            pageStamp = pageStamp,
            binders = binders,
        )
    }
}

/**
 * 串行调用。
 * [api] 可变参数API调用，注意API调用的顺序就是API成功结果的接收顺序。
 *      在串行调用队列中，API调用需要明确依赖前置API正确结果的情形，如果前置API结果失败，会导致后续API调用链中断（api允许返回null）。
 * [mainApiIndex] 主API调用顺序下标，默认从 0 开始。作用于当全部接口错误时，应用主API错误逻辑回调。如果主API执行前，API调用链中断则设置为0，
 * [uiModel] 数据模型，其中 [T] 为多个并行接口标准数据类型的目标整合对象（数据向）。
 * [isRefresh]: 默认为 true 刷新接口，加载更多时需要明确指定为 false。
 * [merge] 接口成功的结果合并成列表，由调用层实现目标结果。
 */
fun <T : Any> ViewModel.callSerial(
    vararg api: suspend CoroutineScope.(Any?) -> ApiResult<*>?,
    mainApiIndex: Int = 0,
    uiModel: BaseUIModel<T>,
    isShowLoading: Boolean = true,
    isRefresh: Boolean = true,
    isEmpty: ((success: T) -> Boolean)? = null,
    hasMore: ((success: T) -> Boolean)? = null,
    pageStamp: ((success: T) -> String?)? = null,
    merge: ((Map<Int, *>) -> T)? = null,
) {
    viewModelScope.launch(Dispatchers.Main) {
        if (isRefresh) {
            uiModel.resetPage() // 刷新重置页戳
        }
        if (isShowLoading) {
            uiModel.emitUIState(showLoading = true, isRefresh = isRefresh)
        }

        var mainApiResult: ApiResult<*>? = null
        var successData: T? = null

        withContext(Dispatchers.IO) {
            var sTime = 0L
            if (BuildConfig.DEBUG) {
                sTime = System.currentTimeMillis()
            }
            val resultList = ArrayList<ApiResult<*>?>()
            api.forEach {
                val lastResult = resultList.lastOrNull()
                val param = lastResult as? ApiResult.Success
                resultList.add(it(param?.data))
            }

            val index = if (mainApiIndex < resultList.size) {
                mainApiIndex
            } else {
                0
            }
            mainApiResult = resultList[index]

            val successMap = HashMap<Int, Any?>()
            var isSuccess = false
            // 遍历成功数据的情况
            resultList.forEachIndexed { index, apiResult ->
                val data = apiResult as? ApiResult.Success
                if (data != null) {
                    isSuccess = true
                }
                successMap[index] = data?.data
            }
            if (isSuccess) {
                successData = merge?.invoke(successMap) ?: successMap as? T
            }

            if (BuildConfig.DEBUG) {
                val endTime = System.currentTimeMillis()
                val log = StringBuilder()
                log.append("${this@callSerial.javaClass.simpleName} callSerial time >>>${endTime - sTime}<<<")
                successMap.entries.forEach {
                    log.append("\nHashMap[${it.key}] -> ${it.value?.javaClass?.simpleName}")
                }
                log.w()
            }
        }

        uiModel.mergeEmitUIState(
            mainApiResult = mainApiResult,
            successData = successData,
            isRefresh = isRefresh,
            isEmpty = isEmpty,
            hasMore = hasMore,
            pageStamp = pageStamp,
        )
    }
}