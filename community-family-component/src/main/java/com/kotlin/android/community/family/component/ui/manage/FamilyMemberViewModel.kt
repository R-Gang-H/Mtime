package com.kotlin.android.community.family.component.ui.manage

import androidx.lifecycle.LiveData
import androidx.lifecycle.viewModelScope
import com.kotlin.android.api.base.BaseUIModel
import com.kotlin.android.community.family.component.repository.FamilyManageRepository
import com.kotlin.android.core.BaseViewModel
import com.kotlin.android.app.data.entity.common.CommBizCodeResult
import com.kotlin.android.app.data.entity.community.group.GroupAdminActiveMemberList
import com.kotlin.android.app.data.entity.community.group.GroupUserList
import kotlinx.coroutines.launch

/**
 * @author vivian.wei
 * @date 2020/8/12
 * @desc 家族成员列表页ViewModel
 */
class FamilyMemberViewModel: BaseViewModel() {

    companion object {
        const val PAGE_SIZE = 10L        // 每页条数
        const val ACTION_FOLLOW = 1L    // 关注
    }

    private val repo by lazy { FamilyManageRepository() }

    // 群主管理员列表+最近活跃列表
    private val mAdminActiveListUIModel = BaseUIModel<GroupAdminActiveMemberList>()
    val adminActiveListUiState = mAdminActiveListUIModel.uiState

    // 最新成员列表
    private val mMemberListUIModel = BaseUIModel<GroupUserList>()
    val memberListUiState = mMemberListUIModel.uiState

    // 关注
    private val followUIModel = BaseUIModel<CommBizCodeResult>()
    val followUiState = followUIModel.uiState

    /**
     * 群主管理员列表+最近活跃列表
     */
    fun getAdminActiveMemberList(groupId: Long) {
        viewModelScope.launch {

            mAdminActiveListUIModel.emitUIState(true)

            val result = withOnIO {
                repo.getCommunityGroupAdminAndActiveMemberList(groupId)
            }

            mAdminActiveListUIModel.checkResultAndEmitUIState(result)
        }
    }

    /**
     * 群组成员列表
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
     * 关注用户
     */
    fun follow(userId: Long) {
        viewModelScope.launch(main) {
            followUIModel.emitUIState(showLoading = true)

            val result = withOnIO {
                repo.followUser(ACTION_FOLLOW, userId)
            }

            followUIModel.checkResultAndEmitUIState(result)
        }
    }

}