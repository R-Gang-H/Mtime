package com.kotlin.android.review.component.item.ui.movie

import androidx.lifecycle.LiveData
import androidx.lifecycle.viewModelScope
import com.kotlin.android.api.base.BaseUIModel
import com.kotlin.android.core.BaseViewModel
import com.kotlin.android.app.data.entity.common.CommBizCodeResult
import com.kotlin.android.app.data.entity.review.MovieReviewList
import com.kotlin.android.review.component.item.repository.MovieReviewRepository
import com.kotlin.android.review.component.item.ui.movie.constant.MovieReviewConstant
import com.kotlin.android.user.afterLogin
import kotlinx.coroutines.launch

/**
 * @author vivian.wei
 * @date 2020/12/28
 * @desc 影片长/短影评列表页ViewModel
 */
class MovieReviewListViewModel: BaseViewModel()  {

    private val mReviewRepo by lazy { MovieReviewRepository() }

    // 长影评列表
    private val mReviewListUIModel = BaseUIModel<MovieReviewList>()
    val mReviewListUiState = mReviewListUIModel.uiState

    // 短影评列表
    private val mShortCommentListUIModel = BaseUIModel<MovieReviewList>()
    val mShortCommentListUiState = mShortCommentListUIModel.uiState

    // 赞/取消赞
    private val mPraiseUpUIModel = BaseUIModel<CommBizCodeResult>()
    val mPraiseUpUiState = mPraiseUpUIModel.uiState

    // 踩/取消踩
    private val mPraiseDownUIModel = BaseUIModel<CommBizCodeResult>()
    val mPraiseDownUiState = mPraiseDownUIModel.uiState

    /**
     * 获取影片长影评列表(按最新排序）
     */
    fun getReviewList(movieId: String, pageIndex: Int) {
        viewModelScope.launch {

            val result = withOnIO {
                mReviewRepo.getMovieLongComments(movieId, pageIndex.toLong(),
                        MovieReviewConstant.REVIEW_PAGE_SIZE, MovieReviewConstant.REVIEW_ORDER_TYPE_NEW)
            }

            mReviewListUIModel.checkResultAndEmitUIState(result)
        }
    }

    /**
     * 获取影片短影评列表
     */
    fun getShortCommentList(movieId: String, pageIndex: Int, orderType: Long) {
        viewModelScope.launch {

            val result = withOnIO {
                mReviewRepo.getMovieComments(movieId, pageIndex.toLong(),
                        MovieReviewConstant.SHORT_COMMENT_PAGE_SIZE, orderType)
            }

            mShortCommentListUIModel.checkResultAndEmitUIState(result)
        }
    }

    /**
     * 赞/取消赞
     */
    fun praiseUp(
            action: Long,
            objType: Long,
            objId: Long
    ) {
        // 需要登录
        afterLogin {
            viewModelScope.launch(main) {
                mPraiseUpUIModel.emitUIState(showLoading = true)

                val result = withOnIO {
                    mReviewRepo.praiseUp(action, objType, objId)
                }

                mPraiseUpUIModel.checkResultAndEmitUIState(result)
            }
        }
    }

    /**
     * 踩/取消踩
     */
    fun praiseDown(
            action: Long,
            objType: Long,
            objId: Long
    ) {
        // 需要登录
        afterLogin {
            viewModelScope.launch(main) {
                mPraiseDownUIModel.emitUIState(showLoading = true)

                val result = withOnIO {
                    mReviewRepo.praiseDown(action, objType, objId)
                }

                mPraiseDownUIModel.checkResultAndEmitUIState(result)
            }
        }
    }

}