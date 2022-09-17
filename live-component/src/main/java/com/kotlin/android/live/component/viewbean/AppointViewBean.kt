package com.kotlin.android.live.component.viewbean

import com.kotlin.android.app.data.ProguardRule

/**
 * create by lushan on 2021/2/26
 * description: 直播预约页面显示实体类
 */
data class AppointViewBean(var liveDate: String? = "",//直播开始时间
                           var appointState: Long = 0L,//预约状态
                           var appointPersonNum: Long = 0L//预约人数
) : ProguardRule