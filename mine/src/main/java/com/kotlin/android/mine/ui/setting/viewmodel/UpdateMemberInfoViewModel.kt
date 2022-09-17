package com.kotlin.android.mine.ui.setting.viewmodel

import com.kotlin.android.api.ApiResult
import com.kotlin.android.api.base.BaseUIModel
import com.kotlin.android.api.base.call
import com.kotlin.android.app.data.entity.mine.SuccessErrorResultBean
import com.kotlin.android.app.data.entity.mine.UpdateMemberInfoBean
import com.kotlin.android.core.BaseViewModel
import com.kotlin.android.mine.repoistory.PersonalDataRepository

class UpdateMemberInfoViewModel : BaseViewModel() {

    private val repo by lazy {
        PersonalDataRepository()
    }

    private val birthUIModel: BaseUIModel<UpdateMemberInfoBean> = BaseUIModel()
    val birthState = birthUIModel.uiState

    private val signUIModel: BaseUIModel<UpdateMemberInfoBean> = BaseUIModel()
    val signDataState = signUIModel.uiState

    private val sexUIModel: BaseUIModel<SuccessErrorResultBean> = BaseUIModel()
    val sexDataState = sexUIModel.uiState

    fun updateMemberInfo(
        birth: String? = null,
        locationId: String? = null,
        userSign: String? = null,
        type: String
    ) {
//        viewModelScope.launch(main) {
//            val result = withOnIO {
//                requestData(birth, locationId, userSign, type)
//            }
//            checkResult(result, success = { personalDataUIModel.emitUIState(success = it) },
//                error = { personalDataUIModel.emitUIState(error = it) },
//                netError = { personalDataUIModel.emitUIState(netError = it) })
//        }
        var uiModel = when (type) {
            "1" -> birthUIModel
            "3" -> signUIModel
            else -> BaseUIModel()
        }
        call(uiModel = uiModel) {
            requestData(birth, locationId, userSign, type)
        }
    }

    private suspend fun requestData(
        birth: String? = null,
        locationId: String? = null,
        userSign: String? = null,
        type: String
    ): ApiResult<UpdateMemberInfoBean> {
        return repo.updateMemberInfo(birth, locationId, userSign, type)
    }

    fun updateSexInfo(sex: String) {
        call(uiModel = sexUIModel) {
            postUpdateSex(sex)
        }
    }

    private suspend fun postUpdateSex(sex: String): ApiResult<SuccessErrorResultBean> {
        return repo.updateSex(sex)
    }
}