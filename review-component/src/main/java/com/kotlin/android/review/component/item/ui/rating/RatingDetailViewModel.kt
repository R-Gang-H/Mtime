package com.kotlin.android.review.component.item.ui.rating

import androidx.lifecycle.viewModelScope
import com.kotlin.android.api.ApiResult
import com.kotlin.android.api.base.BaseUIModel
import com.kotlin.android.core.BaseViewModel
import com.kotlin.android.app.data.entity.review.RatingDetail
import com.kotlin.android.review.component.item.bean.MovieSubItemRatingBean
import com.kotlin.android.review.component.item.bean.RatingCountRatioBean
import com.kotlin.android.review.component.item.bean.RatingDetailBean
import com.kotlin.android.review.component.item.repository.MovieRatingDetailRepository
import kotlinx.coroutines.launch

/**
 * @author ZhouSuQiang
 * @email zhousuqiang@126.com
 * @date 2021/5/24
 */
class RatingDetailViewModel : BaseViewModel() {

    private val repo by lazy {
        MovieRatingDetailRepository()
    }

    private val uiModel = BaseUIModel<RatingDetailBean>()
    val uiSate = uiModel.uiState

    fun getMovieRatingDetail(movieId: Long) {
        viewModelScope.launch(main) {
            uiModel.emitUIState(showLoading = true)

            val result = withOnIO {
                repo.getMovieRatingDetail(movieId)
            }

            uiModel.checkResultAndEmitUIState(result)
        }
    }
}