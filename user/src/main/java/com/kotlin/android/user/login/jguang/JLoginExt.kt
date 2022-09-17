package com.kotlin.android.user.login.jguang

import android.content.Context
import android.os.Bundle
import android.text.TextUtils
import com.jeremyliao.liveeventbus.LiveEventBus
import com.kotlin.android.core.CoreApp
import com.kotlin.android.app.data.entity.user.Login
import com.kotlin.android.ktx.ext.log.e
import com.kotlin.android.ktx.ext.store.getSpValue
import com.kotlin.android.router.KEY_LOGIN_NAVIGATION_PATH
import com.kotlin.android.router.RouterManager
import com.kotlin.android.router.liveevent.LOGIN_STATE
import com.kotlin.android.app.router.liveevent.event.LoginState
import com.kotlin.android.user.*

/**
 * 极光一键登录扩展
 *
 * Created on 2021/4/9.
 *
 * @author o.s
 */


/**
 * 极光授权登录
 */
fun jLoginAuth(
        context: Context? = null,
        bundle: Bundle? = null,
        requestCode: Int = -1,
        otherLogin: (() -> Unit)? = null
) {
    "jLoginAuth 极光授权登录".e()
    var count = 0 // 登录尝试次数计数。最多尝试3次
    JLoginManager.instance.loginAuth(
            failure = {
                count++
                JLoginSupport.instance.authFailure(it, otherLogin)
            },
            cancel = {
                count++
                JLoginSupport.instance.authCancel()
            },
            success = {
                count++
                "jLoginAuth success $this".e()
                token?.apply {
                    JLoginSupport.instance.jVerifyLogin(
                            loginToken = this,
                            netError = {
                                JLoginSupport.instance.netError(it, count, otherLogin)
                            },
                            error = {
                                JLoginSupport.instance.loginFailure(it, count, otherLogin)
                            },
                            success = {
                                JLoginSupport.instance.loginSuccess(this, count, otherLogin) {
                                    loginSuccess(it, context, bundle, requestCode)
                                }
                            }
                    )
                }
            }
    )
}

fun jLoginAuthUIConfig(otherLogin: (() -> Unit)? = null) {
    JLoginManager.instance.setCustomUIWithConfig(
            userAgreementUrl = userAgreementUrl,
            privacyPolicyUrl = privacyPolicyUrl,
            otherLogin = otherLogin
    )
}

fun loginSuccess(
        login: Login,
        context: Context? = null,
        bundle: Bundle? = null,
        requestCode: Int = -1,
) {
    // 登录成功
    UserManager.instance.update(login.user, login.hasPassword)
    // 发送事件
    LiveEventBus.get(LOGIN_STATE).post(
        com.kotlin.android.app.router.liveevent.event.LoginState(true).apply {
        // 一键登录成功后在此直接跳转到目标页面，无需传递bundle到下一页
//        this.context = WeakReference(context)
//        this.bundle = bundle
//        this.requestCode = requestCode
    })
    if (bundle != null) {
        // 一键登录成功后在此直接跳转到 [KEY_LOGIN_NAVIGATION_PATH] 指定的目标页面
        val path = bundle.getString(KEY_LOGIN_NAVIGATION_PATH)
        if (null != path && path.isNotEmpty()) {
            RouterManager.instance.navigation(
                path = path,
                bundle = bundle,
                context = context,
                requestCode = requestCode
            )
        }
    }
}

/**
 * 用户协议
 */
val userAgreementUrl: String
    get() = CoreApp.instance.getSpValue(KEY_USER_AGREEMENT_URL, USER_AGREEMENT_URL, SP_DEFAULT_NAME)

/**
 * 隐私条款
 */
val privacyPolicyUrl: String
    get() = PRIVACY_POLICY_URL