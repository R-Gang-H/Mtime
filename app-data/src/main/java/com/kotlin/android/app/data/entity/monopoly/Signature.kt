package com.kotlin.android.app.data.entity.monopoly

import com.kotlin.android.app.data.ProguardRule

/**
 * 签名档
 *
 * Created on 2020/9/28.
 *
 * @author o.s
 */
data class Signature(
        var userInfo: UserInfo? = null, // 用户信息
        var signature: String? = null // 签名档
) : ProguardRule