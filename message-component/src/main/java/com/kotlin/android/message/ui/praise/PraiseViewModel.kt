package com.kotlin.android.message.ui.praise

import com.kotlin.android.api.base.BaseUIModel
import com.kotlin.android.api.base.BinderUIModel
import com.kotlin.android.api.base.call
import com.kotlin.android.app.data.entity.message.PraiseListResult
import com.kotlin.android.core.BaseViewModel
import com.kotlin.android.message.repository.MessageRepository
import com.kotlin.android.message.ui.praise.viewBean.PraiseViewBean
import com.kotlin.android.message.widget.EmptyViewBinder
import com.kotlin.android.widget.adapter.multitype.adapter.binder.MultiTypeBinder

/**
 * Created by zhaoninglongfei on 2022/3/15
 *
 */
class PraiseViewModel : BaseViewModel() {

    private val repo = MessageRepository()

    private val praiseUiModel =
        BinderUIModel<PraiseListResult, List<MultiTypeBinder<*>>>()
    val praiseUiState = praiseUiModel.uiState

    private val emptyStateUiModel = BaseUIModel<MultiTypeBinder<*>>()
    val emptyStateUiState = emptyStateUiModel.uiState

    fun loadPraiseList(isRefresh: Boolean) {
        call(
            uiModel = praiseUiModel,
            isShowLoading = isRefresh,
            isRefresh = isRefresh,
            converter = {
                PraiseViewBean.convertToItemPraiseBinders(it)
            },
            isEmpty = {
                isRefresh && it.items.isNullOrEmpty()
            },
            hasMore = {
                it.hasNext ?: false
            },
            pageStamp = {
                it.nextStamp
            }
        ) {
            repo.loadPraiseList(
                nextStamp = praiseUiModel.pageStamp,
                pageSize = praiseUiModel.pageSize
            )
        }
    }

    fun loadEmptyView() {
        emptyStateUiModel.emitUIState(
            success = EmptyViewBinder()
        )
    }
}