package com.kotlin.android.player.bean

import com.kotlin.android.app.data.ProguardRule
import java.io.Serializable

/**
 * create by lushan on 2021/3/10
 * description:直播状态下状态改变
 */
data class LiveVideoData(
        var liveStatus: Long = 0L,//直播状态
        var onLineNum: Long = 0L,//直播观看人数
        var title:String = ""//直播标题
) : ProguardRule,Serializable