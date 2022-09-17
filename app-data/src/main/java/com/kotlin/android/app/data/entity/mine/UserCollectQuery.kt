package com.kotlin.android.app.data.entity.mine

import com.kotlin.android.app.data.ProguardRule

/**
 * 查询当前用户的收藏状态
 */
data class UserCollectQuery (
    var userId: Long ?= 0,          // 用户ID
    var objType: Long ?= 0,         // 收藏主体类型 MOVIE(1, "电影"), PERSON(2, "影人"),
                                    // CINEMA(3, "影院"), POST(4, "帖子"), ARTICLE(5, "文章");
    var objId: Long ?= 0,           // 收藏主体ID
    var isCollect: Boolean ?= false // 收藏与否
): ProguardRule