package com.kotlin.tablet.ui.details

import com.kotlin.android.api.base.BaseUIModel
import com.kotlin.android.api.base.call
import com.kotlin.android.app.data.constant.CommConstant.SHARE_TYPE_SUBJECT
import com.kotlin.android.app.data.entity.common.CollectionResult
import com.kotlin.android.app.data.entity.common.CommonShare
import com.kotlin.android.app.data.entity.filmlist.FilmListBasicInfoEntity
import com.kotlin.android.app.data.entity.filmlist.FilmListCreateResult
import com.kotlin.android.app.data.entity.filmlist.FilmListPageMoviesEntity
import com.kotlin.android.app.data.entity.filmlist.FilmListShareEntity
import com.kotlin.android.core.BaseViewModel
import com.kotlin.tablet.repository.FilmListRepository

class FilmListDetailsViewModel : BaseViewModel() {
    private val repo by lazy { FilmListRepository() }
    private val baseInfoUiMode by lazy { BaseUIModel<FilmListBasicInfoEntity>() }
    val baseInfoState by lazy { baseInfoUiMode.uiState }

    private val pageMoviesUIModel by lazy { BaseUIModel<FilmListPageMoviesEntity>() }
    val pageMoviesState = pageMoviesUIModel.uiState

    private val shareUIModel by lazy { BaseUIModel<FilmListShareEntity>() }
    val shareState = shareUIModel.uiState

    private val deleteUiModel by lazy { BaseUIModel<FilmListCreateResult>() }
    val deleteState = deleteUiModel.uiState
    private val collectUiModel by lazy { BaseUIModel<CollectionResult>() }
    val collectState = collectUiModel.uiState


    /**
     * 加载上半部分数据
     */
    fun loadBasicInfo(filmListId: Long, source: Long) {
        call(
            uiModel = baseInfoUiMode
        ) {
            repo.details(filmListId, source)
        }
    }

    /**
     * 加载下半部分数据
     */
    fun loadPageMovies(
        isRefresh: Boolean,
        filmListId: Long,
        type: Long = 0L,
        source: Long
    ) {
        call(
            uiModel = pageMoviesUIModel,
            isRefresh = isRefresh,
            pageStamp = {
                it.nextStamp
            },
            hasMore = {
                it.hasNext
            }
        ) {
            repo.pageMovies(
                filmListId = filmListId,
                type = type,
                pageSize = pageMoviesUIModel.pageSize,
                nextStamp = pageMoviesUIModel.pageStamp,
                source = source
            )
        }
    }

    /**
     * 片单分享数据
     * filmListId LONG 片单id【必填】
     */
    fun loadShare(filmListId: Long) {
        call(
            uiModel = shareUIModel
        ) {
            repo.share(filmListId)
        }
    }

    /**
     * 片单详情删除
     * filmListId	Number【必填】片单id
     */
    fun delete(filmListId: Long) {
        call(
            uiModel = deleteUiModel
        ) {
            repo.delete(filmListId)
        }
    }

    /**
     * 片单详情收藏
     * filmListId	Number【必填】片单id
     */
    fun collect(action: Long, filmListId: Long) {
        call(
            uiModel = collectUiModel
        ) {
            repo.collect(action, filmListId)
        }
    }
}