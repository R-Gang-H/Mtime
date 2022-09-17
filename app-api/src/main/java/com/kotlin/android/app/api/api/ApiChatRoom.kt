package com.kotlin.android.app.api.api

import com.kotlin.android.api.ApiResponse
import com.kotlin.android.app.data.entity.chatroom.Token
import retrofit2.http.GET

/**
 * Created by zhaoninglongfei on 2021/11/23.
 * 聊天室相关
 */
interface ApiChatRoom {

    companion object{
        const val TOKEN = "/im/token.api" //获取登录认证 需要用户首先登录时光网
    }

    @GET(TOKEN)
    suspend fun getToken() : ApiResponse<Token>
}