package com.kotlin.android.app.data.entity.chatroom

import com.kotlin.android.app.data.ProguardRule

/**
 * Created by zhaoninglongfei on 2021/11/23.
 *
 */
data class Token(
    var bizCode: Int? = null,
    var err: String? = null,
    var bizData: EaseClient? = null
) : ProguardRule


//环信客户端 client
data class EaseClient(
    var userId: Long? = null,
    var imId: String? = null,//imId 也是环信的username
    var token: String? = null,//im token
    var expire: String? = null // 到期时间
) : ProguardRule