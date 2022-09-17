package com.kotlin.tablet.ui.list

import com.kotlin.android.api.base.BaseUIModel
import com.kotlin.android.api.base.call
import com.kotlin.android.app.data.entity.filmlist.FilmListEntity
import com.kotlin.android.core.BaseViewModel
import com.kotlin.tablet.repository.FilmListRepository

/**
 * 片单列表页面viewmodel
 */
class TabletMainViewModel : BaseViewModel() {
    private val repo by lazy { FilmListRepository() }
    private val listUiModel by lazy { BaseUIModel<FilmListEntity>() }
    val listUiState by lazy { listUiModel.uiState }

    /**
     * 加载列表数据
     */
    fun loadData(
        isRefresh: Boolean,
        category: Long? = null
    ) {
        call(
            uiModel = listUiModel,
            isRefresh = isRefresh,
            pageStamp = {
                it.nextStamp
            },
            hasMore = {
                it.hasNext
            },
            isEmpty = {
                it.pageRcmds?.size == 0
            }
        ) {
            repo.list(
                category = category,
                pageSize = listUiModel.pageSize,
                nextStamp = listUiModel.pageStamp
            )
        }
    }

}