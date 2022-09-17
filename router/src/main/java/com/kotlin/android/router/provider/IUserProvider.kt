package com.kotlin.android.router.provider

import android.content.Context
import android.os.Bundle
import com.kotlin.android.router.USER_PROVIDER_PATH
import com.kotlin.android.router.annotation.RouteProvider

/**
 *
 * Created on 2021/4/16.
 *
 * @author o.s
 */
@RouteProvider(path = USER_PROVIDER_PATH)
interface IUserProvider : IBaseProvider {

    fun startLoginPage(
            context: Context? = null,
            bundle: Bundle? = null,
            requestCode: Int? = null
    )

    fun isLogin(): Boolean

    fun isSelf(userId: Long): Boolean

    /**
     * 清除用户信息
     */
    fun clearUser()

    /**
     * 获取userId
     */
    fun getUserId():Long
}