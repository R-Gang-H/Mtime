package com.kotlin.chat_component.manager

import android.content.Context
import android.text.TextUtils
import com.google.gson.Gson
import com.kotlin.android.core.CoreApp
import com.kotlin.android.app.data.entity.chatroom.Token
import com.kotlin.android.ktx.ext.store.getSpValue
import com.kotlin.android.ktx.ext.store.putSpValue
import com.kotlin.android.ktx.ext.store.removeKey

/**
 * Created by zhaoninglongfei on 2021/11/29.
 *
 */
class HuanxinTokenStore private constructor() {
    private val tokenPrefsName = "ease_token_prefs"
    private val keyToken = "ease_token"
    private var context: Context = CoreApp.instance

    companion object {
        val instance by lazy { HuanxinTokenStore() }
    }

    fun saveToken(token: Token) {
        val tokenInfo = Gson().toJson(token)
        context.putSpValue(keyToken, tokenInfo, tokenPrefsName)
    }

    fun getToken(): Token? {
        var token: Token? = null
        try {
            val tokenInfo = context.getSpValue(keyToken, "", tokenPrefsName)
            if (!TextUtils.isEmpty(tokenInfo)) {
                token = Gson().fromJson(tokenInfo, Token::class.java)
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
        return token
    }

    fun clearToken(){
        context.removeKey(keyToken, tokenPrefsName)
    }
}