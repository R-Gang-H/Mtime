package com.kotlin.android.mine.repoistory

import com.kotlin.android.api.ApiResult
import com.kotlin.android.app.api.base.BaseRepository
import com.kotlin.android.app.data.entity.mine.SuccessErrorResultBean
import com.kotlin.android.app.data.entity.mine.UpdateMemberInfoBean

class PersonalDataRepository : BaseRepository() {

    suspend fun updateMemberInfo(
        birth: String? = null,
        locationId: String? = null,
        userSign: String? = null,
        type: String
    ): ApiResult<UpdateMemberInfoBean> {
        return request {
            apiMTime.updateMemberInfo(
                birthday = birth,
                locationId = locationId,
                userSign = userSign,
                type = type
            )
        }
    }

    suspend fun updateSex(sex: String): ApiResult<SuccessErrorResultBean> {
        return request { apiMTime.updateSex(sex = sex) }
    }

}