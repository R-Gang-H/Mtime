package com.kotlin.android.app.data.entity

import com.kotlin.android.app.data.ProguardRule

/**
 * @author ZhouSuQiang
 * @email zhousuqiang@126.com
 * @date 2020/8/27
 *
 * 公共的带是否还有更多数据的列表UI实体
 */
data class CommHasMoreList<T>(
        var pageStamp: Long = 0,
        var hasMore: Boolean,
        var list: List<T>,
        var topList: List<T>? = null
) : ProguardRule