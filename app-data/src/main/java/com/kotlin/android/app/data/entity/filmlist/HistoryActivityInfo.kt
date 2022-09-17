package com.kotlin.android.app.data.entity.filmlist

import com.kotlin.android.app.data.ProguardRule
 /**
  * 创建者: SunHao
  * 创建时间: 2022/3/25
  * 描述:历史投稿
  **/
data class HistoryActivityInfo(
    val nextStamp: String? = "",//下一页标识
    val hasNext: Boolean? = false,//是否有下一页
    val activitys: MutableList<Activitys>? = mutableListOf()//活动信息
) : ProguardRule