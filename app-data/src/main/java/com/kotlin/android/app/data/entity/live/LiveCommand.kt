package com.kotlin.android.app.data.entity.live

import com.kotlin.android.app.data.ProguardRule
import org.json.JSONObject

/**
 * @author ZhouSuQiang
 * @email zhousuqiang@126.com
 * @date 2021/3/16
 *
 */
data class LiveCommand(
        var cmdCode: Int,
        var roomNum: String?,
        var cmdParams: JSONObject?
) : ProguardRule
