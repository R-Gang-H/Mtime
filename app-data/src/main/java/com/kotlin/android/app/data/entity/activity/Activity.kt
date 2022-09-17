package com.kotlin.android.app.data.entity.activity

import com.kotlin.android.app.data.ProguardRule

/**
 * 创建者: vivian.wei
 * 创建时间: 2022/3/17
 * 描述: 活动
 */
data class Activity(
    var introId: Long? = null,              // 活动ID
    var name: String? = null,               // 活动名称
    var placardUrl: String? = null,         // 活动海报
    var appSkip: AppSkip? = null,	        // 相关链接
): ProguardRule {

    data class AppSkip(
            var type: Long? = 0L,           // 相关链接-下拉框类型，1：H5、2：AppLink
            var appLink:String? = null,     // 相关链接
    ): ProguardRule

}
