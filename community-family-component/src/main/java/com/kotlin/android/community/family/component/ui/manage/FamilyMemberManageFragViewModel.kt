package com.kotlin.android.community.family.component.ui.manage

import androidx.lifecycle.viewModelScope
import com.kotlin.android.api.base.BaseUIModel
import com.kotlin.android.api.base.call
import com.kotlin.android.community.family.component.repository.FamilyManageRepository
import com.kotlin.android.community.family.component.ui.manage.FamilyMemberManageFragment.Companion.EVENT_CODE_ADD
import com.kotlin.android.community.family.component.ui.manage.FamilyMemberManageFragment.Companion.EVENT_CODE_REFUSE
import com.kotlin.android.community.family.component.ui.manage.FamilyMemberManageFragment.Companion.EVENT_CODE_REMOVE
import com.kotlin.android.core.BaseViewModel
import com.kotlin.android.app.data.entity.common.CommonResult
import com.kotlin.android.app.data.entity.community.group.GroupSectionList
import com.kotlin.android.app.data.entity.community.group.GroupUser
import com.kotlin.android.app.data.entity.community.group.GroupUserList
import kotlinx.coroutines.launch

/**
 * @author vivian.wei
 * @date 2020/8/13
 * @desc 类描述
 */
class FamilyMemberManageFragViewModel: BaseViewModel() {

    companion object {
        const val PAGE_SIZE = 10L
    }

    private val repo by lazy { FamilyManageRepository() }

    // 获取群组成员/黑名单/申请列表
    private val mMemberListUIModel = BaseUIModel<GroupUserList>()
    val memberListUiState = mMemberListUIModel.uiState

    // 用于在成员列表/申请者列表/黑名单列表中根据用户昵称搜索成员(名称精确查询)
    private val mMemberListByNickNameUIModel = BaseUIModel<GroupUserList>()
    val memberListByNickNameUiState = mMemberListByNickNameUIModel.uiState

    // 点击底部按钮
    private val mBtnEventCodeUIModel = BaseUIModel<CommonResult>()
    val btnEventCodeUiState = mBtnEventCodeUIModel.uiState

    //设置家族发布权限
    private val mPublishAuthorityUIModel = BaseUIModel<CommonResult>()
    val mPublishAuthorityUIModelState = mPublishAuthorityUIModel.uiState

    //家族分区管理--编辑
    private val mSectionEditorUIModel = BaseUIModel<CommonResult>()
    val mSectionEditorUIModelState = mSectionEditorUIModel.uiState

    //家族分区管理--删除
    private val mSectionDeleteUIModel = BaseUIModel<CommonResult>()
    val mSectionDeleteUIModelState = mSectionDeleteUIModel.uiState

    //家族分区管理--获取列表
    private val mSectionListUIModel = BaseUIModel<GroupSectionList>()
    val mSectionListUIModelState = mSectionListUIModel.uiState

    /**
     * 获取群组成员/黑名单/申请列表
     */
    fun getMemberList(groupId: Long, pageIndex: Int, type: Long) {
        viewModelScope.launch {

            val result = withOnIO {
                when(type) {
                    GroupUser.USER_TYPE_BLACK_LIST -> {
                        repo.getCommunityGroupRemoveMemberList(groupId, pageIndex.toLong(), PAGE_SIZE)
                    }
                    GroupUser.USER_TYPE_APPLY -> {
                        repo.getCommunityGroupApplicantList(groupId, pageIndex.toLong(), PAGE_SIZE)
                    }
                    else -> {
                        repo.getCommunityGroupMemberList(groupId, pageIndex.toLong(), PAGE_SIZE)
                    }
                }
            }

            mMemberListUIModel.checkResultAndEmitUIState(result)
        }
    }

    /**
     * 用于在成员列表/申请者列表/黑名单列表中根据用户昵称搜索成员(名称精确查询)
     */
    fun getCommunityGroupMemberListByNickName(groupId: Long, nickName: String, type: Long) {
        viewModelScope.launch {
            // 实时搜索不显示loading

            val result = withOnIO {
                repo.getCommunityGroupMemberListByNickName(groupId, nickName, type)
            }

            mMemberListByNickNameUIModel.checkResultAndEmitUIState(result)
        }
    }

    /**
     * 点击底部按钮
     */
    fun clickBottomBtn(groupId: Long, userIds: String, eventCode: Int) {
        viewModelScope.launch {
            mBtnEventCodeUIModel.emitUIState(true)

            val result = withOnIO {
                when(eventCode) {
                    EVENT_CODE_REMOVE -> {
                        repo.postCommunityGroupRemoveMember(groupId, userIds)
                    }
                    EVENT_CODE_ADD -> {
                        repo.postCommunityGroupAddMember(groupId, userIds)
                    }
                    EVENT_CODE_REFUSE -> {
                        repo.postCommunityGroupRefuseMember(groupId, userIds)
                    }
                    else -> {
                        repo.postCommunityGroupRestoreMember(groupId, userIds)
                    }
                }
            }

            mBtnEventCodeUIModel.checkResultAndEmitUIState(result)
        }
    }

    /**
     * 设置家族发布权限
     */
    fun setAuthority(groupId: Long, groupAuthority: Long){
        call(uiModel = mPublishAuthorityUIModel){
            repo.postSetPublishAuthority(groupId,groupAuthority)
        }
    }

    /**
     * 获取分区列表数据
     * @param groupId 家族id
     */
    fun getSectionList(groupId: Long){
        call(uiModel = mSectionListUIModel){
            repo.getSectionList(groupId)
        }
    }

    /**
     * 删除分区数据
     */
    fun delSectionItem(groupId: Long,sectionId:Long){
        call(uiModel = mSectionDeleteUIModel){
            repo.delSectionList(groupId,sectionId)
        }
    }

    fun editorSectionItem(groupId: Long,sectionId:Long,sectionName:String){
        call(uiModel = mSectionEditorUIModel){
            repo.editorSectionList(groupId, sectionId, sectionName)
        }
    }

}