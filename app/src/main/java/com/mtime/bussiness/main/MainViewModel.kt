package com.mtime.bussiness.main

import androidx.lifecycle.viewModelScope
import com.kotlin.android.api.base.BaseUIModel
import com.kotlin.android.app.data.entity.bonus.PopupBonusScene
import com.kotlin.android.core.BaseViewModel
import com.mtime.bussiness.main.repository.MainTabRepository
import kotlinx.coroutines.launch

/**
 * Created by suq on 2022/4/6
 * des:
 */
class MainViewModel : BaseViewModel() {
    private val repo by lazy { MainTabRepository() }

    private val mCheckPopupBonusUIModel = BaseUIModel<PopupBonusScene>()
    val checkPopupBonusUIState = mCheckPopupBonusUIModel.uiState

    fun checkPopupBonus(action: Long) {
        viewModelScope.launch {
            val result = withOnIO {
                repo.checkPopupBonus(action)
            }

            mCheckPopupBonusUIModel.checkResultAndEmitUIState(result)
        }
    }
}