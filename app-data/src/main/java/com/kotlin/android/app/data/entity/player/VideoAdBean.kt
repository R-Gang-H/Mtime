package com.kotlin.android.app.data.entity.player

import com.kotlin.android.app.data.ProguardRule

/**
 * create by lushan on 2020/8/31
 * description:视频广告
 */
data class VideoAdBean(var daList:List<AdItem>? = mutableListOf()) : ProguardRule {
    data class AdItem(
            var aID:Long = 0L,//广告id
            var title:String? = "",//广告标题
            var duration:Long = 0L,//广告持续时间，单位：秒
            var isShowTag:Boolean = false,//是否显示广告标签
            var tagDesc:String? = "",//广告标签描述
            var image:String? = "",//图片链接地址
            var applinkData:String? = ""//applink通用规则
    ): ProguardRule
}