package com.kotlin.android.share.auth

import android.app.Activity
import android.content.Intent
import com.kotlin.android.share.ShareEnv
import com.kotlin.android.share.entity.AuthEntity
import com.kotlin.android.share.sina.AuthWB
import com.kotlin.android.share.tencent.AuthQQ
import com.kotlin.android.share.wx.AuthWX

/**
 * AUTH 授权管理器：
 * 暂未启用
 *
 * Created on 2020/7/8.
 *
 * @author o.s
 */
object AuthManager {
    private var authWB: AuthWB? = null
    private var authWX: AuthWX? = null
    private var authQQ: AuthQQ? = null

    fun install(
            activity: Activity,
            event: ((state: AuthEntity) -> Unit)? = null
    ) {
        authWB = AuthWB(activity, ShareEnv.APP_ID_WB, ShareEnv.REDIRECT_URL, ShareEnv.SCOPE, event)
        authWX = AuthWX(activity, ShareEnv.APP_ID_WX, ShareEnv.WX_SCOPE, ShareEnv.WX_STATE)
        AuthWX.authState = event
        authQQ = AuthQQ(activity, ShareEnv.APP_ID_TENCENT, ShareEnv.APP_AUTHORITIES, ShareEnv.QQ_SCOPE, event)
    }

    fun auth(platform: AuthPlatform) {
        when (platform) {
            AuthPlatform.WE_CHAT -> {
                authWX?.auth()
            }
            AuthPlatform.WEI_BO -> {
                authWB?.auth()
            }
            AuthPlatform.QQ -> {
                authQQ?.auth()
            }
        }
    }

    fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        authWB?.onActivityResult(requestCode, resultCode, data)
        authQQ?.onActivityResult(requestCode, resultCode, data)
    }
}

enum class AuthState {
    SUCCESS,
    FAILURE,
    CANCEL
}