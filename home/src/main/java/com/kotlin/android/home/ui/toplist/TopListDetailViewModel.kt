package com.kotlin.android.home.ui.toplist

import androidx.lifecycle.viewModelScope
import com.kotlin.android.api.base.BaseUIModel
import com.kotlin.android.comment.component.DetailBaseViewModel
import com.kotlin.android.app.data.entity.common.WantToSeeResult
import com.kotlin.android.app.data.entity.toplist.TopListInfo
import com.kotlin.android.home.repository.TopListRepository
import kotlinx.coroutines.launch

/**
 * @author vivian.wei
 * @date 2020/7/15
 * @desc 电影/影人榜单详情页ViewModel
 */
class TopListDetailViewModel: DetailBaseViewModel() {

    private val repo by lazy { TopListRepository() }

    // 获取榜单详情
    private val mTopListInfoUIModel = BaseUIModel<TopListInfo>()
    val topListUiState = mTopListInfoUIModel.uiState

    // 想看
    private val mWantSeeUIModel = BaseUIModel<WantToSeeResult>()
    val wantSeeUiState = mWantSeeUIModel.uiState

    val topListUiStateSuccess = topListUiState.value?.success

    /**
     * 获取榜单详情
     */
    fun getTopListDetail(id: Long) {
        viewModelScope.launch {
            mTopListInfoUIModel.emitUIState(showLoading = true)

            val result = withOnIO {
                repo.getTopListDetail(id)
            }

            mTopListInfoUIModel.checkResultAndEmitUIState(result)
        }
    }

    /**
     * 想看/取消想看
     */
    fun setWantSee(movieId: Long, flag: Long, year: Long) {
        viewModelScope.launch {
            mWantSeeUIModel.emitUIState(showLoading = true)

            val result = withOnIO {
                    repo.getMovieWantToSee(movieId, flag, year)
            }

            mWantSeeUIModel.checkResultAndEmitUIState(result)
        }
    }

}