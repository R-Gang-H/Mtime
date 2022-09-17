package com.kotlin.android.data.auth

import com.kotlin.android.app.data.ProguardRule

/**
 * UserController - 第三方授权登录接口(user/oauth/login.api）
 *
 * accessToken	    String  第三授权成功口令
 * platformId	    Number  授权平台，1 新浪微博、2 qq、4微信、6苹果apple
 * qqExpiresIn	    String  qq授权失效时间，时间戳，单位秒。
 * code	            String  授权code，目前仅用于微信、苹果
 * mobileToken	    String  手机号验证通过凭证 强制绑定手机必填
 * password	        String  设置密码使用 强制绑定手机且没有密码必填
 * confirmPassword	String  设置密码，确认使用 强制绑定手机且没有密码必填
 *
 * Created on 2020/11/30.
 *
 * @author o.s
 */
data class AuthLogin(
        var accessToken: String? = null, // 第三授权成功口令
        var platformId: Int? = null, // 授权平台，1 新浪微博、2 qq、4微信、6苹果apple
        var qqExpiresIn: String? = null, // qq授权失效时间，时间戳，单位秒。
        var code: String? = null, // 授权code，目前仅用于微信、苹果
        var mobileToken: String? = null, // 手机号验证通过凭证 强制绑定手机必填
        var password: String? = null, // 设置密码使用 强制绑定手机且没有密码必填
        var confirmPassword: String? = null, // 设置密码，确认使用 强制绑定手机且没有密码必填
) : ProguardRule
