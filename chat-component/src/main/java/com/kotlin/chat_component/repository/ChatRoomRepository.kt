package com.kotlin.chat_component.repository

import com.kotlin.android.api.ApiResult
import com.kotlin.android.app.api.base.BaseRepository
import com.kotlin.android.app.data.entity.chatroom.Token

/**
 * Created by zhaoninglongfei on 2021/11/23.
 * 聊天室相关
 */
class ChatRoomRepository : BaseRepository() {

    suspend fun getToken(): ApiResult<Token> {
        return request {
            apiMTime.getToken()
        }
    }
}