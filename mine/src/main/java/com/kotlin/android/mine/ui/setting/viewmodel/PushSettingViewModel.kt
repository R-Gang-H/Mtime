package com.kotlin.android.mine.ui.setting.viewmodel

import com.kotlin.android.api.base.BaseUIModel
import com.kotlin.android.api.base.call
import com.kotlin.android.app.data.entity.mine.CommonPush
import com.kotlin.android.app.data.entity.mine.SuccessErrorResultBean
import com.kotlin.android.core.BaseViewModel
import com.kotlin.android.mine.repoistory.PushSettingRepository

class PushSettingViewModel : BaseViewModel() {

    private val repo by lazy {
        PushSettingRepository()
    }

    private val pushUIModel: BaseUIModel<CommonPush> = BaseUIModel()
    val pushState = pushUIModel.uiState

    private val setPushUIModel: BaseUIModel<SuccessErrorResultBean> = BaseUIModel()
    val setPushState = setPushUIModel.uiState

    fun getCommonPush() {
        call(uiModel = pushUIModel) {
            repo.getCommonPush()
        }
    }

    fun setCommonPush(locationId: Long, isMessage: Boolean, isIMPush: Boolean) {
        call(uiModel = setPushUIModel) {
            repo.setCommonPush(locationId, isMessage, isIMPush)
        }
    }

}