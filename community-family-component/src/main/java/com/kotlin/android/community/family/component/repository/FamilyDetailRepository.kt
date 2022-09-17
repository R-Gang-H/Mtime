package com.kotlin.android.community.family.component.repository

import androidx.collection.arrayMapOf
import com.kotlin.android.api.ApiResult
import com.kotlin.android.app.api.base.BaseRepository
import com.kotlin.android.app.data.entity.CommHasMoreList
import com.kotlin.android.app.data.entity.common.CommonResult
import com.kotlin.android.app.data.entity.community.group.GroupSectionList
import com.kotlin.android.app.data.entity.community.post.PostReleasedList
import com.kotlin.android.community.family.component.ui.details.bean.FamilyDetail
import com.kotlin.android.community.family.component.ui.details.bean.FamilyDetailMember
import com.kotlin.android.community.post.component.item.adapter.CommunityPostBinder
import com.kotlin.android.community.post.component.item.bean.CommunityPostItem
import com.kotlin.android.retrofit.getRequestBody

/**
 * @author ZhouSuQiang
 * @email zhousuqiang@126.com
 * @date 2020/8/10
 */
class FamilyDetailRepository : BaseRepository() {

    /**
     * 加载家族详情数据
     */
    suspend fun loadFamilyDetail(id: Long): ApiResult<FamilyDetail> {
        return request(
            converter = {
                FamilyDetail(
                    id = it.groupId,
                    name = it.groupName ?: "",
                    pic = it.groupImg ?: "",
                    des = it.groupDes ?: "",
                    postNumber = it.groupPostsCount,
                    memberNumber = it.groupPeopleCount,
                    userType = it.userType,
                    creator = FamilyDetailMember(
                        id = it.groupCreateUser?.userId ?: 0L,
                        name = it.groupCreateUser?.userName ?: "",
                        pic = it.groupCreateUser?.userImg ?: "",
                        userType = it.groupCreateUser?.userType ?: 0L
                    ),
                    memberList = it.groupJoinUserList?.map { user ->
                        FamilyDetailMember(
                            id = user.userId ?: 0L,
                            name = user.userName ?: "",
                            pic = user.userImg ?: "",
                            userType = user.userType ?: 0L
                        )
                    },
                    administratorCount = it.administratorCount,
                    joinPermission = it.joinPermission,
                    status = it.status,
                    uploadId = it.uploadId ?: "",
                    primaryCategoryId = it.primaryCategoryId,
                    primaryCategoryName = it.primaryCategoryName ?: "",
                    bizCode = it.bizCode,
                    posting = it.posting,
                    groupAuthority = it.groupAuthority
                )
            },
            api = {
                apiMTime.getCommunityFamilyDetail(id)
            })
    }

    /**
     * 加载当前用户在家族内未发布的帖子
     */
    suspend fun loadPostUnreleased(groupId: Long): ApiResult<List<CommunityPostBinder>> {
        return request(
            converter = {
                val list = mutableListOf<CommunityPostBinder>()
                it.items?.forEach { item ->
                    list.add(CommunityPostItem.converter2Binder(item, false))
                }
                list
            },
            api = {
                val body = getRequestBody(
                    arrayMapOf(
                        "groupId" to groupId,
                        "pageIndex" to 1L,
                        "pageSize" to 10L
                    )
                )
                apiMTime.getCommunityPostUserUnreleased(body)
            })
    }

    /**
     * 加载家族的已发布帖子
     */
    suspend fun loadPostReleased(
        groupId: Long,
        essence: Boolean,
        sort: Long,
        pageIndex: Long,
        pageSize: Long
    ): ApiResult<CommHasMoreList<CommunityPostBinder>> {
        return request(
            converter = {
                val list = mutableListOf<CommunityPostBinder>()
                it.items?.forEach { item ->
                    list.add(CommunityPostItem.converter2Binder(item))
                }
                CommHasMoreList(
                    hasMore = it.hasNext,
                    list = list
                )
            },
            api = {
                val body = getRequestBody(
                    arrayMapOf(
                        "groupId" to groupId,
                        "essence" to essence,
                        "sort" to sort,
                        "pageIndex" to pageIndex,
                        "pageSize" to pageSize
                    )
                )
                apiMTime.getCommunityPostReleased(body)
            })
    }

    /**
     * 加载家族的已发布帖子
     */
    suspend fun loadPostReleasedV2(
        groupId: Long,
        sectionId: Long,
        essence: Boolean,
        sort: Long,
        nextStamp: String? = null,
        pageSize: Long
    ): ApiResult<PostReleasedList> {
        return request(
            api = {
                val map = arrayMapOf<String, Any>(
                    "groupId" to groupId,
                    "essence" to essence,
                    "sort" to sort,
                    "nextStamp" to nextStamp.orEmpty(),
                    "pageSize" to pageSize
                )
                if (sectionId != 0L) {
                    map["sectionId"] = sectionId
                }
                val body = getRequestBody(map)
                apiMTime.getCommunityPostReleasedV2(body)
            })
    }

    /**
     * 家族分区展示
     */
    suspend fun loadFamilySection(groupId: Long): ApiResult<GroupSectionList> {
        return request {
            apiMTime.getCommunityFamilySectionList(groupId)
        }
    }

    /**
     * 家族分区添加
     */
    suspend fun addFamilySection(groupId: Long, sectionName: String): ApiResult<CommonResult> {
        return request(
            api = {
                val body = getRequestBody(
                    arrayMapOf(
                        "groupId" to groupId,
                        "sectionName" to sectionName
                    )
                )
                apiMTime.addCommunityFamilySection(body)
            }
        )
    }
}