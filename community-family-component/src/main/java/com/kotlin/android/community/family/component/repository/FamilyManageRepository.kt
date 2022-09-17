package com.kotlin.android.community.family.component.repository

import androidx.collection.arrayMapOf
import com.kotlin.android.api.ApiResult
import com.kotlin.android.app.api.base.BaseRepository
import com.kotlin.android.app.data.entity.common.CommBizCodeResult
import com.kotlin.android.app.data.entity.common.CommonResult
import com.kotlin.android.app.data.entity.community.group.*
import com.kotlin.android.retrofit.getRequestBody

/**
 * @author vivian.wei
 * @date 2020/9/3
 * @desc 家族管理Repository
 */
class FamilyManageRepository : BaseRepository() {

    /**
     * 用户可创建多少群组
     */
    suspend fun getCreateGroupCount(): ApiResult<GroupCreateGroupCount> {
        return request {
            apiMTime.getCommunityCreateGroupCount()
        }
    }

    /**
     * 创建群组
     *
     * @param groupImg              群组封面
     * @param groupName              群组名称
     * @param groupDes              群组简介
     * @param primaryCategoryId      群组一级分类ID
     * @param joinPermission      加入权限 （1-自由加入 2-需要审核）
     */
    suspend fun postCommunityCreateGroup(
        groupImg: String,
        groupName: String,
        groupDes: String,
        primaryCategoryId: Long,
        joinPermission: Long
    ): ApiResult<CommonResult> {
        return request {
            apiMTime.postCommunityCreateGroup(
                groupImg, groupName, groupDes,
                primaryCategoryId, joinPermission
            )
        }
    }

    /**
     * 修改群组
     *
     * @param groupId              群组id
     * @param groupImg              群组封面
     * @param groupName              群组名称
     * @param groupDes              群组简介
     * @param joinPermission      加入权限 （1-自由加入 2-需要审核）
     */
    suspend fun postCommunityEditGroup(
        groupId: Long,
        groupImg: String,
        groupName: String,
        groupDes: String,
        joinPermission: Long
    ): ApiResult<CommonResult> {
        return request {
            apiMTime.postCommunityEditGroup(
                groupId, groupImg, groupName, groupDes,
                joinPermission
            )
        }
    }

    /**
     * 加载家族详情
     *
     * @param id 群组Id
     */
    suspend fun getGroupDetail(id: Long): ApiResult<Group> {
        return request {
            apiMTime.getCommunityFamilyDetail(id)
        }
    }

    /**
     * 群组一级分类列表
     */
    suspend fun getCommunityGroupCategory(): ApiResult<GroupCategoryList> {
        return request {
            apiMTime.getCommunityFamilyClass()
        }
    }

    /**
     * 群组管理员列表
     *
     * @param groupId 群组Id
     */
    suspend fun getCommunityGroupAdminList(groupId: Long): ApiResult<GroupUserList> {
        return request {
            apiMTime.getCommunityGroupAdminList(groupId)
        }
    }

    /**
     * 取消管理员
     * @param groupId 群组Id
     * @param userIds 被取消管理员的用户ID 多个","隔开
     */
    suspend fun postCommunityGroupUnsetAdmin(groupId: Long, userIds: String):
            ApiResult<CommonResult> {
        return request {
            apiMTime.postCommunityGroupUnsetAdmin(groupId, userIds)
        }
    }

    /**
     * 设置管理员
     * @param groupId 群组Id
     * @param userIds 被设置管理员的用户ID  多个","隔开
     */
    suspend fun postCommunityGroupSetAdmin(groupId: Long, userIds: String):
            ApiResult<CommonResult> {
        return request {
            apiMTime.postCommunityGroupSetAdmin(groupId, userIds)
        }
    }

    /**
     * 设置家族发布权限
     * @param groupId 群组Id
     * @param groupAuthority 必传：1-成员加入发帖及评论，2-所有人任意发帖评论，3-管理员任意发帖评论
     */
    suspend fun postSetPublishAuthority(groupId: Long, groupAuthority: Long):
            ApiResult<CommonResult> {
        return request {
            val body = getRequestBody(
                arrayMapOf(
                    "groupId" to groupId,
                    "groupAuthority" to groupAuthority
                )
            )
            apiMTime.postCommunitySetAuthority(body)
        }
    }

    /**
     * 群组成员列表
     *
     * @param groupId        Number  群组id
     * @param pageIndex        Number
     * @param pageSize        Number
     */
    suspend fun getCommunityGroupMemberList(groupId: Long, pageIndex: Long, pageSize: Long):
            ApiResult<GroupUserList> {
        return request {
            apiMTime.getCommunityGroupMemberList(groupId, pageIndex, pageSize)
        }
    }

    /**
     * 群组黑名单列表
     *
     * @param groupId        Number  群组id
     * @param pageIndex        Number
     * @param pageSize        Number
     */
    suspend fun getCommunityGroupRemoveMemberList(groupId: Long, pageIndex: Long, pageSize: Long):
            ApiResult<GroupUserList> {
        return request {
            apiMTime.getCommunityGroupRemoveMemberList(groupId, pageIndex, pageSize)
        }
    }

    /**
     * 群组申请列表
     *
     * @param groupId        Number  群组id
     * @param pageIndex        Number
     * @param pageSize        Number
     */
    suspend fun getCommunityGroupApplicantList(groupId: Long, pageIndex: Long, pageSize: Long):
            ApiResult<GroupUserList> {
        return request {
            apiMTime.getCommunityGroupApplicantList(groupId, pageIndex, pageSize)
        }
    }

    /**
     * 群主管理员列表+最近活跃列表
     *
     * @param groupId   Number  群组id
     */
    suspend fun getCommunityGroupAdminAndActiveMemberList(groupId: Long):
            ApiResult<GroupAdminActiveMemberList> {
        return request {
            apiMTime.getCommunityGroupAdminAndActiveMemberList(groupId)
        }
    }

    /**
     * 用于在成员列表/申请者列表/黑名单列表中根据用户昵称搜索成员。(名称精确查询)
     *
     * @param groupId        Number  群组id
     * @param nickName        String
     * @param type            Number  类型 -1:申请者 1：群主 2：管理员 3：普通成员 4：黑名单
     *                                  （1、2分别是群主和管理员，暂时没有昵称搜索功能）
     */
    suspend fun getCommunityGroupMemberListByNickName(groupId: Long, nickName: String, type: Long):
            ApiResult<GroupUserList> {
        return request {
            apiMTime.getCommunityGroupMemberListByNickName(groupId, nickName, type)
        }
    }

    /**
     * 群组移除成员
     * @param groupId 群组Id
     * @param userIds 被设置管理员的用户ID  多个","隔开
     */
    suspend fun postCommunityGroupRemoveMember(groupId: Long, userIds: String):
            ApiResult<CommonResult> {
        return request {
            apiMTime.postCommunityGroupRemoveMember(groupId, userIds)
        }
    }

    /**
     * 释放群组黑名单中的成员
     * @param groupId 群组Id
     * @param userIds 被设置管理员的用户ID  多个","隔开
     */
    suspend fun postCommunityGroupRestoreMember(groupId: Long, userIds: String):
            ApiResult<CommonResult> {
        return request {
            apiMTime.postCommunityGroupRestoreMember(groupId, userIds)
        }
    }

    /**
     * 批准用户加入群组
     * @param groupId 群组Id
     * @param userIds 被设置管理员的用户ID  多个","隔开
     */
    suspend fun postCommunityGroupAddMember(groupId: Long, userIds: String):
            ApiResult<CommonResult> {
        return request {
            apiMTime.postCommunityGroupAddMember(groupId, userIds)
        }
    }

    /**
     * 拒绝加入群组申请
     * @param groupId 群组Id
     * @param userIds 被设置管理员的用户ID  多个","隔开
     */
    suspend fun postCommunityGroupRefuseMember(groupId: Long, userIds: String):
            ApiResult<CommonResult> {
        return request {
            apiMTime.postCommunityGroupRefuseMember(groupId, userIds)
        }
    }

    /**
     * 关注用户
     */
    suspend fun followUser(action: Long, userId: Long): ApiResult<CommBizCodeResult> {
        return request { apiMTime.followUser(action, userId) }
    }

    /**
     * 获取分区列表
     */
    suspend fun getSectionList(groupId: Long): ApiResult<GroupSectionList> {
        return request { apiMTime.getCommunityFamilySectionList(groupId) }
    }

    /**
     * 编辑分区
     */
    suspend fun editorSectionList(groupId: Long,sectionId: Long,sectionName:String): ApiResult<CommonResult> {
        return request {
            val param = getRequestBody(
                arrayMapOf(
                    "groupId" to groupId,
                    "sectionId" to sectionId,
                    "sectionName" to sectionName
                )
            )
            apiMTime.editorCommunityFamilySection(param)
        }
    }

    /**
     * 删除分区
     */
    suspend fun delSectionList(groupId: Long, sectionId: Long): ApiResult<CommonResult> {
        return request {
            val param = getRequestBody(
                arrayMapOf(
                    "groupId" to groupId,
                    "sectionId" to sectionId
                )
            )
            apiMTime.delCommunityFamilySection(param)
        }
    }


}