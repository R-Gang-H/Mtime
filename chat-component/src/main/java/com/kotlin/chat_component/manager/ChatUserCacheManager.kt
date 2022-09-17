package com.kotlin.chat_component.manager

import com.kotlin.chat_component.model.MtimeUserSimple

/**
 * Created by zhaoninglongfei on 2021/12/2.
 * 环信聊天室 用户信息缓存
 */
object ChatUserCacheManager {

    const val KEY_IM_ID = "mtime_im_id"
    const val KEY_MTIME_ID = "mtime_user_id"
    const val KEY_NICKNAME = "mtime_user_nickname"
    const val KEY_AVATAR = "mtime_user_avatar"
    const val KEY_MTIME_AUTH_TYPE = "mtime_user_auth_type"
    const val KEY_MTIME_AUTH_ROLE = "mtime_user_auth_role"
    const val KEY_MTIME_ROLE = "role"  //"role":official

    //本地用 携带对方的用户信息
    const val KEY_MTIME_OTHER = "mtime_other_simple"

    private val map: HashMap<String, MtimeUserSimple> = hashMapOf()

    fun put(imId: String, user: MtimeUserSimple) {
        map[imId] = user
    }

    fun getMtimeUserSimple(imId: String?): MtimeUserSimple? {
        return map[imId]
    }
}