package com.kotlin.android.app.data.entity.common

import com.kotlin.android.app.data.ProguardRule

/**
 *
 * Created on 2020/10/22.
 *
 * @author o.s
 */
open class StatusResult(
        var status: Long = 0, //状态值：0失败 1成功
        var statusMsg: String? = ""
): ProguardRule