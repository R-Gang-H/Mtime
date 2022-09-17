package com.kotlin.android.community.family.component.repository

import com.kotlin.android.api.ApiResult
import com.kotlin.android.app.api.base.BaseRepository
import com.kotlin.android.app.data.entity.family.FindFamily

/**
 * @author zhangjian
 * @date 2022/3/18 11:00
 */
class FamilyRepository : BaseRepository() {
    /**
     * 找家族接口
     */
    suspend fun loadFindData(): ApiResult<FindFamily> {
        return request {
            apiMTime.getCommunityFamilyFindGroup()
        }
    }
}