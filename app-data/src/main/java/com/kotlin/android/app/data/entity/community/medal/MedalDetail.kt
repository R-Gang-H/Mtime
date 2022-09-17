package com.kotlin.android.app.data.entity.community.medal

import com.kotlin.android.app.data.ProguardRule

data class MedalDetail(
    val medalName: String? = "",
    val typeName: String? = "",
    val count: Long? = 0L,
    val appLogoUrl: String? = "",
    val finishNum: Long? = 0L,
    val pcLogoUrl: String? = "",
    val receiveCount: Long? = 0L,
    val details: String? = "",
    val theme: Long? = 0L,
    val startTime: StartTime? = null,
    val endTime: StartTime? = null,
    val factor: String? = "",
    val finishFactor: String? = "",
    val status: Long? = 0L,
    val receiveTime: StartTime? = null
) : ProguardRule {

    data class StartTime(val show: String? = "", val stamp: Long? = 0L) : ProguardRule

}