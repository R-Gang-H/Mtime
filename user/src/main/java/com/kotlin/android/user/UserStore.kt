package com.kotlin.android.user

import android.content.Context
import android.text.TextUtils
import com.google.gson.Gson
import com.kotlin.android.core.CoreApp
import com.kotlin.android.ktx.ext.store.getSpValue
import com.kotlin.android.ktx.ext.log.i
import com.kotlin.android.ktx.ext.store.putSpValue
import com.kotlin.android.ktx.ext.store.removeKey
import com.kotlin.android.app.data.entity.user.User
import java.lang.Exception

/**
 * 用户存储
 *
 * Created on 2020/8/14.
 *
 * @author o.s
 */
class UserStore private constructor(private val context: Context) {
    val userPrefsName = "user_prefs"
    private val keyUserInfo = "userInfo"

    companion object {
        var context: Context = CoreApp.instance
        val instance by lazy { UserStore(context) }
    }

    /**
     * 保存用户信息
     */
    fun saveUser(user: User?) {
        user?.apply {
            val userInfo = Gson().toJson(this)
            "saveUser userInfo = $userInfo".i()
            context.putSpValue(keyUserInfo, userInfo, userPrefsName)
        }
    }

    /**
     * 获取用户信息
     */
    fun getUser(): User? {
        var user: User? = null
        try {
            val userInfo = context.getSpValue(keyUserInfo, "", userPrefsName)
            if (!TextUtils.isEmpty(userInfo)) {
                user = Gson().fromJson(userInfo, User::class.java)
            }
            //"getUser user = $user".i()
        } catch (e: Exception) {
            e.printStackTrace()
        } finally {
            return user
        }
    }

    fun clear() {
        context.removeKey(keyUserInfo, userPrefsName)
    }
}