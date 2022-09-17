package com.kotlin.android.community.family.component.ui.manage

import androidx.lifecycle.viewModelScope
import com.kotlin.android.api.base.BaseUIModel
import com.kotlin.android.community.family.component.repository.FamilyManageRepository
import com.kotlin.android.core.BaseViewModel
import com.kotlin.android.app.data.entity.common.CommonResult
import com.kotlin.android.app.data.entity.community.group.GroupUserList
import kotlinx.coroutines.launch

/**
 * @author vivian.wei
 * @date 2020/8/10
 * @desc 家族管理员列表页ViewModel
 */
class FamilyAdminViewModel: BaseViewModel() {

    private val repo by lazy { FamilyManageRepository() }

    // 群组管理员列表
    private val mGroupAdminListUIModel = BaseUIModel<GroupUserList>()
    val uiState = mGroupAdminListUIModel.uiState

    // 取消管理员
    private val mUnsetAdminUIModel = BaseUIModel<CommonResult>()
    val unsetAdminUiState = mUnsetAdminUIModel.uiState

    /**
     * 群组管理员列表
     */
    fun getAdminList(groupId: Long) {
        viewModelScope.launch {
            mGroupAdminListUIModel.emitUIState(showLoading = true)

            val result = withOnIO {
                repo.getCommunityGroupAdminList(groupId)
            }

            mGroupAdminListUIModel.checkResultAndEmitUIState(result)
        }
    }

    /**
     * 移除群组管理员
     */
    fun unsetAdmin(groupId: Long, userIds: String) {
        viewModelScope.launch {
            mUnsetAdminUIModel.emitUIState(showLoading = true)

            val result = withOnIO {
                repo.postCommunityGroupUnsetAdmin(groupId, userIds)
            }

            mUnsetAdminUIModel.checkResultAndEmitUIState(result)
        }
    }

}
