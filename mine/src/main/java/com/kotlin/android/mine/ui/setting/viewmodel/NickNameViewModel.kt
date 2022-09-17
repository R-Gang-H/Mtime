package com.kotlin.android.mine.ui.setting.viewmodel

import androidx.lifecycle.viewModelScope
import com.kotlin.android.api.ApiResult
import com.kotlin.android.api.base.BaseUIModel
import com.kotlin.android.api.base.checkResult
import com.kotlin.android.app.data.entity.mine.SuccessErrorResultBean
import com.kotlin.android.core.BaseViewModel
import com.kotlin.android.mine.repoistory.NickNameRepository
import kotlinx.coroutines.launch

class NickNameViewModel : BaseViewModel() {

    private val repo by lazy {
        NickNameRepository()
    }

    private val nickNameUIModel: BaseUIModel<SuccessErrorResultBean> = BaseUIModel()
    val nickNameState = nickNameUIModel.uiState

    fun saveNickName(nickname: String) {
        viewModelScope.launch(main) {
            val result = withOnIO {
                requestData(nickname)
            }
            checkResult(result,
                success = { nickNameUIModel.emitUIState(success = it) },
                error = { nickNameUIModel.emitUIState(error = it) },
                netError = { nickNameUIModel.emitUIState(netError = it) })
        }
    }

    private suspend fun requestData(nickname: String): ApiResult<SuccessErrorResultBean> {
        return repo.postNickName(nickname)
    }
}