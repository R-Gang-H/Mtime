package com.kotlin.android.app.data.entity.live

import com.kotlin.android.app.data.ProguardRule

/**
 * @author ZhouSuQiang
 * @email zhousuqiang@126.com
 * @date 2021/3/8
 *
 * 直播预约结果
 */
data class LiveAppointResult(var dataMsg: String? = "",
                             var appointCount: Long = 0L) : ProguardRule {
}