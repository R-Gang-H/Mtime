package com.kotlin.android.app.data.entity.toplist

import com.kotlin.android.app.data.ProguardRule

/**
 * Created by vivian.wei on 2020-09-01
 * 榜单影人实体
 */
data class TopListPersonInfo(
         var personId: Long ?= 0,         // 影人ID（等于ItemId）
         var personName: String ?= "",    // 影人名
         var personNameEn: String ?= "",  // 影人名（英文）
         var img: String ?= "",           // 影人图片地址url
         var score: String ?= "",         // 评分，即喜爱度 （"89%"，也可能没有）
         var profession: String ?= "",    // 主要职业
         var relatedMovie: String ?= "",  // 代表作("《复仇者联盟》")
         var award: String ?= "",         // 主要奖项
): ProguardRule {

    // 是否有评分
    fun hasScore(): Boolean {
        score?.let {
            return it.isNotEmpty()
        }
        return false
    }

}