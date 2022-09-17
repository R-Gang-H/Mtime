package com.kotlin.android.mine.repoistory

import com.kotlin.android.api.ApiResult
import com.kotlin.android.app.api.base.BaseRepository
import com.kotlin.android.app.data.entity.mine.CreatorTaskEntity
import com.kotlin.android.app.data.entity.mine.RewardEntity

/**
 * 创作者中心 - 任务中心和权益说明
 */
class CreatorRepository : BaseRepository() {
    /**
     * 权益说明页面
     */
    suspend fun getCreatorReward(): ApiResult<RewardEntity> {
        return request {
            apiMTime.getCreatorReward()
        }
    }

    /**
     * 任务中心页面
     */
    suspend fun getCreatorTask(): ApiResult<CreatorTaskEntity> {
        return request {
            apiMTime.getCreatorTaskInfo()
        }
    }
}