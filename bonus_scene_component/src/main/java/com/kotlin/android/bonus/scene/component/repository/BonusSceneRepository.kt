package com.kotlin.android.bonus.scene.component.repository

import com.kotlin.android.api.ApiResult
import com.kotlin.android.app.api.base.BaseRepository
import com.kotlin.android.app.data.entity.bonus.BonusScene

/**
 * create by lushan on 2020/12/29
 * description:
 */
class BonusSceneRepository : BaseRepository() {
    /**
     * 打开彩蛋
     */
    suspend fun getBonusScene(action: Long): ApiResult<BonusScene> {
        return request { apiMTime.bondunsScene(action) }
    }
}