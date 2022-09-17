package com.kotlin.android.app.data.entity.mine

import com.kotlin.android.app.data.ProguardRule

data class UserBindViewBean(
    var oauthApple: Boolean, //是否绑定苹果
    var oauthQQ: Boolean, //是否绑定qq
    var oauthSina: Boolean, //是否绑定新浪
    var oauthWechat: Boolean, //是否绑定微信
    var success: Boolean, //成功与否
    var error: String //提示信息
) : ProguardRule