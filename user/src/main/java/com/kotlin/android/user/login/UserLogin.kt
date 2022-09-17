package com.kotlin.android.user.login

import android.app.Activity
import android.content.Context
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import com.kotlin.android.ktx.ext.log.e
import com.kotlin.android.router.ext.getProvider
import com.kotlin.android.app.router.path.RouterProviderPath
import com.kotlin.android.app.router.provider.main.IMainProvider
import com.kotlin.android.user.login.jguang.jLoginAuth
import com.kotlin.android.user.login.jguang.jLoginAuthUIConfig

/**
 * 用户登录接口(user/login.api）
 *
 * Created on 2021/4/9.
 *
 * @author o.s
 */

/**
 * 跳转到登录页。
 * 兼容 startActivityForResult 模式，需要满足两个条件：
 *  1，[context] 是 [Activity]
 *  2，[requestCode] 不为 null
 */
fun gotoLoginPage(
        context: Context? = null,
        bundle: Bundle? = null,
        requestCode: Int? = null,
) {
    "gotoLoginPage 跳转登录页".e()
    Looper.getMainLooper()?.apply {
        Handler(this).post {
            jLoginAuthUIConfig {
                gotoOtherLoginPage(context, bundle, requestCode)
            }
            jLoginAuth(context, bundle, requestCode ?: -1) {
                gotoOtherLoginPage(context, bundle, requestCode)
            }
        }
    }
}

/**
 * 跳转到其他登录页面（常规登录页）
 * [Activity.startActivityForResult] 的跳转行为需要确保 [context] 和 [requestCode] 不为空，并且 [context] 为 [Activity]。
 * 否则执行 [Activity.startActivity] 的跳转行为
 */
fun gotoOtherLoginPage(
        context: Context? = null,
        bundle: Bundle? = null,
        requestCode: Int? = null
) {
    "gotoOtherLoginPage 跳转普通登录页".e()
    if (requestCode != null && context != null && context is Activity) {
        getProvider(IMainProvider::class.java)?.startNormalLoginActivityForResult(context, bundle, requestCode)
    } else {
        getProvider(IMainProvider::class.java)?.startNormalLoginActivity(bundle)
    }
}