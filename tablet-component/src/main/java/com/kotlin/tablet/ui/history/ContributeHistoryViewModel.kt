package com.kotlin.tablet.ui.history

import com.kotlin.android.api.base.BaseUIModel
import com.kotlin.android.api.base.call
import com.kotlin.android.app.data.entity.filmlist.HistoryActivityInfo
import com.kotlin.android.core.BaseViewModel
import com.kotlin.tablet.repository.FilmListRepository

/**
 * 创建者: SunHao
 * 创建时间: 2022/3/25
 * 描述:投稿历史主题
 **/
class ContributeHistoryViewModel : BaseViewModel() {
    private val repo by lazy { FilmListRepository() }

    private val historyUIModel by lazy { BaseUIModel<HistoryActivityInfo>() }
    val historyUIState = historyUIModel.uiState

    fun reqHistoryData(isRefresh: Boolean) {
        call(
            uiModel = historyUIModel,
            isRefresh = isRefresh,
            pageStamp = { it.nextStamp },
            isEmpty = { it.activitys?.isEmpty() == true },
            hasMore = {
                it.hasNext == true
            }) {
            repo.reqHistoryActivities(
                nextStamp = historyUIModel.pageStamp,
                pageSize = historyUIModel.pageSize
            )
        }
    }

}