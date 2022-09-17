package com.kotlin.android.app.data.entity.toplist

import com.kotlin.android.app.data.ProguardRule

/**
 * @author vivian.wei
 * @date 2020/8/18
 * @desc 游戏榜单详情
 */
data class GameTopList(
        var rankType: Long ?= 0,        //榜单类型：1昨日道具狂人，2昨日衰人，3昨日交易达人，
                                        //         4昨日收藏大玩家，5金币大富翁，6套装组合狂
        var rankName: String ?= "",     // 榜单名称
        var rankDesc: String ?= "",     // 榜单描述
        var totalCount: Long ?= 0,      // 榜单人数
        var userList: List<GameRankUser> ?= null   // 用户列表
): ProguardRule