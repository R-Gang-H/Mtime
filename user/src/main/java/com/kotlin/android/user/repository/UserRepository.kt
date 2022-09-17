package com.kotlin.android.user.repository

import com.kotlin.android.api.ApiResult
import com.kotlin.android.app.api.base.BaseRepository
import com.kotlin.android.app.data.entity.user.Login

/**
 *
 * Created on 2021/4/9.
 *
 * @author o.s
 */
class UserRepository : BaseRepository() {

    suspend fun jVerifyLogin(loginToken: String): ApiResult<Login> {
        return request {
            apiMTime.postJVerifyLogin(loginToken)
        }
    }

}