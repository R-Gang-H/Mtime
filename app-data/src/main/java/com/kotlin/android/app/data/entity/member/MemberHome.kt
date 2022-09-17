package com.kotlin.android.app.data.entity.member

import com.kotlin.android.app.data.ProguardRule

/**
 * create by lushan on 2020/11/11
 * description:会员中心首页
 */
data class MemberHome(
    var growUp: GrowUp? = null,//成长攻略
    var personal: Personal? = null//会员个人基本信息
): ProguardRule {


    data class GrowUp(
            var experience: Long = 0L,//经验
            var experienceGroupUpUrl: String? = "",//经验值详情页
            var experienceUrl: String? = "",//经验值详情页,升级攻略
            var growUpDesc: String = "",//成长描述
            var mtimeCoin: Long = 0L,//时光币
            var mtimeCoinUrl: String? = ""//时光币详情页
    ): ProguardRule


    data class Personal(
            var headImg: String? = "",//会员头像
            var level: Long = 0L,//等级
            var levelDescCn: String? = "",//会员等级中文名
            var levelDescEn: String? = "",//会员等级英文名
            var name: String? = "",//会员名称
            var registerTimeDesc: String? = "",//注册时长展示
            var rightUrl: String = ""//会员权益页
    ): ProguardRule

//    data class (
//            var name: String = "",
//            var tips: String = "",
//            var type: Int = 0
//    )
}

