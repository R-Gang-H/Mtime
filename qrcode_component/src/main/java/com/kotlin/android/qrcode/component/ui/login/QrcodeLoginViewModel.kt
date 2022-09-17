package com.kotlin.android.qrcode.component.ui.login

import androidx.lifecycle.viewModelScope
import com.kotlin.android.api.base.BaseUIModel
import com.kotlin.android.core.BaseViewModel
import com.kotlin.android.app.data.entity.common.CommBizCodeResult
import com.kotlin.android.qrcode.component.repository.QrcodeLoginRepository
import kotlinx.coroutines.launch

/**
 * @author ZhouSuQiang
 * @email zhousuqiang@126.com
 * @date 2020/12/25
 */
class QrcodeLoginViewModel : BaseViewModel() {
    private val repo by lazy { QrcodeLoginRepository() }

    private val mUIModel = BaseUIModel<CommBizCodeResult>()
    val uiSate = mUIModel.uiState

    fun qrcodeLogin(uuid: String) {
        viewModelScope.launch {
            mUIModel.emitUIState(showLoading = true)

            val result = withOnIO {
                repo.qrcodeLogin(uuid)
            }

            mUIModel.checkResultAndEmitUIState(result)
        }
    }
}