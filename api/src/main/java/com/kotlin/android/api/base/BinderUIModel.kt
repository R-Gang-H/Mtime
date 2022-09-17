package com.kotlin.android.api.base

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.kotlin.android.api.ApiResult

/**
 * UI数据模型扩展：兼容UI数据类型
 * [T] : 接口响应标准实体类。如 Film
 * [B] : 匹配UI展示的目标数据类型。如：MutableList<MultiTypeBinder<*>>
 *
 * [binders] 和 [success] 同时回调，具有同等作用。
 *
 * Created on 2021/12/1.
 *
 * @author o.s
 */
class BinderUIModel<T : Any, B : Any> : BaseUIModel<T>() {
    var binders: B? = null // 接口响应成功

    private val _uiState = MutableLiveData<BinderUIModel<T, B>>()

    override val uiState: MutableLiveData<BinderUIModel<T, B>>
        get() = _uiState

    /**
     * 检查API结果并通知UI刷新
     * [isEmpty]: 自定义判空条件，返回true表示结果为空。默认非空处理。
     * 设置 [hasMore] 将启动分页管理：[BaseUIModel] 内置自增 [BaseUIModel.pageIndex]，需要时根据 [pageStamp] 同步 [BaseUIModel.pageStamp]。
     * 外部接口调用时，直接使用分页相关字段即可。可通过 [BaseUIModel.initPageSize] 设置分页大小（建议在声明uiModel时执行）
     * [binders]: 调用时，默认值需要明确指定 binders = null，否则会调用父类中的同名方法。
     */
    fun checkResultAndEmitUIState(
        result: ApiResult<T>,
        isRefresh: Boolean = true,
        isEmpty: ((success: T) -> Boolean)? = null,
        hasMore: ((success: T) -> Boolean)? = null,
        pageStamp: ((success: T) -> String?)? = null,
        binders: B? = null, // 子线程调用转换方法
    ) {
        checkResult(
            result = result,
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
                        isRefresh = isRefresh,
                        success = it,
                        binders = binders,
                    )
                }
            }
        )
    }

    /**
     * [binders]: 调用时，默认值需要明确指定 binders = null，否则会调用父类中的同名方法。
     */
    fun emitUIState(
        showLoading: Boolean = false,
        isRefresh: Boolean = true,
        loadMore: Boolean = false,
        noMoreData: Boolean = false,
        needLogin: Boolean = false,
        error: String? = null,
        netError: String? = null,
        isEmpty: Boolean = false,
        success: T? = null,
        binders: B? = null
    ) {
        resetAll()
        this.showLoading = showLoading
        this.isRefresh = isRefresh
        this.loadMore = loadMore
        this.noMoreData = noMoreData
        this.needLogin = needLogin
        this.error = error
        this.netError = netError
        this.isEmpty = isEmpty
        this.success = success
        this.binders = binders

        _uiState.value = this
    }

    override fun toString(): String {
        return "${javaClass.simpleName} :: " +
                "\nshowLoading = $showLoading, " +
                "\nisRefresh = $isRefresh, " +
                "\nloadMore = $loadMore, " +
                "\nnoMoreData = $noMoreData, " +
                "\nneedLogin = $needLogin, " +
                "\nerror = $error, " +
                "\nnetError = $netError, " +
                "\nisEmpty = $isEmpty, " +
                "\npageStamp = $pageStamp, " +
                "\npageIndex = $pageIndex, " +
                "\npageSize = $pageSize, " +
                "\nsuccess = $success, " +
                "\nbinders = $binders, " +
                "\n"
    }

    /**
     * [binders]: 调用时，默认值需要明确指定 binders = null，否则会调用父类中的同名方法。
     */
    fun setData(
        showLoading: Boolean = false,
        isRefresh: Boolean = true,
        loadMore: Boolean = false,
        noMoreData: Boolean = false,
        needLogin: Boolean = false,
        error: String? = null,
        netError: String? = null,
        isEmpty: Boolean = false,
        success: T? = null,
        binders: B? = null
    ) {
        super.setData(
            showLoading,
            isRefresh,
            loadMore,
            noMoreData,
            needLogin,
            error,
            netError,
            isEmpty,
            success
        )
        this.binders = binders
    }

    override fun resetAll() {
        super.resetAll()
        binders = null
    }
}