package com.kotlin.android.app.data.entity.mine

import com.kotlin.android.app.data.ProguardRule

/**
 * create by lushan on 2020/9/14
 * description:收藏实体类
 */
open class CollectionBase : ProguardRule {
    var hasNext: Boolean = true//是否还有下一页
    var pageIndex: Long = 0L//分页index
    var pageSize: Long = 0L//分页size
    var totalCount: Long = 0L//总条数

    var nextStamp: String = ""//分页index
}