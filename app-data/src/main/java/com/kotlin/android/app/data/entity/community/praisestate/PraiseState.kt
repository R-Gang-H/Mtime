package com.kotlin.android.app.data.entity.community.praisestate

import com.kotlin.android.app.data.ProguardRule

/**
 * create by lushan on 2020/9/24
 * description:查询点赞点踩状态
 */
data class PraiseState(
        var currentUserPraise: Long? = 0L,//当前用户点赞、点踩状态：1.点赞 2.点踩 null无 -1：当前用户未登录
        var downCount: Long = 0L,//点踩数
        var objId: Long = 0L,//点赞主体对象id
        var objType: Long = 0L,//点赞主体类型
        var upCount: Long = 0L//点赞数
) : ProguardRule {
    companion object {
        const val CURRENT_USER_PRAISE_STAT_PRAISE = 1L
    }
}