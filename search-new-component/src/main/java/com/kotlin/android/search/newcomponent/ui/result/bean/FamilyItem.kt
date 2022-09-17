package com.kotlin.android.search.newcomponent.ui.result.bean

import com.kotlin.android.app.data.ProguardRule

/**
 * 搜索结果页显示用家族数据
 */
data class FamilyItem(
    var familyId: Long = 0L,           // 家族id
    var name: String = "",             // 名称
    var imageUrl: String = "",         // 图片地址
    var summary: String = "",          // 简介
    var memberNum: Long = 0L,          // 成员数
    var isJoin: Long = 0L,             // 当前用户是否加入此家族：0:未加入 1：已加入成功 2 加入中（待审核） 3 黑名单人员
    var showIsJoin: Boolean = false,   // 是否展示是否加入
): ProguardRule {

    companion object {
        // 当前用户是否加入此家族
        // 页面判断与社区保持一致：0:未加入 1：已加入成功 2 加入中（待审核） 3 黑名单人员
        const val JOIN_STATUS_NOT = 0L      // 未加入
        const val JOIN_STATUS_JOINED = 1L   // 已加入
        const val JOIN_STATUS_JOINING = 2L  // 加入中
        const val JOIN_STATUS_BLACK_LIST = 3L    // 黑名单
    }

}
