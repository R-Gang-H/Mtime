package com.kotlin.android.app.data.entity.user

import com.kotlin.android.app.data.ProguardRule
import java.io.Serializable

/**
 * 切换登录用户
 *
 * Created on 2020/8/14.
 *
 * @author o.s
 */
data class SwitchUser(
        var success: Boolean = false,
        val error: String? = null,
        val userId: Long = 0,
        val userLevel: Int = 0,
        val nickname: String? = null,
        val headPic: String? = null,
        val mobile: String? = null,
        val sex: Int = 0,
        val balance: Double = 0.0,
        val isVirtualUser: Boolean = false,
        val hasBindedMobile: Boolean = false, // true:需要， false:不需要
        val birthday: String? = null,
        val location: UserLocation? = null // 居住地
) : Serializable, ProguardRule