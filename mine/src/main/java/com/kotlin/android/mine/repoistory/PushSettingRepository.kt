package com.kotlin.android.mine.repoistory

import androidx.collection.arrayMapOf
import com.kotlin.android.api.ApiResult
import com.kotlin.android.app.api.base.BaseRepository
import com.kotlin.android.app.data.entity.mine.CommonPush
import com.kotlin.android.app.data.entity.mine.SuccessErrorResultBean
import com.kotlin.android.retrofit.getRequestBody

class PushSettingRepository : BaseRepository() {

    suspend fun getCommonPush(): ApiResult<CommonPush> {
        return request { apiMTime.getCommonPush() }
    }

    suspend fun setCommonPush(
        locationId: Long,
        isMessage: Boolean,
        isIMPush: Boolean
    ): ApiResult<SuccessErrorResultBean> {
        return request {
            val body = getRequestBody(
                arrayMapOf(
                    "locationId" to locationId,
                    "isIMPush" to isIMPush,
                    "isMessage" to isMessage,
                )
            )
            apiMTime.setCommonPush(body)
        }
    }

}