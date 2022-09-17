package com.kotlin.android.app.data.entity.live

import com.kotlin.android.app.data.ProguardRule
import org.json.JSONObject

/**
 * Created by zhaoninglongfei on 2021/12/8.
 *
 */
data class HuanxinLiveCommand(
        var cmdCode: Long,
        var roomNum: String?,
        var cmdParams: ArrayList<JSONObject>?
) : ProguardRule
