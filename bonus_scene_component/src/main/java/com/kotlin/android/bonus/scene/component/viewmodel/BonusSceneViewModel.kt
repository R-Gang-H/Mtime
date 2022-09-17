package com.kotlin.android.bonus.scene.component.viewmodel

import androidx.lifecycle.viewModelScope
import com.kotlin.android.api.base.BaseUIModel
import com.kotlin.android.bonus.scene.component.repository.BonusSceneRepository
import com.kotlin.android.core.BaseViewModel
import com.kotlin.android.app.data.entity.bonus.BonusScene
import kotlinx.coroutines.launch

/**
 * create by lushan on 2020/12/29
 * description:开彩蛋
 */
class BonusSceneViewModel:BaseViewModel() {
    private val repo by lazy {
        BonusSceneRepository()
    }

    private val bonusUIModel = BaseUIModel<BonusScene>()
    val bonusState = bonusUIModel.uiState


    fun getBonusScene(action:Long){
        viewModelScope.launch(main){
            bonusUIModel.emitUIState(showLoading = true)
            val result = withOnIO {
                repo.getBonusScene(action)
            }

            bonusUIModel.checkResultAndEmitUIState(result)
        }
    }
}