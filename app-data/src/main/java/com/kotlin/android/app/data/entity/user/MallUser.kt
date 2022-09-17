package com.kotlin.android.app.data.entity.user

import com.kotlin.android.app.data.ProguardRule

/**
 * 商城用户
 * 参见：MallPayUserBean
 *
 * Created on 2020/8/14.
 *
 * @author o.s
 */
data class MallUser(
        var balance: Double = 0.0,
        val bindMobile: String = "",
        val headPic: String = "",
        val isVirtualUser: Boolean = false,
        val nickname: String = "",
        val rechargeMax: Long = 0,
        val sex: Int = 0,
        val userId: Long = 0,
        val mobile: String? = null, //用户注册手机号
        val hasBindedMobile: Boolean = false // true:需要， false:不需要
) : ProguardRule