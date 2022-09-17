package com.kotlin.android.app.data.entity

import com.kotlin.android.app.data.ProguardRule

/**
 * @author ZhouSuQiang
 * @email zhousuqiang@126.com
 * @date 2020/8/25
 *
 * 社区内容公共API实体（日志、帖子、文章、影评）列表
 */
data class CommContentList(
        var items: List<CommContent>?,
        var nextStamp: String = "",
        var hasNext: Boolean = true,
        var totalCount: Long = 0//总数量
) : ProguardRule