package com.kotlin.chat_component.model

import com.kotlin.android.app.data.ProguardRule
import org.json.JSONObject
import java.io.Serializable

/**
 * Created by zhaoninglongfei on 2021/12/2.
 * 时光用户聊天信息
 */
data class MtimeUserSimple(
    val imId: String?,
    val mtimeId: Long?,
    val nickName: String? = null,//昵称
    val headPic: String? = null,//头像
    val authType: Long?,
    val authRole: String?,
    val isOfficial: Boolean = false
) : ProguardRule, Serializable {

    companion object {
        fun fromJSONObject(jsonObj: JSONObject): MtimeUserSimple {
            return MtimeUserSimple(
                imId = jsonObj.getString("imId"),
                mtimeId = jsonObj.getLong("mtimeId"),
                nickName = jsonObj.getString("nickName"),
                headPic = jsonObj.getString("headPic"),
                authType = jsonObj.getLong("authType"),
                authRole = jsonObj.getString("authRole"),
            )
        }
    }

    fun toJsonObject(): JSONObject {
        return JSONObject().apply {
            put("imId", imId)
            put("mtimeId", mtimeId)
            put("nickName", nickName)
            put("headPic", headPic)
            put("authType", authType)
            put("authRole", authRole)
            put("isOfficial", isOfficial)
        }
    }
}
