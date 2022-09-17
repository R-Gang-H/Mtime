package com.kotlin.android.mine.repoistory

import com.kotlin.android.api.ApiResult
import com.kotlin.android.app.api.base.BaseRepository
import com.kotlin.android.app.data.entity.mine.SuccessErrorResultBean

class NickNameRepository : BaseRepository() {

    suspend fun postNickName(nickname: String): ApiResult<SuccessErrorResultBean> {
        return request { apiMTime.getSaveOrder(nickname) }
    }

}