package com.kotlin.android.community.family.component.ui.manage

import androidx.lifecycle.viewModelScope
import com.kotlin.android.api.base.BaseUIModel
import com.kotlin.android.community.family.component.repository.FamilyManageRepository
import com.kotlin.android.core.BaseViewModel
import com.kotlin.android.app.data.entity.common.CommonResult
import com.kotlin.android.app.data.entity.community.group.Group
import com.kotlin.android.app.data.entity.community.group.GroupCreateGroupCount
import kotlinx.coroutines.launch

/**
 * @author vivian.wei
 * @date 2020/8/4
 * @desc 创建|管理家族页ViewModel
 */
class FamilyCreateViewModel: BaseViewModel() {

    private val repo by lazy { FamilyManageRepository() }

    // 用户可创建多少群组
    private val mCreateGroupCountUIModel = BaseUIModel<GroupCreateGroupCount>()
    val createGroupCountUiState = mCreateGroupCountUIModel.uiState

    // 创建群组
    private val mCreateGroupUIModel = BaseUIModel<CommonResult>()
    val createGroupUiState = mCreateGroupUIModel.uiState

    // 修改群组
    private val mEditGroupUIModel = BaseUIModel<CommonResult>()
    val editGroupUiState = mEditGroupUIModel.uiState

    // 群组详情
    private val mGroupDetailUIModel = BaseUIModel<Group>()
    val groupDetailUiState = mGroupDetailUIModel.uiState

    /**
     * 用户可创建多少群组
     */
    fun getCreateMaxCount() {
        viewModelScope.launch {
            mCreateGroupCountUIModel.emitUIState(showLoading = true)

            val result = withOnIO {
                repo.getCreateGroupCount()
            }

            mCreateGroupCountUIModel.checkResultAndEmitUIState(result)
        }
    }

    /**
     * 创建群组
     */
    fun postCommunityCreateGroup(groupImg: String,
                                 groupName: String,
                                 groupDes: String,
                                 primaryCategoryId: Long,
                                 joinPermission: Long) {
        viewModelScope.launch {
            mCreateGroupUIModel.emitUIState(showLoading = true)

            val result = withOnIO {
                repo.postCommunityCreateGroup(groupImg, groupName, groupDes, primaryCategoryId,
                        joinPermission)
            }

            mCreateGroupUIModel.checkResultAndEmitUIState(result)
        }
    }

    /**
     * 修改群组
     */
    fun postCommunityEditGroup(groupId: Long,
                               groupImg: String,
                               groupName: String,
                               groupDes: String,
                               joinPermission: Long) {
        viewModelScope.launch {
            mEditGroupUIModel.emitUIState(showLoading = true)

            val result = withOnIO {
                repo.postCommunityEditGroup(groupId, groupImg, groupName, groupDes, joinPermission)
            }

            mEditGroupUIModel.checkResultAndEmitUIState(result)
        }
    }

    /**
     * 加载群组详情
     */
    fun getGroupDetail(groupId: Long) {
        viewModelScope.launch {
            mGroupDetailUIModel.emitUIState(showLoading = true)

            val result = withOnIO {
                repo.getGroupDetail(groupId)
            }

            mGroupDetailUIModel.checkResultAndEmitUIState(result)
        }
    }
}