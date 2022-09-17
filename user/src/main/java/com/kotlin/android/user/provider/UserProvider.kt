package com.kotlin.android.user.provider

import android.content.Context
import android.os.Bundle
import com.alibaba.android.arouter.facade.annotation.Route
import com.kotlin.android.app.router.path.RouterProviderPath
import com.kotlin.android.router.provider.IUserProvider
import com.kotlin.android.user.UserManager
import com.kotlin.android.user.login.gotoLoginPage

/**
 * user 模块导航路由提供者
 *
 * Created on 2021/4/16.
 *
 * @author o.s
 */
@Route(path = RouterProviderPath.Provider.PROVIDER_USER)
class UserProvider : IUserProvider {

    override fun startLoginPage(
            context: Context?,
            bundle: Bundle?,
            requestCode: Int?
    ) {
        gotoLoginPage(context, bundle, requestCode ?: -1)
    }

    override fun isLogin(): Boolean {
        return UserManager.instance.isLogin
    }

    override fun isSelf(userId: Long): Boolean {
        return isLogin() && userId == UserManager.instance.userId
    }

    override fun clearUser() {

    }

    override fun getUserId(): Long {
        return UserManager.instance.userId
    }
}