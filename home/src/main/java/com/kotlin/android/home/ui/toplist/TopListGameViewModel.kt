package com.kotlin.android.home.ui.toplist

import androidx.lifecycle.LiveData
import androidx.lifecycle.viewModelScope
import com.kotlin.android.api.base.BaseUIModel
import com.kotlin.android.core.BaseViewModel
import com.kotlin.android.app.data.entity.toplist.*
import com.kotlin.android.home.repository.TopListRepository
import kotlinx.coroutines.launch

/**
 * @author vivian.wei
 * @date 2020/7/10
 * @desc 首页_榜单游戏
 */
class TopListGameViewModel : BaseViewModel() {

    private val repo by lazy { TopListRepository() }

    private val mIndexAppGameTopListUIModel = BaseUIModel<IndexAppGameTopList>()
    val gameTopListUiState = mIndexAppGameTopListUIModel.uiState

    fun getIndexGameTopList() {
        mIndexAppGameTopListUIModel.emitUIState(showLoading = true)

        viewModelScope.launch {
            val result = withOnIO {
                repo.getIndexGameTopList()
            }

            mIndexAppGameTopListUIModel.checkResultAndEmitUIState(result)
        }
    }

}