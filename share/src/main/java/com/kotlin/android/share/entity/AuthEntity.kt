package com.kotlin.android.share.entity

import com.kotlin.android.data.auth.AuthLogin
import com.kotlin.android.app.data.ProguardRule
import com.kotlin.android.share.auth.AuthPlatform
import com.kotlin.android.share.auth.AuthState
import com.sina.weibo.sdk.auth.Oauth2AccessToken

/**
 * 授权返回实体
 * 使用 [authLogin] 数据请求第三方授权登录接口(user/oauth/login.api）
 *
 * Created on 2020/11/30.
 *
 * @author o.s
 */
data class AuthEntity(
        val state: AuthState,
        val platform: AuthPlatform,
        var authLogin: AuthLogin? = null,
        val wxCode: String? = null,
        val qqAuthData: QQAuthResponse? = null,
        val wbAuthData: Oauth2AccessToken? = null
) : ProguardRule