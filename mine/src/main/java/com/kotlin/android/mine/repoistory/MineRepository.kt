package com.kotlin.android.mine.repoistory

import com.kotlin.android.api.ApiResult
import com.kotlin.android.app.api.base.BaseRepository
import com.kotlin.android.app.data.entity.activity.ActivityList
import com.kotlin.android.app.data.entity.message.UnReadMessage
import com.kotlin.android.app.data.entity.mine.AccountStatisticsInfo
import com.kotlin.android.mtime.ktx.GlobalDimensionExt
import com.kotlin.android.app.data.entity.user.User
import com.kotlin.android.mtime.ktx.GlobalDimensionExt.getDigitsCurrentCityId

/**
 * create by lushan on 2020/9/30
 * description:我的页面数据
 */
class MineRepository : BaseRepository() {
    /**
     * 获取用户统计信息
     */
    suspend fun getMineStatisticInfo(): ApiResult<AccountStatisticsInfo> {
        return request { apiMTime.getAccountStatisticInfo(GlobalDimensionExt.getDigitsCurrentCityId()) }
    }

    /**
     * 获取用户详情
     */
    suspend fun getMineUserDetail(): ApiResult<User> {
        return request {
            apiMTime.getAccountDetail(
                    param = 0L,
                    locationId = getDigitsCurrentCityId()
            )
        }
    }

    /**
     * 用户活动列表
     * 未登录用户也可以查看
     */
    suspend fun getUserActivityList(
            nextStamp: String? = null,
            pageSize: Long? = 20L
    ): ApiResult<ActivityList> {
        return request {
            apiMTime.getUserActivityList(
                nextStamp = nextStamp,
                pageSize = pageSize
            )
        }
    }
}