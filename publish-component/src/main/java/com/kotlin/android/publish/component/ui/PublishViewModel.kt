package com.kotlin.android.publish.component.ui

import com.kotlin.android.api.base.BaseUIModel
import com.kotlin.android.core.BaseViewModel
import com.kotlin.android.app.data.annotation.ContentType
import com.kotlin.android.app.data.entity.common.CommBizCodeResult
import com.kotlin.android.app.data.entity.common.StatusResult
import com.kotlin.android.app.data.entity.community.record.PostRecord
import com.kotlin.android.app.data.entity.community.record.RecordId
import com.kotlin.android.app.data.entity.movie.LatestComment
import com.kotlin.android.publish.component.repo.PublishRepository

/**
 * 发布社区内容：
 *
 * Created on 2020/10/9.
 *
 * @author o.s
 */
class PublishViewModel : BaseViewModel() {

    private val repo by lazy { PublishRepository() }

    private val recordIdUIModel by lazy { BaseUIModel<RecordId>() }
    private val recordUIModel by lazy { BaseUIModel<CommBizCodeResult>() }
    private val movieRatingUIModel by lazy { BaseUIModel<StatusResult>() }
    private val latestCommentUIModel by lazy { BaseUIModel<LatestComment>() }
    private val deleteCommentUIModel by lazy { BaseUIModel<CommBizCodeResult>() }

    val recordIdUIState = recordIdUIModel.uiState
    val recordUIState = recordUIModel.uiState
    val movieRatingUIState = movieRatingUIModel.uiState
    val latestCommentUIState = latestCommentUIModel.uiState
    val deleteCommentUIState = deleteCommentUIModel.uiState

    fun loadRecordId(@ContentType type: Long) {
        launchOnUI {
            recordIdUIModel.emitUIState(showLoading = true)
            val result = withOnIO {
                repo.loadRecordId(type)
            }
            recordIdUIModel.checkResultAndEmitUIState(result)
        }
    }

    fun publishRecord(record: PostRecord) {
        launchOnUI {
            recordUIModel.emitUIState(showLoading = true)
            val result = withOnIO {
                repo.postRecord(record)
            }
            recordUIModel.checkResultAndEmitUIState(result)
        }
    }

    fun movieRating(movieId: Long, rating: Double, subItemRating: String) {
        launchOnUI {
            val result = withOnIO {
                repo.getMovieRating(movieId, rating, subItemRating)
            }
            movieRatingUIModel.checkResultAndEmitUIState(result)
        }
    }

    fun getMovieRating(movieId: Long) {
        launchOnUI {
            val result = withOnIO {
                repo.getMovieLatestComment(movieId)
            }
            latestCommentUIModel.checkResultAndEmitUIState(result)
        }
    }

    fun postDeleteContent(@ContentType type: Long, contentId: Long) {
        launchOnUI {
            recordIdUIModel.emitUIState(showLoading = true)
            val result = withOnIO {
                repo.postDeleteContent(type, contentId)
            }
            deleteCommentUIModel.checkResultAndEmitUIState(result)
        }
    }
}