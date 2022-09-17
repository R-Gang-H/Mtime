package com.kotlin.android.share.entity

import com.kotlin.android.app.data.ProguardRule

/**
 * QQ授权登录成功返回response json实体
 *
 * Created on 2020/11/30.
 *
 * @author o.s
 *
 * {
"ret":0,
"openid":"182C1157E10CBA5E4C5174336CA82A7F",
"access_token":"863EA5DDC9DE75DC9F9DB19CE5476C85",
"pay_token":"7251B6D5AF9F8004EB446D2F37B27CD0",
"expires_in":7776000,
"code":"",
"proxy_code":"",
"proxy_expires_in":0,
"pf":"desktop_m_qq-10000144-android-2002-",
"pfkey":"43da7e0bc77e349157545942c2b341c2",
"msg":"",
"login_cost":31,
"query_authority_cost":-1154758313,
"authority_cost":0,
"expires_time":1614577301737
}
 */
data class QQAuthResponse(
        var ret: Long = 0,
        var openid: String? = null,
        var access_token: String? = null,
        var pay_token: String? = null,
        var expires_in: Long = 0,
        var code: String? = null,
        var proxy_code: String? = null,
        var proxy_expires_in: Long = 0,
        var pf: String? = null,
        var pfkey: String? = null,
        var msg: String? = null,
        var login_cost: Long = 0,
        var query_authority_cost: Long = 0,
        var authority_cost: Long = 0,
        var expires_time: Long = 0
) : ProguardRule
