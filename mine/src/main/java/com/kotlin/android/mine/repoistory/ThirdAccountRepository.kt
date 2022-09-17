package com.kotlin.android.mine.repoistory

import android.content.Context
import com.kotlin.android.api.ApiResult
import com.kotlin.android.app.api.base.BaseRepository
import com.kotlin.android.app.data.entity.mine.SuccessErrorResultBean
import com.kotlin.android.mine.bean.ThirdAccountViewBean

class ThirdAccountRepository : BaseRepository() {

    suspend fun getBindList(context: Context): ApiResult<ThirdAccountViewBean> {
        return request(
            converter = {
                ThirdAccountViewBean.obtain(it, context = context)
            }) { apiMTime.getUserBind() }
    }

    suspend fun userUnbind(platformId: Int): ApiResult<SuccessErrorResultBean> {
        return request { apiMTime.userUnbind(platformId) }
    }

}