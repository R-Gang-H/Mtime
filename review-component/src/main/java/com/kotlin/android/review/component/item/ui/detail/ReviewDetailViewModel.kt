package com.kotlin.android.review.component.item.ui.detail

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.kotlin.android.api.base.BaseUIModel
import com.kotlin.android.api.base.checkResult
import com.kotlin.android.comment.component.DetailBaseViewModel
import com.kotlin.android.review.component.item.bean.ReviewDetailViewBean
import com.kotlin.android.review.component.item.repository.ReviewDetailRepository
import com.kotlin.android.comment.component.bean.UgcCommonBarBean
import com.kotlin.android.ugc.detail.component.bean.UgcTitleBarBean
import com.kotlin.android.app.data.entity.user.User
import com.kotlin.android.user.isLogin
import kotlinx.coroutines.launch

/**
 * create by lushan on 2020/8/10
 * description: 影评详情页面，包括长影评和短影评
 */
class ReviewDetailViewModel : DetailBaseViewModel() {

    private val repo by lazy {
        ReviewDetailRepository()
    }

    //    影评详情
    private val reviewDetailUIModel = BaseUIModel<ReviewDetailViewBean>()
    val reviewDetailState = reviewDetailUIModel.uiState


    private val _titleBar = MutableLiveData<UgcTitleBarBean>()

    val titleBar: LiveData<UgcTitleBarBean>
        get() = _titleBar


    fun setTitleBar(createUser: UgcCommonBarBean.CreateUser) {
        _titleBar.value = UgcTitleBarBean(createUser.userId, "${createUser.nikeName}  发布的影评", createUser.avatarUrl, createUser.createTime, createUser.followed, true, createUser.score,userAuthType = createUser.authType)
    }

    /**
     * 加载影评详情
     */
    fun loadReviewDetail(contentId: Long, type: Long, recId:Long) {
        viewModelScope.launch(main) {
            reviewDetailUIModel.emitUIState(showLoading = true)

            val result = withOnIO {
                repo.loadReviewDetail(contentId, type, recId,false)
            }

            checkResult(result, error = { reviewDetailUIModel.emitUIState(error = it) },
                    netError = { reviewDetailUIModel.emitUIState(netError = it) },
                    success = { reviewDetailUIModel.emitUIState(success = it)},
                    empty = {reviewDetailUIModel.emitUIState(isEmpty = true)})
        }
    }
}