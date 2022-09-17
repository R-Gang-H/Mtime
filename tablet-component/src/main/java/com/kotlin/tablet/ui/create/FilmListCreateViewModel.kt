package com.kotlin.tablet.ui.create

import com.kotlin.android.api.base.BaseUIModel
import com.kotlin.android.api.base.call
import com.kotlin.android.app.data.entity.filmlist.FilmListCreateResult
import com.kotlin.android.app.data.entity.filmlist.FilmListEditInfo
import com.kotlin.android.app.data.entity.filmlist.FilmListModifyMovieInfo
import com.kotlin.android.app.data.entity.monopoly.CommResult
import com.kotlin.android.core.BaseViewModel
import com.kotlin.tablet.repository.FilmListRepository

/**
 * 创建者: sunhao
 * 创建时间: 2022/3/10
 * 描述:创建片单ViewModel
 **/
class FilmListCreateViewModel : BaseViewModel() {
    private val repo by lazy { FilmListRepository() }
    private val createUiModel by lazy { BaseUIModel<FilmListCreateResult>() }
    val createUiState by lazy { createUiModel.uiState }

    private val editUiModel by lazy { BaseUIModel<CommResult>() }
    val editUiState by lazy { editUiModel.uiState }

    private val filmListInfoUiMode by lazy { BaseUIModel<FilmListEditInfo>() }
    val filmListInfoState by lazy { filmListInfoUiMode.uiState }

    private val modifyMoviesUiModel by lazy { BaseUIModel<FilmListModifyMovieInfo>() }
    val modifyMoviesUiState by lazy { modifyMoviesUiModel.uiState }
    var isFirstRefresh = true

    /**
     * 创建or编辑片单
     */
    fun save(
        filmListId: Long? = 0L,
        isEdit: Boolean,
        title: String,
        synopsis: String,
        coverUrl: String,
        coverFieldId: String,
        checked: Boolean,
        movieIds: List<Long>? = listOf()
    ) {
        val privacyStatus = if (checked) 1L else 0L
        if (isEdit) {
            call(editUiModel) {
                repo.edit(
                    filmListId ?: 0L,
                    title,
                    synopsis,
                    coverUrl,
                    coverFieldId,
                    privacyStatus,
                    movieIds ?: listOf()
                )
            }
        } else {
            call(createUiModel) {
                repo.create(title, synopsis, coverUrl, coverFieldId, privacyStatus)
            }
        }
    }

    /**
     * 编辑-片单信息
     */
    fun reqFilmListInfo(filmListId: Long) {
        call(
            uiModel = filmListInfoUiMode
        ) {
            repo.reqFilmListInfo(filmListId)
        }
    }
    /**
     * 编辑 -已添加电影
     */
    fun getModifyMovies(filmListId: Long) {
        call(modifyMoviesUiModel) {
            repo.getModifyMovies(
                filmListId
            )
        }
    }
}