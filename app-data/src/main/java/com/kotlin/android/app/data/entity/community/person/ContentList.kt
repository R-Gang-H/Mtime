package com.kotlin.android.app.data.entity.community.person

import com.kotlin.android.app.data.entity.CommContent
import com.kotlin.android.app.data.ProguardRule

/**
 * @author Wangwei
 * @date 2020/9/23
 *
 * 社区个人主页 内容 实体
 */
data class ContentList(
        var hasNext: Boolean,
        var totalCount:Long,
        var pageIndex:Long,
        var pageSize:Long,
        var nextStamp:String?,
        var items: List<CommContent>?
) : ProguardRule {

    data class RcmdSelection(
            var rcmdText: String?,
            var rcmdTop: Boolean,
            var commContent: CommContent
    ) : ProguardRule
}