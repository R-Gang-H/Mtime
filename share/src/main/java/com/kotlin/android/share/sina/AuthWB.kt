package com.kotlin.android.share.sina

import android.app.Activity
import android.content.Intent
import com.kotlin.android.data.auth.AuthLogin
import com.kotlin.android.ktx.ext.log.d
import com.kotlin.android.mtime.ktx.ext.showToast
import com.kotlin.android.share.R
import com.kotlin.android.share.ShareEnv
import com.kotlin.android.share.auth.AuthPlatform
import com.kotlin.android.share.auth.AuthState
import com.kotlin.android.share.entity.AuthEntity
import com.sina.weibo.sdk.auth.*
import com.sina.weibo.sdk.common.UiError
import com.sina.weibo.sdk.openapi.IWBAPI
import com.sina.weibo.sdk.openapi.WBAPIFactory

/**
 * 微博授权：
 * AuthInfo维护了授权需要的基本信息，APP_KEY(开发平台生成的唯一key)、Redirect URI (授权回调)、SCOPE(需要请求的权限功能，默认参考 [ShareEnv.SCOPE] 中数据)。
 *
 * Created on 2020/7/8.
 *
 * @author o.s
 */
class AuthWB(
        private val context: Activity,
        private val appId: String,
        redirectUrl: String,
        scope: String = ShareEnv.SCOPE,
        private val event: ((state: AuthEntity) -> Unit)? = null
) {

    lateinit var api: IWBAPI
    private var mToken: Oauth2AccessToken = Oauth2AccessToken()

    init {
        api = WBAPIFactory.createWBAPI(context)
        api.registerApp(context, AuthInfo(context, appId, redirectUrl, scope))
    }

    /**
     * 授权监听
     */
    private val authListener = object : WbAuthListener {
        override fun onComplete(token: Oauth2AccessToken?) {
            context.runOnUiThread {
                mToken = token ?: Oauth2AccessToken()
//                if (mToken.isSessionValid) {
//                    AccessTokenKeeper.writeAccessToken(context, mToken)
//                }
                showToast(context, R.string.auth_success)
                event?.invoke(AuthEntity(
                        state = AuthState.SUCCESS,
                        platform = AuthPlatform.WEI_BO,
                        wbAuthData = token,
                        authLogin = AuthLogin(
                                platformId = AuthPlatform.WEI_BO.id,
                                accessToken = token?.accessToken
                        )
                ))
            }
        }

        override fun onError(error: UiError?) {
            error?.errorMessage?.apply {
                showToast(context, this)
            } ?: showToast(context, R.string.auth_fail)
            event?.invoke(AuthEntity(
                    state = AuthState.FAILURE,
                    platform = AuthPlatform.WEI_BO
            ))
        }

        override fun onCancel() {
            showToast(context, R.string.auth_cancel)
            event?.invoke(AuthEntity(
                    state = AuthState.FAILURE,
                    platform = AuthPlatform.WEI_BO
            ))
        }

    }

    /**
     * 请求授权: all In one方式授权
     * 注：此种授权方式会根据手机是否安装微博客户端来决定使用sso授权还是网页授权，
     * 如果安装有微博客户端 则调用微博客户端授权，否则调用Web页面方式授权 参见pdf文档说明
     */
    fun auth() {
        if (mToken.isSessionValid) {
            // 当前 Token 仍在有效期内，无需再次登录。
            event?.invoke(AuthEntity(
                    state = AuthState.SUCCESS,
                    platform = AuthPlatform.WEI_BO,
                    wbAuthData = mToken,
                    authLogin = AuthLogin(
                            platformId = AuthPlatform.WEI_BO.id,
                            accessToken = mToken.accessToken
                    )
            ))
            mToken.d()
            showToast("当前 Token 仍在有效期内，无需再次登录")
            return
        }
        api.authorize(authListener)
    }

    /**
     * 请求授权: Web 授权
     */
    fun authWeb() {
        if (mToken.isSessionValid) {
            // 当前 Token 仍在有效期内，无需再次登录。
            event?.invoke(AuthEntity(
                    state = AuthState.SUCCESS,
                    platform = AuthPlatform.WEI_BO,
                    wbAuthData = mToken,
                    authLogin = AuthLogin(
                            platformId = AuthPlatform.WEI_BO.id,
                            accessToken = mToken.accessToken
                    )
            ))
            return
        }
        api.authorizeWeb(authListener)
    }

    /**
     * 当 SSO 授权 Activity 退出时，该函数被调用。
     * 以上三种授权需要在Activity的onActivityResult函数中，调用以下方法：
     * [Activity.onActivityResult]
     */
    fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        if (requestCode == 32973) {
            api.authorizeCallback(requestCode, resultCode, data)
        }
    }
}