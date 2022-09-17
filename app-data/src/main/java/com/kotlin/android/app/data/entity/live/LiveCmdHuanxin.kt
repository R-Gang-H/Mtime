package com.kotlin.android.app.data.entity.live

import com.google.gson.Gson
import com.kotlin.android.app.data.ProguardRule
import org.json.JSONObject

/**
 * @author ZhouSuQiang
 * @email zhousuqiang@126.com
 * @date 2021/3/16
 *
 */
data class LiveCmdHuanxin(
    var cmdCode: Int,
    var roomNum: String?,
    var cmdParams: HashMap<String, Any?>? = hashMapOf()
) : ProguardRule {
    fun toLiveCommand(): LiveCommand {
        return LiveCommand(
            this.cmdCode,
            this.roomNum.orEmpty(),
            JSONObject(Gson().toJson(this.cmdParams))
        )
    }
}

