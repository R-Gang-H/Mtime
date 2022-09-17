package com.kotlin.android.share

import com.tencent.connect.common.Constants

/**
 *
 * Created on 2020/6/22.
 *
 * @author o.s
 */
object ShareEnv {

    /**
     * APP_AUTHORITIES FileProvider
     */
    const val APP_AUTHORITIES = "com.mtime.fileprovider"

    /**
     * Tencent QQ
     */
    const val APP_ID_TENCENT = "100838835"
    const val APP_KEY_TENCENT = "4482cf02941a988160cdd199d54c28fc"

    /**
     * WX 微信
     */
    const val APP_ID_WX = "wx839739a08ff78016"
    const val APP_SECRET_WX = "d26bf53be1cae30a469920407c78f329"

    /**
     * WB 微博
     */
    const val APP_ID_WB = "1100590649" // AppKey
    const val APP_SECRET_WB = "e5cb194d2d72e850034a21fef24353f6"
    /**
     * 第三方应用可以使用自己的回调页。
     *
     * <p>
     * 注：关于授权回调页对移动客户端应用来说对用户是不可见的，所以定义为何种形式都将不影响，
     * 但是没有定义将无法使用 SDK 认证登录。
     * 建议使用默认回调页：https://api.weibo.com/oauth2/default.html
     * </p>
     */
    const val REDIRECT_URL = "http://www.mtime.com"

    /**
     * 【必须】微信授权后重定向的回调链接地址（我们前面申请的）
     */
    const val WX_REDIRECT_URL = "http://www.mtime.com"

    /**
     * Scope 是 OAuth2.0 授权机制中 authorize 接口的一个参数。通过 Scope，平台将开放更多的微博
     * 核心功能给开发者，同时也加强用户隐私保护，提升了用户体验，用户在新 OAuth2.0 授权页中有权利
     * 选择赋予应用的功能。
     *
     * 我们通过新浪微博开放平台-->管理中心-->我的应用-->接口管理处，能看到我们目前已有哪些接口的
     * 使用权限，高级权限需要进行申请。
     *
     * 目前 Scope 支持传入多个 Scope 权限，用逗号分隔。
     *
     * 有关哪些 OpenAPI 需要权限申请，请查看：http://open.weibo.com/wiki/%E5%BE%AE%E5%8D%9AAPI
     * 关于 Scope 概念及注意事项，请查看：http://open.weibo.com/wiki/Scope
     */
    const val SCOPE = ("email,direct_messages_read,direct_messages_write,"
            + "friendships_groups_read,friendships_groups_write,statuses_to_me_read,"
            + "follow_app_official_microblog," + "invitation_write")

    /**
     * 【必须】微信授权读取用户信息
     * 应用授权作用域，
     * snsapi_base （不弹出授权页面，直接跳转，只能获取用户openid），
     * snsapi_userinfo （弹出授权页面，可通过openid拿到昵称、性别、所在地。并且，即使在未关注的情况下，只要用户授权，也能获取其信息）
     */
    const val WX_SCOPE = "snsapi_userinfo"

    /**
     * QQ登录：应用需要获得哪些接口的权限，由“，”分隔。例如：SCOPE = “get_simple_userinfo,add_topic”；所有权限用“all”
     */
    const val QQ_SCOPE = "get_simple_userinfo"

    /**
     * 【可选】微信重定向后会带上state参数，开发者可以填写a-zA-Z0-9的参数值，最多128字节，该值会被微信原样返回，我们可以将其进行比对，防止别人的攻击。
     * 该参数可用于防止csrf攻击（跨站请求伪造攻击），建议第三方带上该参数，可设置为简单的随机数加session进行校验
     */
    const val WX_STATE = "mtime_wxapi_login"

    /**
     * APK pkgName
     */
    const val PACKAGE_WX = "com.tencent.mm"
    const val PACKAGE_QQ = Constants.PACKAGE_QQ //"com.tencent.mobileqq"
    const val PACKAGE_WB = "com.sina.weibo"

    /**
     * 文件
     */
    const val PATH_DOCUMENT = "document"
}