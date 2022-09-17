package com.kotlin.android.widget.refresh.ext

import com.kotlin.android.api.base.BaseUIModel
import com.kotlin.android.api.base.BinderUIModel
import com.scwang.smart.refresh.layout.SmartRefreshLayout
import com.scwang.smart.refresh.layout.constant.RefreshState


/**
 * 完成刷新、加载更多逻辑：
 *
 * 1，showLoading = false 时：接口必定返回success/binders/isEmpty/error/netError/needLogin，这些情况都需要处理UI加载的状态。
 *
 * 2，所有的接口默认为 isRefresh = true，表示更新接口，如果加载下一页（第二页及以后），需明确指定 isRefresh = false。
 *
 * 3，noMoreData 下拉刷新和加载更多都需要判断是否有更多数据。
 */
fun SmartRefreshLayout?.complete(uiModel: BaseUIModel<*>) {
    this?.apply {
        if (!uiModel.showLoading) {
            if (uiModel.isRefresh) {
                if (uiModel.noMoreData) {
                    // noMoreData = true 一定是下啦/加载成功
                    finishRefreshWithNoMoreData()
                } else {
                    val hasData = if (uiModel is BinderUIModel<*, *>) {
                        uiModel.binders != null
                    } else {
                        uiModel.success != null
                    }
                    // 这个处理是：解决没有刷新动画状态的情况下，列表加载更多状态切换。
                    if (state == RefreshState.None) {
                        setNoMoreData(false)
                    }
                    // 有结果数据或空执行下啦/加载成功
                    finishRefresh(hasData or uiModel.isEmpty)
                }
            } else {
                if (uiModel.noMoreData) {
                    // noMoreData = true 一定是下啦/加载成功
                    finishLoadMoreWithNoMoreData()
                } else {
                    val hasData = if (uiModel is BinderUIModel<*, *>) {
                        uiModel.binders != null
                    } else {
                        uiModel.success != null
                    }
                    // 有结果数据或空执行下啦/加载成功
                    finishLoadMore(hasData or uiModel.isEmpty)
                }
            }
        }
    }
}