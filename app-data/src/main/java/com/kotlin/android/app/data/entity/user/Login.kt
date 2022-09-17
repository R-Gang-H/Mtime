package com.kotlin.android.app.data.entity.user

import com.kotlin.android.app.data.ProguardRule

/**
 * 登录响应实体，对应下面4个登录接口:
 *
 * <1> UserController - 用户登录接口(user/login.api）
 *
 * <2> UserController - 第三方授权登录接口(user/oauth/login.api）
 *
 * <3> UserController - 第三方授权H5回调登录接口(user/authWap/login.api ）
 *
 * <4> VerifyLoginController - 极光APP三方一键登录（/verify_jpush_login.api）
 *
 * Created on 2021/4/8.
 *
 * @author o.s
 */
data class Login(
        /**
         * <1> UserController - 用户登录接口(user/login.api）:
         *      status:
         *      1 登录成功、2 账号或密码有误，请重试、3 该账号已被禁止登录、4 请输入图片验证码、5 强制绑定手机
         *
         * <2> UserController - 第三方授权登录接口(user/oauth/login.api）
         *      status:
         *      1登录成功、即根据参数的accessToken（或者微信code）获取到有效的第三方授权信息，并可以据此获取到有效的时光用户信息，并且无需手机号绑定密码设置/或者手机号绑定密码设置成功
         *      2 根据第三方授权信息没有获取到相应的时光用户信息，返回【没有授权注册】错误、 -- 应通过/user/login.api进行第三方授权和时光用户信息的绑定
         *      3 根据参数的accessToken（或者微信code）无法获取到有效的第三方授权信息，返回【授权失败】错误、
         *      4 如果推荐位配置为需要强制绑定，该用户没有手机号，返回【强制绑定】错误、
         *      5 绑定手机号失败、这个值不会返回，只在/user/login.api中有这个返回值
         *      6 根据第三方授权信息可以获取到相应的时光用户信息，但账号被封禁，返回【该账号已被禁止登录】错误、
         *      7 mobileToken不为空，参数传入了密码，时光用户也确实还没有密码，但是，传入的password和confirmPassword不一样，返回【参数中的密码不一致】错误、
         *      8 mobileToken不为空，参数传入了password，但实际上时光用户已经有了密码了，返回【密码已存在】错误、
         *      9 mobileToken不为空，参数没有传入password，但是时光用户目前没有密码，返回【需要设置密码】错误、
         *      10 mobileToken不为空，参数password和confirmPassword也没问题，但是后台服务返回错误码，则返回【绑定手机号和设置密码失败】错误 0 其他不可预料的错误
         *
         * <3> UserController - 第三方授权H5回调登录接口(user/authWap/login.api ）
         *      status:
         *      1 登录成功、2 授权登录失败、3 授权值不能为空
         *
         * <4> VerifyLoginController - 极光APP三方一键登录（/verify_jpush_login.api）
         *      status:
         *      1 登录成功、2 账号或密码有误，请重试、3 该账号已被禁止登录、4 请输入图片验证码、5 强制绑定手机、6 绑定手机出错、7 第三方授权失败
         */
        var status: Long, //
        var msg: String, // 消息
        var serviceEmail: String, // 客服邮箱
        var codeId: String, // 验证码编号
        var codeUrl: String, // 验证码
        var skipBindMobileText: String, // 跳过强制绑定手机号的文案，如果没有文案就是强制绑定。注：目前此字段永远为空，永远强制绑定手机号
        var needBindMobile: Boolean, // 是否绑定手机号
        var hasPassword: Boolean, // 是否有密码
        var user: ItemUser, // 用户信息

        // 第三方授权登录接口(user/oauth/login.api）新增字段
        var platformId: Long, // 1 新浪微博、2 qq、4微信 、6苹果apple
        var token: String, // 第三方授权口令,用于注册，创建用户时授权的凭证。
) : ProguardRule

/**
status	        Number  1 登录成功、2 账号或密码有误，请重试、3 该账号已被禁止登录、4 请输入图片验证码、5 强制绑定手机、6 绑定手机出错、7 第三方授权失败
msg	            String  消息
serviceEmail    String  客服邮箱
codeId	        String  验证码编号
codeUrl         String  验证码
skipBindMobileText	String  跳过强制绑定手机号的文案，如果没有文案就是强制绑定。注：目前此字段永远为空，永远强制绑定手机号
needBindMobile	Boolean 是否绑定手机号
hasPassword	    Boolean 是否有密码
user	        Object  用户信息
        userId	    Number  用户ID
        nickname	String  昵称
        headImg	    String  头像
        mobile      String  手机
        dataEncryption	String  userId加密
        gender      Number  性别 1 男 、2 女、 3 保密

// 第三方授权登录接口(user/oauth/login.api）
platformId	    Number  1 新浪微博、2 qq、4微信 、6苹果apple
token	        String  第三方授权口令,用于注册，创建用户时授权的凭证。
 */