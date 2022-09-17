package com.kotlin.android.audio.floatview.component.aduiofloat.bean

import com.kotlin.android.app.data.ProguardRule

/**
 * create by lushan on 2022/3/22
 * des:音频播放器
 **/
data class AudioBean(
    var contentId:Long = 0L,//资讯id
    var audioUrl:String = "",//音频地址
    var contentDes:String = "",//音频描述
    var audioImg:String = ""//音频图片
):ProguardRule