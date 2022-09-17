package com.kotlin.android.home.ui.toplist

import androidx.lifecycle.LiveData
import androidx.lifecycle.viewModelScope
import com.kotlin.android.api.base.BaseUIModel
import com.kotlin.android.core.BaseViewModel
import com.kotlin.android.app.data.entity.toplist.GameTopList
import com.kotlin.android.home.repository.TopListRepository
import kotlinx.coroutines.launch

/**
 * @author vivian.wei
 * @date 2020/7/20
 * @desc 游戏榜单详情页ViewModel
 */
class TopListGameDetailViewModel: BaseViewModel()  {

    private val repo by lazy { TopListRepository() }

    // 游戏榜单详情
    private val mGameTopListUIModel = BaseUIModel<GameTopList>()
    val mGameTopListUiState = mGameTopListUIModel.uiState

    /**
     * 获取游戏榜单详情
     */
    fun getIndexGameTopList(rankType: Long) {
        viewModelScope.launch {
            mGameTopListUIModel.emitUIState(showLoading = true)

            val result = withOnIO {
                repo.getRichmanTopUserList(rankType)
            }

            mGameTopListUIModel.checkResultAndEmitUIState(result)
        }
    }

}