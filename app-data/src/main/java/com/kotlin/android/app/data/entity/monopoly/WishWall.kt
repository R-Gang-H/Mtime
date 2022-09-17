package com.kotlin.android.app.data.entity.monopoly

import com.kotlin.android.app.data.ProguardRule

/**
 * 许愿墙信息
 * Created on 2021-07-14 11:04:18.
 * @author zhangjian
 */
data class WishWall(
    var hasMore: Boolean? = false, // 是否更多
    var totalCount: Long = 0, // 总条数
    var wishList: List<WishInfo?>? = null
) : ProguardRule