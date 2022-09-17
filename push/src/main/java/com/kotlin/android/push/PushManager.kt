package com.kotlin.android.push

import android.content.Context
import cn.jpush.android.api.JPushInterface

/**
 * Created by zhaoninglongfei on 2022/2/15
 *
 */
class PushManager private constructor() {

    companion object {
        val instance by lazy { PushManager() }
    }


    fun initPush(applicationContext: Context, isDebug: Boolean){
        JPushInterface.setDebugMode(isDebug)
        JPushInterface.init(applicationContext)
    }


}