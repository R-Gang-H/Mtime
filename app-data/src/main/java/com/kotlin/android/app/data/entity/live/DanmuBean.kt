package com.kotlin.android.app.data.entity.live

import com.kotlin.android.app.data.ProguardRule
import java.io.Serializable

/**
 * create by lushan on 2021/3/16
 * description:弹幕实体
 */
data class DanmuBean(
        var content: String = "",
        var time:Long = 0L,
        var nickName:String = "",
        var userId:Long = 0L,
        var sex:Long = 0L,
        var isMine:Boolean = false
) : ProguardRule,Serializable