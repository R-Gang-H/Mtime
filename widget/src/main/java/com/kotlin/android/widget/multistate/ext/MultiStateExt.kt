package com.kotlin.android.widget.multistate.ext

import com.kotlin.android.api.base.BaseUIModel
import com.kotlin.android.api.base.BinderUIModel
import com.kotlin.android.widget.multistate.MultiStateView

/**
 * 完成多状态页面更新操作：不是loading，也不是加载更多，才会去更新页面状态。
 *
 * 仅针对主接口（可以影响多状态页面）状态更新。其他接口不应调用此方法。
 *
 * @param emptyState 如需自己处理空逻辑则传入
 */
fun <T : Any> MultiStateView?.complete(uiModel: BaseUIModel<T>, emptyState: (() -> Unit)? = null) {
    uiModel.apply {
        // 不是loading，也不是加载更多，才会去更新页面状态。
        if (!showLoading && isRefresh) {
            val hasData = if (this is BinderUIModel<*, *>) {
                binders != null
            } else {
                success != null
            }
            if (hasData && !isEmpty) {
                this@complete?.setViewState(MultiStateView.VIEW_STATE_CONTENT)
            }
            if (isEmpty) {
                if (emptyState == null) {
                    //默认空处理
                    this@complete?.setViewState(MultiStateView.VIEW_STATE_EMPTY)
                } else {
                    //外部处理空逻辑
                    emptyState.invoke()
                }
            }
            error?.apply {
                if (isRefresh) {
                    this@complete?.setViewState(MultiStateView.VIEW_STATE_ERROR)
                }
            }
            netError?.apply {
                if (isRefresh) {
                    this@complete?.setViewState(MultiStateView.VIEW_STATE_NO_NET)
                }
            }
        }
    }
}