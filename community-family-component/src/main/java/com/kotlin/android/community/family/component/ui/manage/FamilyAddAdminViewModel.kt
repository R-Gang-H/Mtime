package com.kotlin.android.community.family.component.ui.manage

import androidx.lifecycle.LiveData
import androidx.lifecycle.viewModelScope
import com.kotlin.android.api.base.BaseUIModel
import com.kotlin.android.community.family.component.repository.FamilyManageRepository
import com.kotlin.android.core.BaseViewModel
import com.kotlin.android.app.data.entity.common.CommonResult
import com.kotlin.android.app.data.entity.community.group.GroupUserList
import kotlinx.coroutines.launch

/**
 * @author vivian.wei
 * @date 2020/8/12
 * @desc 家族_添加管理员列表页ViewModel
 */
class FamilyAddAdminViewModel: BaseViewModel() {

    companion object {
        const val PAGE_SIZE = 10L
    }

    private val repo by lazy { FamilyManageRepository() }

    // 群组成员列表
    private val mMemberListUIModel = BaseUIModel<GroupUserList>()
    val memberListUiState = mMemberListUIModel.uiState

    // 根据用户昵称搜索成员
    private val mMemberListByNickNameUIModel = BaseUIModel<GroupUserList>()
    val memberListByNickNameUiState = mMemberListByNickNameUIModel.uiState

    // 设置管理员
    private val mSetAdminUIModel = BaseUIModel<CommonResult>()
    val setAdminUiState = mSetAdminUIModel.uiState

    /**
     * 获取群组成员列表(分页)
     */
    fun getMemberList(groupId: Long, pageIndex: Int) {
        viewModelScope.launch {

            val result = withOnIO {
                repo.getCommunityGroupMemberList(groupId, pageIndex.toLong(), PAGE_SIZE)
            }

            mMemberListUIModel.checkResultAndEmitUIState(result)
        }
    }

    /**
     * 用于在成员列表/申请者列表/黑名单列表中根据用户昵称搜索成员(名称精确查询)
     */
    fun getMemberListByNickName(groupId: Long, nickName: String, type: Long) {
        viewModelScope.launch {
            // 实时搜索不加loading

            val result = withOnIO {
                repo.getCommunityGroupMemberListByNickName(groupId, nickName, type)
            }

            mMemberListByNickNameUIModel.checkResultAndEmitUIState(result)
        }
    }

    /**
     * 设置管理员
     */
    fun setAdmin(groupId: Long, userIds: String) {
        viewModelScope.launch {
            mSetAdminUIModel.emitUIState(showLoading = true)

            val result = withOnIO {
                repo.postCommunityGroupSetAdmin(groupId, userIds)
            }

            mSetAdminUIModel.checkResultAndEmitUIState(result)
        }
    }

}