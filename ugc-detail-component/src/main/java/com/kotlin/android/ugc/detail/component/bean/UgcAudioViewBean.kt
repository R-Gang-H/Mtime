package com.kotlin.android.ugc.detail.component.bean

import com.kotlin.android.app.data.ProguardRule

/**
 * create by lushan on 2022/3/23
 * des:音频详情
 **/
data class UgcAudioViewBean(
    var contentId:Long = 0L,
    var audioImg:String = "",//音频封面图
    var audioUrl:String = "",//音频地址
    var des:String = ""//资讯标题

):ProguardRule