package com.kotlin.android.live.component.ui.fragment.livedetail

import androidx.lifecycle.LiveData
import androidx.lifecycle.viewModelScope
import com.kotlin.android.api.base.BaseUIModel
import com.kotlin.android.core.BaseViewModel
import com.kotlin.android.app.data.entity.live.LiveNewsList
import com.kotlin.android.live.component.repository.LiveDetailRepository
import kotlinx.coroutines.launch

/**
 * create by vivian.wei on 2021/3/2
 * description:直播详情fragment viewModel
 */
class LiveDetailFragmentViewModel: BaseViewModel() {

    private val mRepo by lazy { LiveDetailRepository() }

    // 短影评列表
    private val mNewsListUIModel = BaseUIModel<LiveNewsList>()
    val mNewsListUiState = mNewsListUIModel.uiState

    /**
     * 获取直播资讯列表
     */
    fun getLiveNews(liveId: Long) {
        viewModelScope.launch {

            val result = withOnIO {
                mRepo.getLiveNews(liveId)
            }

            mNewsListUIModel.checkResultAndEmitUIState(result)
        }
    }

}