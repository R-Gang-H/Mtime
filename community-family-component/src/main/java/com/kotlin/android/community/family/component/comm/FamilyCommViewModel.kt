package com.kotlin.android.community.family.component.comm

import androidx.lifecycle.viewModelScope
import com.kotlin.android.api.base.BaseUIModel
import com.kotlin.android.community.family.component.repository.FamilyCommRepository
import com.kotlin.android.core.BaseViewModel
import com.kotlin.android.app.data.entity.common.CommonResult
import com.kotlin.android.app.data.entity.common.CommonResultExtend
import com.kotlin.android.user.afterLogin
import kotlinx.coroutines.launch

/**
 * @author ZhouSuQiang
 * @email zhousuqiang@126.com
 * @date 2020/9/9
 */
open class FamilyCommViewModel<E> : BaseViewModel() {
    protected val mFamilyCommRepository by lazy { FamilyCommRepository() }

    private val mCommJoinFamilyUIModel = BaseUIModel<CommonResultExtend<CommonResult, E>>()
    val mCommJoinFamilyUISate = mCommJoinFamilyUIModel.uiState

    private val mCommOutFamilyUIModel = BaseUIModel<CommonResultExtend<CommonResult, E>>()
    val mCommOutFamilyUISate = mCommOutFamilyUIModel.uiState

    /**
     * 加入家族
     */
    fun joinFamily(id: Long, extend: E) {
        afterLogin {
            viewModelScope.launch(main) {
                mCommJoinFamilyUIModel.emitUIState(showLoading = true)

                val result = withOnIO {
                    mFamilyCommRepository.joinFamily(id, extend)
                }

                mCommJoinFamilyUIModel.checkResultAndEmitUIState(result = result)
            }
        }
    }

    /**
     * 退出家族
     */
    fun outFamily(id: Long, extend: E) {
        afterLogin {
            viewModelScope.launch(main) {
                mCommOutFamilyUIModel.emitUIState(showLoading = true)

                val result = withOnIO {
                    mFamilyCommRepository.outFamily(id, extend)
                }

                mCommOutFamilyUIModel.checkResultAndEmitUIState(result = result)
            }
        }
    }
}