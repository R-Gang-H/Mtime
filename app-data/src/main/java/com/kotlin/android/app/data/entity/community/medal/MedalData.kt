package com.kotlin.android.app.data.entity.community.medal

import com.kotlin.android.app.data.ProguardRule

data class MedalData(
    val ongoingMedalInfos: List<MedalInfos>?, //已完成勋章集合
    val completeMedalInfos: List<MedalInfos>?, //未完成勋章集合
    val userId: Long? = 0L, //创作者用户ID
    val medalCount: Long? = 0L //创作者勋章数
) : ProguardRule {

    data class MedalInfos(
        val medalName: String? = "", //勋章名称
        val pcLogoUrl: String? = "", //PC图标
        val typeName: String? = "", //类型名称
        val appLogoUrl: String? = "", //APP图标
        val details: String? = "", //描述
        val medalId: Long = 0L //勋章id
    ) : ProguardRule

}