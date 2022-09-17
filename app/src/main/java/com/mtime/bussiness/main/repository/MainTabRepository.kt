package com.mtime.bussiness.main.repository

import com.kotlin.android.api.ApiResult
import com.kotlin.android.app.api.base.BaseRepository
import com.kotlin.android.app.data.entity.bonus.PopupBonusScene

/**
 * Created by suq on 2022/4/7
 * des:
 */
class MainTabRepository : BaseRepository() {

    suspend fun checkPopupBonus(action: Long): ApiResult<PopupBonusScene> {
        return request {
            apiMTime.checkPopupBonus(action)
        }
    }
}