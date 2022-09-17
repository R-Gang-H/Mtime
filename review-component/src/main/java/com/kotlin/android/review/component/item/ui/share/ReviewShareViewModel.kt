package com.kotlin.android.review.component.item.ui.share

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.kotlin.android.api.base.BaseUIModel
import com.kotlin.android.api.base.checkResult
import com.kotlin.android.comment.component.DetailBaseViewModel
import com.kotlin.android.app.data.entity.image.MovieImage
import com.kotlin.android.review.component.item.adapter.ReviewShareBinder
import com.kotlin.android.review.component.item.bean.ReviewDetailViewBean
import com.kotlin.android.review.component.item.bean.ReviewShareItemViewBean
import com.kotlin.android.review.component.item.bean.ReviewShareViewBean
import com.kotlin.android.review.component.item.repository.ReviewDetailRepository
import com.kotlin.android.app.data.entity.user.User
import com.kotlin.android.user.isLogin
import com.kotlin.android.widget.adapter.multitype.adapter.binder.MultiTypeBinder
import kotlinx.coroutines.launch

/**
 * create by lushan on 2020/11/3
 * description: 影评分享页面
 */
class ReviewShareViewModel : DetailBaseViewModel() {
    private val repo by lazy {
        ReviewDetailRepository()
    }

    //    分享页面上数据
    private val _shareViewState = MutableLiveData<ReviewShareViewBean>()
    val shareViewState: LiveData<ReviewShareViewBean>
        get() = _shareViewState

    //    影评详情
    private val reviewDetailUIModel = BaseUIModel<ReviewDetailViewBean>()
    val reviewDetailState
        get() = reviewDetailUIModel.uiState

    //    剧照信息
    private val reviewPhotoUIModel = BaseUIModel<MovieImage>()
    val reviewPhotoState
        get() = reviewPhotoUIModel.uiState

    //    用户详情信息
    private val accountDetailUIModel = BaseUIModel<User>()
    val accountDetailState = accountDetailUIModel.uiState


    /**
     * 加载用户详情信息
     */
    fun getAccountDetail() {
        if (isLogin().not()) {
            return
        }
        viewModelScope.launch(main) {
            val result = withOnIO {
                repo.getMineUserDetail()
            }
            accountDetailUIModel.checkResultAndEmitUIState(result)
        }
    }


    fun getShareList(): MutableList<MultiTypeBinder<*>> {
        val list = mutableListOf<MultiTypeBinder<*>>()
        list.add(ReviewShareBinder(ReviewShareItemViewBean(ReviewShareItemViewBean.REVIEW_SHARE_PLATFORM_SWITCH)))//暂时不要匿名
        list.add(ReviewShareBinder(ReviewShareItemViewBean(ReviewShareItemViewBean.REVIEW_SHARE_PLATFORM_LOCAL)))
        list.add(ReviewShareBinder(ReviewShareItemViewBean(ReviewShareItemViewBean.REVIEW_SHARE_PLATFORM_WECHAT)))
        list.add(ReviewShareBinder(ReviewShareItemViewBean(ReviewShareItemViewBean.REVIEW_SHARE_PLATFORM_FIREND)))
        list.add(ReviewShareBinder(ReviewShareItemViewBean(ReviewShareItemViewBean.REVIEW_SHARE_PLATFORM_SINA)))
        list.add(ReviewShareBinder(ReviewShareItemViewBean(ReviewShareItemViewBean.REVIEW_SHARE_PLATFORM_QQ)))
        return list
    }

    /**
     * 加载影评详情
     */
    fun loadReviewDetail(contentId: Long, type: Long, recId: Long) {
        viewModelScope.launch(main) {
            reviewDetailUIModel.emitUIState(showLoading = true)

            val result = withOnIO {
                repo.loadReviewDetail(contentId, type, recId,true)
            }

            checkResult(result, error = { reviewDetailUIModel.emitUIState(error = it) },
                    netError = { reviewDetailUIModel.emitUIState(netError = it) },
                    success = { reviewDetailUIModel.emitUIState(success = it) },
                    empty = { reviewDetailUIModel.emitUIState(isEmpty = true) })
        }
    }

    /**
     * 更新内容信息
     */
    fun setContent(reviewDetailViewBean: ReviewDetailViewBean) {
        _shareViewState.value = ReviewShareViewBean.build(reviewDetailViewBean)
    }

    /**
     * 加载剧照信息
     */
    fun loadMoviePhoto(movieId: Long) {
        viewModelScope.launch(main) {
            val result = withOnIO {
                repo.loadMovePhoto(movieId)
            }

            reviewPhotoUIModel.checkResultAndEmitUIState(result)
        }

    }

    fun getNickName(): String {
        return _shareViewState.value?.userName.orEmpty()
    }

    fun getUserPic(): String {
        return _shareViewState.value?.userPic.orEmpty()
    }

    fun updateJoinDays(joinDays: Long, movieNum: Long) {
        val value = _shareViewState.value
        value?.joinDays = joinDays
        value?.movieNum = movieNum
        _shareViewState.value = value
    }

}