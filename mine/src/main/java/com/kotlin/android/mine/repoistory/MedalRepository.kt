package com.kotlin.android.mine.repoistory

import com.kotlin.android.api.ApiResult
import com.kotlin.android.app.api.base.BaseRepository
import com.kotlin.android.app.data.entity.community.medal.MedalData
import com.kotlin.android.app.data.entity.community.medal.MedalDetail

class MedalRepository : BaseRepository() {

    suspend fun getMedalData(): ApiResult<MedalData> {
        return request { apiMTime.getMyMedal() }
    }

    suspend fun getMedalDetail(medalId: Long): ApiResult<MedalDetail> {
        return request { apiMTime.getMyMedalDetail(medalId) }
    }

}