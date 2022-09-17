package com.kotlin.android.api.base

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.kotlin.android.api.ApiResult

/**
 * 关联UI展示的数据模型
 *
 * Created on 2020-04-21.
 *
 * @author o.s
 */
open class BaseUIModel<T : Any>(
    var showLoading: Boolean = false, // 显示/隐藏loading
    var isRefresh: Boolean = true, // true默认:刷新接口，false:加载更多页（从第二页起false）
    var loadMore: Boolean = false, // 将被移除，其含义和 noMoreData 相反，且强关联。
    var noMoreData: Boolean = false, //true:没有更多数据了
    var needLogin: Boolean = false, // true:需要登录
    var error: String? = null, // 接口响应失败
    var netError: String? = null, // 接口响应失败
    var isEmpty: Boolean = false, // 数据为空
    var success: T? = null, // 接口响应成功
) {

    private val _uiState = MutableLiveData<BaseUIModel<T>>()

    open val uiState: MutableLiveData<out BaseUIModel<T>>
        get() = _uiState

    private var _pageStamp: String? = null // 内置分页页签，页戳。注意：不要随意清除，只有在明确刷新时，才需要重置为null
    private var _pageIndex: Long = 1 // 内置分页页签
    private var _pageSize: Long = 20 // 内置分页大小

    /**
     * 分页方式一、[pageStamp]
     */
    val pageStamp: String?
        get() = _pageStamp

    /**
     * 分页方式二、[pageIndex]
     */
    val pageIndex: Long
        get() = _pageIndex

    /**
     * 分页大小外部可更改 [initPageSize]
     */
    val pageSize: Long
        get() = _pageSize

    /**
     * 当前加载数据最大总量
     */
    val totalCount: Long
        get() = _pageIndex * pageSize

    /**
     * 初始化分页大小，默认20条数据
     */
    fun initPageSize(pageSize: Long) {
        _pageSize = pageSize
    }

    /**
     * 重置分页状态
     */
    fun resetPage() {
        _pageStamp = null
        _pageIndex = 1
    }

    /**
     * 准备下一页。
     * 在接口调用时，指定了 [call] hasMore 参数的，不论是否有下一页，页戳都会进行下一页准备。
     * 没有指定 [call] hasMore 参数的说明，接口不需要处理加载更多的逻辑。
     */
    fun nextPage(pageStamp: String? = null) {
        _pageStamp = pageStamp
        _pageIndex++
    }

    /**
     * 检查API结果并通知UI刷新
     * [isEmpty]: 自定义判空条件，返回true表示结果为空。默认非空处理。
     * 设置 [hasMore] 将启动分页管理：[BaseUIModel] 内置自增 [BaseUIModel.pageIndex]，需要时根据 [pageStamp] 同步 [BaseUIModel.pageStamp]。
     * 外部接口调用时，直接使用分页相关字段即可。可通过 [BaseUIModel.initPageSize] 设置分页大小（建议在声明uiModel时执行）
     */
    fun checkResultAndEmitUIState(
        result: ApiResult<T>,
        isRefresh: Boolean = true,
        isEmpty: ((success: T) -> Boolean)? = null,
        hasMore: ((success: T) -> Boolean)? = null,
        pageStamp: ((success: T) -> String?)? = null,
    ) {
        checkResult(
            result = result,
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
                        success = it
                    )
                } else {
                    emitUIState(
                        isRefresh = isRefresh,
                        success = it,
                    )
                }
            })
    }

    /**
     * 发出UI状态
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
        success: T? = null
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
                "\n"
    }

    fun setData(
        showLoading: Boolean = false,
        isRefresh: Boolean = true,
        loadMore: Boolean = false,
        noMoreData: Boolean = false,
        needLogin: Boolean = false,
        error: String? = null,
        netError: String? = null,
        isEmpty: Boolean = false,
        success: T? = null
    ) {
        this.showLoading = showLoading
        this.isRefresh = isRefresh
        this.loadMore = loadMore
        this.noMoreData = noMoreData
        this.needLogin = needLogin
        this.error = error
        this.netError = netError
        this.isEmpty = isEmpty
        this.success = success
    }

    open fun resetAll() {
        showLoading = false
        isRefresh = true
        loadMore = false
        noMoreData = false
        needLogin = false
        error = null
        netError = null
        isEmpty = false
        success = null
    }
}