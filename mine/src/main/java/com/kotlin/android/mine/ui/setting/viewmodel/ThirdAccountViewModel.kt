package com.kotlin.android.mine.ui.setting.viewmodel

import android.content.Context
import com.kotlin.android.api.base.BaseUIModel
import com.kotlin.android.api.base.call
import com.kotlin.android.app.data.entity.mine.SuccessErrorResultBean
import com.kotlin.android.core.BaseViewModel
import com.kotlin.android.mine.bean.ThirdAccountViewBean
import com.kotlin.android.mine.repoistory.ThirdAccountRepository

class ThirdAccountViewModel : BaseViewModel() {

    private val repo by lazy {
        ThirdAccountRepository()
    }

    private val bindUIModel: BaseUIModel<ThirdAccountViewBean> = BaseUIModel()
    val bindState = bindUIModel.uiState

    private val unbindUIModel: BaseUIModel<SuccessErrorResultBean> = BaseUIModel()
    val unbindState = unbindUIModel.uiState

    fun getBindList(context: Context) {
        call(uiModel = bindUIModel) {
            repo.getBindList(context)
        }
    }

    fun unbindAccount(platformId: Int) {
        call(uiModel = unbindUIModel) {
            repo.userUnbind(platformId)
        }
    }

}