package com.kotlin.tablet.ui.mycreate

import com.kotlin.android.api.base.BaseUIModel
import com.kotlin.android.api.base.call
import com.kotlin.android.app.data.entity.filmlist.MyCreateFilmList
import com.kotlin.android.app.data.entity.monopoly.CommResult
import com.kotlin.android.core.BaseViewModel
import com.kotlin.tablet.repository.FilmListRepository

/**
 * 创建者: SunHao
 * 创建时间: 2022/3/27
 * 描述:我的片单-投稿
 **/
class MyCreateViewModel : BaseViewModel() {

    private val repo by lazy { FilmListRepository() }

    private val myCreateUIModel by lazy { BaseUIModel<MyCreateFilmList>() }
    private val resultUIModel by lazy { BaseUIModel<CommResult>() }
    val myCreateUIState = myCreateUIModel.uiState
    val resultUIState = resultUIModel.uiState

    fun reqMyCreateData(isRefresh: Boolean, activityId: Long?) {
        call(
            uiModel = myCreateUIModel,
            isRefresh = isRefresh,
            pageStamp = { it.nextStamp },
            isEmpty = { it.myManuscriptsFilmLists.isNullOrEmpty() },
            hasMore = { it.hasNext == true }) {
            repo.reqMyManuscripts(
                nextStamp = myCreateUIModel.pageStamp,
                pageSize = myCreateUIModel.pageSize,
                activityId
            )
        }
    }

    fun contribute(activityId: Long, filmListId: Long) {
        call(uiModel = resultUIModel) {
            repo.reqContribute(
                activityId,
                filmListId
            )
        }
    }
}