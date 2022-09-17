package com.kotlin.android.home.ui.home

import androidx.lifecycle.viewModelScope
import com.kotlin.android.api.base.BaseUIModel
import com.kotlin.android.app.data.entity.home.HomeTabNavList
import com.kotlin.android.core.BaseViewModel
import com.kotlin.android.home.repository.HomeRepository
import kotlinx.coroutines.launch

/**
 * @author ZhouSuQiang
 * @email zhousuqiang@126.com
 * @date 2020/7/7
 */
class HomeViewModel : BaseViewModel() {
    private val repo by lazy { HomeRepository() }

    private val mHomeUIModel = BaseUIModel<HomeTabNavList>()
    val uiState = mHomeUIModel.uiState

    fun loadNav(cityId: String) {
        viewModelScope.launch {
            mHomeUIModel.emitUIState(showLoading = true)

            val result = withOnIO {
                repo.loadNav(cityId)
            }

            mHomeUIModel.checkResultAndEmitUIState(result)
        }
    }
}