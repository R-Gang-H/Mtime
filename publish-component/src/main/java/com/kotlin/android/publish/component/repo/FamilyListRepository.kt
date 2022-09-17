package com.kotlin.android.publish.component.repo

import com.kotlin.android.api.ApiResult
import com.kotlin.android.app.api.base.BaseRepository
import com.kotlin.android.app.data.entity.community.group.MyGroupList.Group
import com.kotlin.android.app.data.entity.community.publish.Footmarks

/**
 * 选择家族列表
 *
 * Created on 2020/10/14.
 *
 * @author o.s
 */
class FamilyListRepository : BaseRepository() {

    /**
     * 选择家族列表：
     * 过滤当前用户都有权限发布的家族列表
     * [userId] 不传表示当前登录用户
     */
    suspend fun loadFamilyList(userId: Long? = null): ApiResult<ArrayList<Group>> {
        return request(
            converter = {
                val groups = ArrayList<Group>()
                it.managedGroupList?.forEach { group ->
                    groups.add(group)
                }
                it.joinedGroupList?.forEach { group ->
                    groups.add(group)
                }
                groups
            }
        ) {
            apiMTime.getCommunityMyFamilyCanPostList(1L, 1000L)
        }
    }


    /**
     * 种草家族列表
     */
    suspend fun getGroupFootmarks(): ApiResult<Footmarks> {
        return request {
            apiMTime.getGroupFootmarks()
        }
    }

}