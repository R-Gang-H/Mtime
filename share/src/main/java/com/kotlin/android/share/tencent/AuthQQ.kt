package com.kotlin.android.share.tencent

import android.app.Activity
import android.content.Intent
import com.google.gson.Gson
import com.kotlin.android.data.auth.AuthLogin
import com.kotlin.android.mtime.ktx.ext.showToast
import com.kotlin.android.share.R
import com.kotlin.android.share.auth.AuthPlatform
import com.kotlin.android.share.auth.AuthState
import com.kotlin.android.share.entity.AuthEntity
import com.kotlin.android.share.entity.QQAuthResponse
import com.tencent.connect.common.Constants
import com.tencent.tauth.IUiListener
import com.tencent.tauth.Tencent
import com.tencent.tauth.UiError
import org.json.JSONObject

/**
 * QQ授权登录：
 *
 * Created on 2020/11/30.
 *
 * @author o.s
 */

class AuthQQ(
        private val activity: Activity,
        private val appId: String,
        private val authorities: String,
        private val scope: String,
        private val event: ((state: AuthEntity) -> Unit)? = null
) {
    private val mTencent by lazy { Tencent.createInstance(appId, activity.applicationContext, authorities) }

    private val uiListener = object : IUiListener {
        override fun onComplete(response: Any?) {
            showToast(R.string.auth_success)
            if (response is JSONObject) {
                val result = Gson().fromJson(response.toString(), QQAuthResponse::class.java)
//                result.d()
                event?.invoke(AuthEntity(
                        state = AuthState.SUCCESS,
                        platform = AuthPlatform.QQ,
                        qqAuthData = result,
                        authLogin = AuthLogin(
                                platformId = AuthPlatform.QQ.id,
                                accessToken = result.access_token,
                                qqExpiresIn = result.expires_in.toString()
                        )
                ))
            }
        }

        override fun onCancel() {
            showToast(R.string.auth_cancel)
            event?.invoke(AuthEntity(
                    state = AuthState.FAILURE,
                    platform = AuthPlatform.QQ
            ))
        }

        override fun onWarning(p0: Int) {
            showToast(R.string.auth_fail)
            event?.invoke(AuthEntity(
                    state = AuthState.FAILURE,
                    platform = AuthPlatform.QQ
            ))
        }

        override fun onError(e: UiError?) {
            showToast(R.string.auth_fail)
            event?.invoke(AuthEntity(
                    state = AuthState.FAILURE,
                    platform = AuthPlatform.QQ
            ))
        }
    }

    fun auth() {
        mTencent.login(activity, scope, uiListener)
//        "QQ :: isSessionValid=${mTencent.isSessionValid}, isSupportSSOLogin=${mTencent.isSupportSSOLogin(activity)}, isReady=${mTencent.isReady}, qqToken=${mTencent.qqToken}".d()
//        if (mTencent.isSessionValid) {
//        } else {
//            showToast("QQ授权异常！")
//        }
    }

    fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        if (requestCode == Constants.REQUEST_LOGIN) {//qq,requestCode=11101
            Tencent.onActivityResultData(requestCode, resultCode, data, uiListener)
        }
    }
}