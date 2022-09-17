package com.kotlin.android.app.data.entity.toplist

import com.kotlin.android.app.data.ProguardRule

/**
 * Created by mtime on 2020-09-15
 * App首页-游戏榜单列表
 */
data class IndexAppGameTopList(
        var list: List<GameTopList> ?= null  // 游戏榜单列表
): ProguardRule